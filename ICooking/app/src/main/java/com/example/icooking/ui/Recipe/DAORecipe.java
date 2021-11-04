package com.example.icooking.ui.Recipe;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

/**
 * @DAORecipe class is used to call Recipes from database. It can also add, remove Recipes from database.
 */
public class DAORecipe {
    private final static String db_URL="https://icooking-db-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference databaseReference;
    private FirebaseDatabase db;

    public DAORecipe()
    {
        db =FirebaseDatabase.getInstance(db_URL);
        databaseReference = db.getReference(RecipeContent.class.getSimpleName());
    }

    /**
     * @param recipe
     * Adding Recipes to database.
     */
    public Task<Void> add(RecipeContent recipe)
    {
        return databaseReference.push().setValue(recipe);
    }

    /**
     * @param key
     * @param hashMap
     * Edit Recipes in database.
     */
    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    /**
     * @param key
     * Remove Recipes from database.
     */
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    /**
     * @param key
     * Get the particular Recipe given key.
     */
    public Query get(String key)
    {
        if(key == null)
        {
            return databaseReference.orderByKey().limitToFirst(100);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(100);
    }

    /**
     * Get the whole Recipe database.
     */
    public Query get()
    {
        return databaseReference;
    }

}
