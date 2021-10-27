package com.example.icooking.ui.Recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.icooking.ui.home.Buylist;
import com.example.icooking.ui.home.DAObuylist;
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

public class Recipe extends AppCompatActivity {
    private String title;
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

        toBuy = new ArrayList<>();
        toRemove = new HashMap<>();
        daoRecipe = new DAORecipe();


        tvRecipe = findViewById(R.id.tv_title_recipe);

        tvIntro = findViewById(R.id.tv_recipe_intro);

        ivDemo = findViewById(R.id.iv_recipe_demo);

        tvIngred = findViewById(R.id.tv_title_ingredient);
        tvIngred.setText("Ingredients");
        //tvIngred.setTextColor(Color.parseColor("#eb4034"));

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

//              Ignore the following comment.
//                        assert etName != null;
//                        assert etDay != null;
//                        daoRecipe.add(content)
//                                .addOnSuccessListener(success -> {
//                                    Toast.makeText(Recipe.this,"Add ingredient successfully",Toast.LENGTH_SHORT).show();
//                                }).addOnFailureListener(err->{
//                            Toast.makeText(Recipe.this,err.getMessage(),Toast.LENGTH_SHORT).show();
//                        });
//                        Intent intent = new Intent(Recipe.this, NotificationsFragment.class);
//                        startActivity(intent);
                    }
                });
                dialog.setNoListener(new FinishDialog.IOnNoListener() {
                    // If click No
                    @Override
                    public void onNo(FinishDialog dialog) {
                        // Close current recipe
                        RecipeContent content = new RecipeContent();
                        ArrayList<String> tempsteps = new ArrayList<>();
                        ArrayList<String> tempingred = new ArrayList<>();
                        HashMap<String, String> tempimages = new HashMap<>();
                        content.setTitle("Crab In Orange");
                        content.setIntro("Very popular dish during Song dynasty of ancient China. A perfect combination of sweet sourness from orange with flavors of crabs!");

                        tempingred.add("crab");
                        tempingred.add("orange");
                        tempingred.add("shrimp");
                        tempingred.add("ginger");
                        tempingred.add("salt");
                        content.setIngredients(tempingred);

                        tempimages.put("demo",
                                "https://firebasestorage.googleapis.com/v0/b/icooking-db.appspot.com/o/orangecrab_demo.jpg?alt=media&token=88db250e-8950-43d5-bd25-3f951ade2ab3");
                        tempsteps.add("1. Steam the crabs until they are properly cooked. Scoop out of the crab meat.");
                        tempsteps.add("step1 image");
                        tempimages.put("step1 image",
                                "https://firebasestorage.googleapis.com/v0/b/icooking-db.appspot.com/o/orangecrab_step1.png?alt=media&token=2c0e0580-28e0-4383-b4b7-a3e5ec11d272");

                        tempsteps.add("2. Scoop out of the pulp of orange. Fired the crab meat with salt, crashed ginger, shrimp and just a little bit of the orange pulp.");
                        tempsteps.add("step2 image");
                        tempimages.put("step2 image",
                                "https://firebasestorage.googleapis.com/v0/b/icooking-db.appspot.com/o/orangecrab_step2.png?alt=media&token=b47175e1-50bd-4b0c-8f86-809ff2f7156d");

                        tempsteps.add("3. Put the fried crab meat and shrimp into the orange that you just scoop pulp out from and steam it for 5 minutes.");
                        tempsteps.add("step3 image");
                        tempimages.put("step3 image",
                                "https://firebasestorage.googleapis.com/v0/b/icooking-db.appspot.com/o/orangecrab_step3.png?alt=media&token=b5009af4-d465-4d99-be13-7d6f1098c40c");

                        tempsteps.add("4. Take the orange out of the steamer and enjoy!");
                        tempsteps.add("step4 image");
                        tempimages.put("step4 image",
                                "https://firebasestorage.googleapis.com/v0/b/icooking-db.appspot.com/o/orangecrab_step4.png?alt=media&token=b26a7e04-99ef-4c71-8ea7-c2c793b2f6f6");

                        content.setImages(tempimages);
                        content.setSteps(tempsteps);

                        daoRecipe.add(content);
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
                adaptorIngred.setInventory(inventoryList);
                adaptorIngred.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

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