package com.example.tamagotchi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private PetRepository petRepository;
    private Pet pet;
    private Handler handler;
    private Runnable updateRunnable;

    private LinearLayout selectedCard;
    private String selectedPetName;
    private String selectedPetDescription;
    private String selectedPetStats;
    private int selectedPetImageResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        petRepository = new PetRepository(this);
        pet = petRepository.getPet();

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCard != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("petName", selectedPetName);
                    intent.putExtra("petDescription", selectedPetDescription);
                    intent.putExtra("petStats", selectedPetStats);
                    intent.putExtra("petImageResId", selectedPetImageResId);
                    startActivity(intent);
                } else {
                    Toast.makeText(SplashActivity.this, "Выберите питомца", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCard != null) {
                    showEditDialog();
                } else {
                    Toast.makeText(SplashActivity.this, "Выберите питомца", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupCardClickListeners();
        updateUI();

        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                pet = petRepository.getPet();
                updateUI();
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

    private void setupCardClickListeners() {
        LinearLayout card1 = findViewById(R.id.card1);

        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCard != null) {
                    selectedCard.setBackgroundResource(R.color.white);
                }
                selectedCard = (LinearLayout) v;
                selectedCard.setBackgroundResource(R.drawable.back_white_strok10);

                // Update selected pet details
                selectedPetName = ((TextView) findViewById(R.id.petName1)).getText().toString();
                selectedPetDescription = ((TextView) findViewById(R.id.petDescription1)).getText().toString();
                selectedPetStats = ((TextView) findViewById(R.id.petStats1)).getText().toString();
                selectedPetImageResId = pet.imageResId;
            }
        };

        card1.setOnClickListener(cardClickListener);
    }

    private void updateUI() {
        TextView petName1 = findViewById(R.id.petName1);
        TextView petDescription1 = findViewById(R.id.petDescription1);
        TextView petStats1 = findViewById(R.id.petStats1);

        petName1.setText(pet.name);
        petDescription1.setText(pet.description);
        petStats1.setText("Сытость: " + pet.hunger + "%, Счастье: " + pet.happiness + "%, Здоровье: " + pet.health + "%");
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_pet, null);
        builder.setView(dialogView);

        final EditText editPetName = dialogView.findViewById(R.id.editPetName);
        final EditText editPetDescription = dialogView.findViewById(R.id.editPetDescription);

        editPetName.setText(pet.name);
        editPetDescription.setText(pet.description);

        builder.setTitle("Редактировать питомца")
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pet.name = editPetName.getText().toString();
                        pet.description = editPetDescription.getText().toString();
                        petRepository.updatePet(pet);
                        updateUI();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}
