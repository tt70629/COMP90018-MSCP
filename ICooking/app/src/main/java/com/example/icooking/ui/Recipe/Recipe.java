package com.example.icooking.ui.Recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.icooking.ui.Inventory.Inventory;
import com.example.icooking.R;
import com.example.icooking.ui.Inventory.DAOInventory;
import com.example.icooking.ui.Inventory.InventoryAdaptor;
import com.example.icooking.ui.buylist.Buylist;
import com.example.icooking.ui.buylist.DAObuylist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Recipe class is the main class to display and manipulate Recipes.
 */
public class Recipe extends AppCompatActivity {
    private String title;
    // Default value is given to 'key' to avoid NullPointer Exception
    private String key="-Mn04X7ikmjcvp-GqdXW";

    private ArrayList<String> ingredients;
    private ArrayList<String> steps;
    private HashMap<String, String> images;
    private ArrayList<String> toBuy;
    private HashMap<String, String> toRemove;

    private TextView tvRecipe, tvIngred, tvStep, tvIntro;
    private ImageView ivDemo;
    private RecyclerView recIngred, recStep;
    private Button btnFinish, btnAddToBuy;

    // Data Access Object
    private DAORecipe daoRecipe;
    private DAOInventory daoInventory;
    private DAObuylist daoToBuy;

    // Adaptors
    private RecipeAdaptorIngredients adaptorIngred;
    private RecipeAdaptor adaptorStep;

    // Database access
    private DatabaseReference database;
    private final static String db_URL="https://icooking-db-default-rtdb.asia-southeast1.firebasedatabase.app/";

    public void setKey(String key){
        this.key = key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        getSupportActionBar().hide();

        RecipeContent content = new RecipeContent();

        Intent intent = getIntent();
        key = intent.getStringExtra("key_name");
        setKey(intent.getStringExtra("key_name"));

        toBuy = new ArrayList<>();
        toRemove = new HashMap<>();
        daoRecipe = new DAORecipe();


        tvRecipe = findViewById(R.id.tv_title_recipe);

        tvIntro = findViewById(R.id.tv_recipe_intro);

        ivDemo = findViewById(R.id.iv_recipe_demo);

        tvIngred = findViewById(R.id.tv_title_ingredient);
        tvIngred.setText("Ingredients");

        tvStep = findViewById(R.id.tv_title_step);
        tvStep.setText("Steps");

        recIngred = findViewById(R.id.rec_ingredients);
        recStep = findViewById(R.id.rec_step);

        btnFinish = findViewById(R.id.btn_finish);
        btnAddToBuy = findViewById(R.id.btn_recipe_addToBuy);

        images = new HashMap<String,String>();

        // Create Adaptor for Ingredients
        recIngred.setLayoutManager(new LinearLayoutManager(Recipe.this));
        adaptorIngred = new RecipeAdaptorIngredients(Recipe.this);
        recIngred.setAdapter(adaptorIngred);
        daoInventory = new DAOInventory();
        fetchInventoryData();

        // Create Adaptor for Steps
        recStep.setLayoutManager(new LinearLayoutManager(Recipe.this));
        adaptorStep = new RecipeAdaptor(Recipe.this);
        adaptorStep.setImages(images);
        recStep.setAdapter(adaptorStep);

        //Accessing the Recipe database
        database = FirebaseDatabase.getInstance(db_URL).getReference(RecipeContent.class.getSimpleName()).child(key);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Set Title
                String title = snapshot.child("title").getValue(String.class);
                tvRecipe.setText(title);

                // Set Introduction
                String intro = snapshot.child("intro").getValue(String.class);
                tvIntro.setText(intro);

                // Set Ingredients
                GenericTypeIndicator<List<String>> to = new GenericTypeIndicator<List<String>>() {};
                List<String> ingredients = snapshot.child("ingredients").getValue(to);
                adaptorIngred.setIngredients((ArrayList<String>) ingredients);

                // Set Steps
                List<String> steps = snapshot.child("steps").getValue(to);
                adaptorStep.setList((ArrayList<String>) steps);

                // Set Images
                GenericTypeIndicator<Map<String, String>> toMap = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String,String> images = snapshot.child("images").getValue(toMap);
                Glide.with(Recipe.this).load(images.get("demo")).into(ivDemo);
                adaptorStep.setImages((HashMap<String, String>) images);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        // Fetch the toBuy list data
        daoToBuy = new DAObuylist();
        fetchToBuyListData();

        // Button that add missing Ingredients to ToBuy list once click.
        btnAddToBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adaptorIngred.addToBuy();
                btnAddToBuy.setVisibility(View.GONE);
            }
        });

        // Button that ask user whether they would like to remove Ingredients form Inventory.
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishDialog dialog = new FinishDialog(Recipe.this);
                dialog.setYesListener(new FinishDialog.IOnYesListener() {
                    // If click Yes
                    @Override
                    public void onYes(FinishDialog dialog) {
                        Toast.makeText(Recipe.this,"Ingredients removed!",Toast.LENGTH_SHORT).show();
                        // Remove used Ingredients from Inventory in database
                        adaptorIngred.remove();
                        // Close current recipe
                        finish();
                    }
                });
                dialog.setNoListener(new FinishDialog.IOnNoListener() {
                    // If click No
                    @Override
                    public void onNo(FinishDialog dialog) {
                        // Close current recipe
                        finish();
                    }
                });
                dialog.show();
            }
        });
    }


    /**
     * This method is used to fetch Inventory data from database.
     */
    private void fetchInventoryData() {
        daoInventory.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Inventory> inventoryList = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Inventory inventory=data.getValue(Inventory.class);
                    inventory.setKey(data.getKey());
                    inventoryList.add(inventory);
                }
                for(int i=0;i<inventoryList.size();i++){
                    if(Integer.parseInt(InventoryAdaptor.getDayLeft(inventoryList.get(i).getExpiryDate())) <=0){
                        inventoryList.remove(i);
                    }
                }
                adaptorIngred.setInventory(inventoryList);
                adaptorIngred.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    /**
     * This method is used to fetch Checklist data from database.
     */
    private void fetchToBuyListData() {
        daoToBuy.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Buylist> toBuy = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Buylist item = data.getValue(Buylist.class);
                    toBuy.add(item);
                }
                adaptorIngred.setToBuyList(toBuy);
                adaptorIngred.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }




}