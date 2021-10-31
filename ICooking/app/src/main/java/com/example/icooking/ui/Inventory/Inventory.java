package com.example.icooking.ui.Inventory;

import com.google.firebase.database.Exclude;

import java.io.Serializable;



public class Inventory implements Serializable,Comparable<Inventory> {

    private String expiryDate;
    private boolean selected;
    @Exclude
    private String ingredientName;
    @Exclude
    private String key;




    public Inventory(String ingredientName, String expiryDate) {
        this.ingredientName = ingredientName;
        this.expiryDate=expiryDate;

    }

    public Inventory() {

    }

    public String getIngredientName() {
        return ingredientName;
    }
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected){
        this.selected = selected;
    }

    // In order to sort inventory by expiry date.
    @Override
    public int compareTo(Inventory inventory) {
        int target=Integer.parseInt(InventoryAdaptor.getDayLeft(this.getExpiryDate()));
        int candidate=Integer.parseInt(InventoryAdaptor.getDayLeft(inventory.getExpiryDate()));
        return (target<candidate? -1:(target==candidate?0:1));

    }
}

