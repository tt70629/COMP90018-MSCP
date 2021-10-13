package com.example.icooking.ui.Recipe;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * This is just a testing sample of store information of item that is going
 * to be stored into database.
 */
public class TestingToBuyItem implements Serializable {
    private String name;

    public TestingToBuyItem() {

    }

    public TestingToBuyItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
