package com.example.icooking;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Inventory implements Serializable {

    private String ingredientName;
    private String dayLeft;
    @Exclude
    private String key;



    public Inventory(String ingredientName, String dayLeft) {
        this.ingredientName = ingredientName;
        this.dayLeft = dayLeft;
    }

    public Inventory() {

    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getDayLeft() {
        return dayLeft;

    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setDayLeft(String dayLeft) {
        this.dayLeft = dayLeft;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
}

