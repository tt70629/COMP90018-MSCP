package com.example.icooking;

import com.example.icooking.ui.dashboard.InventoryAdaptor;
import com.google.firebase.database.Exclude;

import java.io.Serializable;



public class Inventory implements Serializable,Comparable<Inventory> {

    private String expiryDate;
    private boolean selected;
    private String dayLeft;
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

    //
    @Override
    public int compareTo(Inventory inventory) {
        int target=Integer.parseInt(InventoryAdaptor.getDayLeft(this.getExpiryDate()));
        int candidate=Integer.parseInt(InventoryAdaptor.getDayLeft(inventory.getExpiryDate()));
        return (target<candidate? -1:(target==candidate?0:1));

    }
}

