package com.example.icooking.ui.Recipe;

import com.example.icooking.Inventory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * This is just a testing DAO for adding staff to ToBuy List.
 */
public class TestingDAOToBuy {
    private final static String db_URL="https://icooking-db-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference databaseReference;

    public TestingDAOToBuy() {
        FirebaseDatabase db =FirebaseDatabase.getInstance(db_URL);
        databaseReference = db.getReference(TestingToBuyItem.class.getSimpleName());
    }

    public Task<Void> add(TestingToBuyItem item)
    {
        return databaseReference.push().setValue(item);
    }

    public Query get(String key)
    {
        if(key == null)
        {
            return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }

    public Query get()
    {
        return databaseReference;
    }
}
