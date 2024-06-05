package com.example.tamagotchi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PetRepository {
    private SQLiteDatabase database;
    private PetDatabaseHelper dbHelper;

    public PetRepository(Context context) {
        dbHelper = new PetDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public Pet getPet() {
        Cursor cursor = database.query("pet", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int imageResIdIndex = cursor.getColumnIndex("image_res_id");
            int hungerIndex = cursor.getColumnIndex("hunger");
            int happinessIndex = cursor.getColumnIndex("happiness");
            int healthIndex = cursor.getColumnIndex("health");
            int descriptionIndex = cursor.getColumnIndex("description");

            int id = idIndex != -1 ? cursor.getInt(idIndex) : 0;
            String name = nameIndex != -1 ? cursor.getString(nameIndex) : "";
            int imageResId = imageResIdIndex != -1 ? cursor.getInt(imageResIdIndex) : 0;
            int hunger = hungerIndex != -1 ? cursor.getInt(hungerIndex) : 0;
            int happiness = happinessIndex != -1 ? cursor.getInt(happinessIndex) : 0;
            int health = healthIndex != -1 ? cursor.getInt(healthIndex) : 0;
            String description = descriptionIndex != -1 ? cursor.getString(descriptionIndex) : "";

            cursor.close();
            return new Pet(id, name, imageResId, hunger, happiness, health, description);
        }
        return null;
    }

    public void updatePet(Pet pet) {
        ContentValues values = new ContentValues();
        values.put("name", pet.name);
        values.put("description", pet.description);
        values.put("hunger", pet.hunger);
        values.put("happiness", pet.happiness);
        values.put("health", pet.health);

        database.update("pet", values, "id = ?", new String[]{String.valueOf(pet.id)});
    }

    public void close() {
        dbHelper.close();
    }
}
