package com.example.icooking.ui.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class records the information of a Recipe, it's also the class
 * that is saved in database.
 */
public class RecipeContent implements Serializable {
    private String title;
    private String intro;
    private ArrayList<String> ingredients;
    private ArrayList<String> steps;
    private String key;
    private HashMap<String, String> images;

    public RecipeContent() {
        ingredients = new ArrayList<String>();
        steps = new ArrayList<String>();
        images = new HashMap<String,String>();
    }

    public String getTitle() {
        return title;
    }

    public String getIntro() {
        return intro;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }
}
