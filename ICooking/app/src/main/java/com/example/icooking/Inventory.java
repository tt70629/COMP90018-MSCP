package com.example.icooking;

public class Inventory {

    private String ingredientName;
    private String dayLeft;

    public Inventory(String ingredientName, String dayLeft) {
        this.ingredientName = ingredientName;
        this.dayLeft = dayLeft;
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
}

