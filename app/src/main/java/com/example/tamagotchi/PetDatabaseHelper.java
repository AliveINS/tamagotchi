package com.example.tamagotchi;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pet_database.db";
    private static final int DATABASE_VERSION = 1;

    public PetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pet (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "image_res_id INTEGER NOT NULL, " +
                "hunger INTEGER NOT NULL, " +
                "happiness INTEGER NOT NULL, " +
                "health INTEGER NOT NULL, " +
                "description TEXT NOT NULL)");

        // Пример инициализации данных в коде Java
        ContentValues values = new ContentValues();
        values.put("name", "Волчонок");
        values.put("image_res_id", R.drawable.bazar);
        values.put("hunger", 100);
        values.put("happiness", 100);
        values.put("health", 100);
        values.put("description", "Этот волчонок любит играть и очень дружелюбен.");
        db.insert("pet", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pet");
        onCreate(db);
    }
}
