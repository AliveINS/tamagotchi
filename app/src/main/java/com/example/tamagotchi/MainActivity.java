package com.example.tamagotchi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "pet_notifications";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private PetRepository petRepository;
    private Pet pet;
    private Handler handler;
    private Runnable updateRunnable;

    private TextView petNameTextView;
    private ImageView petImageView;
    private ImageView statusImageView;
    private TextView hungerTextView;
    private TextView happinessTextView;
    private TextView healthTextView;
    private Button feedButton;
    private Button playButton;
    private Button healButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petRepository = new PetRepository(this);
        pet = petRepository.getPet();

        petNameTextView = findViewById(R.id.headerTextView);
        petImageView = findViewById(R.id.petImageView);
        statusImageView = findViewById(R.id.statusImageView);
        hungerTextView = findViewById(R.id.hungerTextView);
        happinessTextView = findViewById(R.id.happinessTextView);
        healthTextView = findViewById(R.id.healthTextView);
        feedButton = findViewById(R.id.feedButton);
        playButton = findViewById(R.id.playButton);
        healButton = findViewById(R.id.healButton);

        createNotificationChannel();
        checkNotificationPermission();

        if (pet != null) {
            petNameTextView.setText(pet.name);
            petImageView.setImageResource(pet.imageResId);
            petImageView.getLayoutParams().width = 350;
            petImageView.getLayoutParams().height = 350;
            updateUI();
        }

        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pet != null) {
                    pet.hunger += 10;
                    if (pet.hunger > 100) pet.hunger = 100;
                    petRepository.updatePet(pet);
                    updateUI();
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pet != null) {
                    pet.happiness += 10;
                    if (pet.happiness > 100) pet.happiness = 100;
                    petRepository.updatePet(pet);
                    updateUI();
                }
            }
        });

        healButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pet != null) {
                    pet.health += 10;
                    if (pet.health > 100) pet.health = 100;
                    petRepository.updatePet(pet);
                    updateUI();
                }
            }
        });

        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePetStats();
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(updateRunnable, 10000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        petRepository.close();
    }

    private void updatePetStats() {
        if (pet != null) {
            if (pet.hunger > 0) {
                pet.hunger--;
            } else if (pet.health > 0) {
                pet.health--;
                sendNotification(pet.name, "Покорми меня");
            }

            if (pet.happiness > 0) {
                pet.happiness -= 1.5;
            } else {
                sendNotification(pet.name, "Поиграй со мной");
            }

            if (pet.health <= 0) {
                sendNotification(pet.name, "Вылечи меня");
            }

            petRepository.updatePet(pet);
            updateUI();
        }
    }

    private void updateUI() {
        if (pet != null) {
            hungerTextView.setText("" + pet.hunger + "%");
            happinessTextView.setText("" + pet.happiness + "%");
            healthTextView.setText("" + pet.health + "%");
            updateStatusImage();
        }
    }

    private void updateStatusImage() {
        if (pet != null) {
            if (pet.hunger < 40 || pet.happiness < 40 || pet.health < 40) {
                statusImageView.setImageResource(R.drawable.grustniy);
            } else if (pet.happiness < 40) {
                statusImageView.setImageResource(R.drawable.neigrali);
            } else if (pet.hunger > 60 || pet.health > 60) {
                statusImageView.setImageResource(R.drawable.pokormili);
            } else if (pet.happiness > 60) {
                statusImageView.setImageResource(R.drawable.veseliy);
            } else {
                statusImageView.setImageResource(R.drawable.bazar);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pet Notifications";
            String description = "Notifications for pet status";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}
