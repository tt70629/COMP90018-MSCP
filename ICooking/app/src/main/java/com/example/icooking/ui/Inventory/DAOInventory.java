package com.example.icooking.ui.Inventory;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOInventory
{
    private final static String db_URL="https://icooking-db-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference databaseReference;
    public DAOInventory()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance(db_URL);
        databaseReference = db.getReference(Inventory.class.getSimpleName());
    }
    /* This method will push all attributes which have getter method, therefore make sure you create
     * getter method for any new added attribute!
    */
    public Task<Void> add(Inventory inv)
    {
        return databaseReference.push().setValue(inv);
    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {

        if(key == null)
        {
            return databaseReference;
        }
        return databaseReference.orderByChild("expiryDate");
    }

    public Query get()
    {
        return databaseReference;
    }
}
