package com.example.icooking.ui.Recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icooking.Inventory;
import com.example.icooking.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe extends AppCompatActivity {
    private String title;
    private ArrayList<String> ingredients;
    private ArrayList<String> steps;
    private ArrayList<Inventory> inventory;
    private HashMap<String, Integer> images;

    private TextView tvRecipe, tvIngred, tvStep, tvIntro;
    private ImageView ivDemo;
    private RecyclerView recIngred, recStep;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        title = "Milky chicken";
        tvRecipe = findViewById(R.id.tv_title_recipe);
        tvRecipe.setText(title);
        tvIntro = findViewById(R.id.tv_recipe_intro);
        tvIntro.setText("I made this up!");
        ivDemo = findViewById(R.id.iv_recipe_demo);
        ivDemo.setImageResource(R.drawable.milkchickendemo);
        tvIngred = findViewById(R.id.tv_title_ingredient);
        tvIngred.setText("Ingredients");
        tvStep = findViewById(R.id.tv_title_step);
        tvStep.setText("Steps");
        recIngred = findViewById(R.id.rec_ingredients);
        recStep = findViewById(R.id.rec_step);
        btnFinish = findViewById(R.id.btn_finish);
        ingredients = new ArrayList<String>();
        steps = new ArrayList<String>();
        ingredients.add("milk");
        ingredients.add("chicken");
        ingredients.add("onion");
        ingredients.add("mushroom");

        inventory = new ArrayList<Inventory>();
        inventory.add( new Inventory("milk","1"));
        inventory.add( new Inventory("ribeye steak","2"));
        inventory.add( new Inventory("chicken","2"));
        inventory.add( new Inventory("onion","3"));

        images = new HashMap<String,Integer>();
        images.put("image for step 2", R.drawable.chicken);
        images.put("image for step 10", R.drawable.milkchicken);



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


        recIngred.setLayoutManager(new LinearLayoutManager(Recipe.this));
        RecipeAdaptor adaptorIngred = new RecipeAdaptor(Recipe.this, false);
        adaptorIngred.setInventory(inventory);
        adaptorIngred.setList(ingredients);
        recIngred.setAdapter(adaptorIngred);

        recStep.setLayoutManager(new LinearLayoutManager(Recipe.this));
        RecipeAdaptor adaptorStep = new RecipeAdaptor(Recipe.this, true);
        adaptorStep.setInventory(inventory);
        adaptorStep.setList(steps);
        adaptorStep.setImages(images);
        recStep.setAdapter(adaptorStep);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishDialog dialog = new FinishDialog(Recipe.this);
                dialog.setYesListener(new FinishDialog.IOnYesListener() {
                    @Override
                    public void onYes(FinishDialog dialog) {
                        Toast.makeText(Recipe.this,"Ingredients removed!",Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(Recipe.this, NotificationsFragment.class);
                        //startActivity(intent);
                    }
                });
                dialog.setNoListener(new FinishDialog.IOnNoListener() {
                    @Override
                    public void onNo(FinishDialog dialog) {
                        //Intent intent = new Intent(Recipe.this, NotificationsFragment.class);
                        //startActivity(intent);
                    }
                });
                dialog.show();
            }
        });
    }

}