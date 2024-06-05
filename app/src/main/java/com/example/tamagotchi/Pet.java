package com.example.tamagotchi;

public class Pet {
    public int id;
    public String name;
    public int imageResId;
    public int hunger;
    public int happiness;
    public int health;
    public String description;

    public Pet(int id, String name, int imageResId, int hunger, int happiness, int health, String description) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.hunger = hunger;
        this.happiness = happiness;
        this.health = health;
        this.description = description;
    }
}
