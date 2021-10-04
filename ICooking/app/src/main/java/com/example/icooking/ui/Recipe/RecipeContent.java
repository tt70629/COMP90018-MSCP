package com.example.icooking.ui.Recipe;

import android.media.Image;

import com.example.icooking.Inventory;
import com.example.icooking.R;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeContent implements Serializable {
    private String title;
    private String intro;
    private ArrayList<String> ingredients;
    private ArrayList<String> steps;
    private String key;
    //private HashMap<String, Integer> stepImages;

    public RecipeContent() {
        title = "Milky chicken";
        intro = "I made this up!";
        ingredients = new ArrayList<String>();
        steps = new ArrayList<String>();
        ingredients.add("milk");
        ingredients.add("chicken");
        ingredients.add("onion");
        ingredients.add("mushroom");

        steps.add("1. Prepare all your ingredients.");
        steps.add("2. Wash the chicken.");
        steps.add("image for step 2");
        steps.add("3. Pour half bottle of the milk in to a bowl and put the chicken in.");
        steps.add("4. Marinate the chicken for 1 hour.");
        steps.add("5. Boil water in a pot.");
        steps.add("6. Put in the marinate chicken.");
        steps.add("7. Put in the mushrooms.");
        steps.add("8. Pour the rest of the milk into the pot.");
        steps.add("9. Let it cook for 30 minutes.");
        steps.add("10. Pour the chicken with the soup into a bowl and enjoy it!");
        steps.add("image for step 10");
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

//    public HashMap<String, Integer> getStepImages() {
//        return stepImages;
//    }

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
//    public void setStepImages(HashMap<String, Integer> stepImages) {
//        this.stepImages = stepImages;
//    }
}
