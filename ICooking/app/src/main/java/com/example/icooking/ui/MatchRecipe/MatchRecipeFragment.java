package com.example.icooking.ui.MatchRecipe;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.icooking.ui.Inventory.Inventory;
import com.example.icooking.databinding.FragmentNotificationsBinding;
import com.example.icooking.ui.Inventory.InventoryAdaptor;
import com.example.icooking.ui.Recipe.DAORecipe;
import com.example.icooking.ui.Recipe.RecipeAdaptorIngredients;
import com.example.icooking.ui.Recipe.RecipeContent;
import com.example.icooking.ui.Inventory.DAOInventory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class MatchRecipeFragment extends Fragment {


    private MatchRecipeViewModel matchRecipeViewModel;
    private FragmentNotificationsBinding binding;
    private DAOInventory daoInventory;
    private DAORecipe daoRecipe;
    private String key=null;
    private DisplayInventoryAdaptor invAdaptor;
    private SearchedRecipeAdaptor recAdaptor;
    private ArrayList<Inventory> inventory = new ArrayList<>();
    private TextView cookbook_title;
    private TextView matched_recipe_title;
    private boolean selection_changed = false;
    private boolean run_out_choice = false;
    private boolean rough_search = false;
    private boolean search_mode_changed = false;
    private boolean initial_rec = true;
    private boolean no_ingredients = false;
    private boolean smart_match = false;


    private HashMap<String, String> images;
    private ImageView imageDemo;

    private SensorManager manager;
    private Vibrator vibrator;
    private SensorEventListener listener;
    private Sensor sensors;
    private int sensor_rate;
    private boolean ready_to_search = false;
    private int search_counter = 0;
    private long last_time = 0;


    ArrayList<Inventory> selected_ingredients = new ArrayList<>();
    ArrayList<Inventory> last_selected = new ArrayList<>();
    ArrayList<Inventory> prior_ingredients = new ArrayList<>();
    ArrayList<RecipeContent> local_recipe_content = new ArrayList<>();
    ArrayList<RecipeContent> displayed_recipe_content = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        matchRecipeViewModel =
                new ViewModelProvider(this).get(MatchRecipeViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Set title
        cookbook_title = binding.titleCookbook;

        matched_recipe_title = binding.matchedRecipeTitle;

        images = new HashMap<String,String>();
        //Set recyclerview
        final RecyclerView currentInventory= binding.currentInventory;
        invAdaptor= new DisplayInventoryAdaptor();
        currentInventory.setAdapter(invAdaptor);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();

        //set the paddings
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 20);

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 20);
        currentInventory.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        currentInventory.setLayoutManager(layoutManager);

        //set DAO
        daoInventory = new DAOInventory();

        //initialize the ingredients
        fetchInventoryData();


        final RecyclerView searchedRecipe = binding.searchedRecipe;
        recAdaptor = new SearchedRecipeAdaptor(getContext());
        searchedRecipe.setAdapter(recAdaptor);
        searchedRecipe.setLayoutManager(new LinearLayoutManager(getContext()));
        daoRecipe = new DAORecipe();

        // Initialize the page if it's first time visited
        initialFetchRecipeData();

        //set the search button
        final Button button = binding.btnSearch;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set click function
                clickShake();
            }
        });

        //rough search
        CheckBox ck_rough_search = binding.roughSearchCheckbox;

        ck_rough_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                rough_search = b;
                search_mode_changed = true;
            }
        });
        ck_rough_search.setChecked(true);

        //Set the shaker sensor manager
        manager=(SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        vibrator=(Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        sensors=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_rate=SensorManager.SENSOR_DELAY_NORMAL;
        //Set listener
        listener=new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                float[] ary=event.values;
                float x=ary[0];
                float y=ary[1];
                float z=ary[2];
                //set a value to awake the shaker
                float f=18;
                //record the time for calculating intervals
                long temp_current_time = System.currentTimeMillis();
                //set the algorithm that fits human's two habits of shaking the smartphone
                //(based on x-axis and based on y-axis)
                if(0.8*Math.abs(x) + 1.4*Math.abs(y) + 0.8*Math.abs(z)>f
                        ||1.4*Math.abs(x) + 0.8*Math.abs(y) + 0.8*Math.abs(z)>f){
                    //if the interval is too long, reset the counter
                    if(temp_current_time > last_time + 1000){
                        search_counter = 0;
                    }
                    //the interval is 300ms. if the phone is still shaking after 300ms, counter+1
                    if(temp_current_time > last_time + 300){
                        last_time = temp_current_time;
                        search_counter += 1;
                    }
                }
                //when counter == 3, reset counter and run the clickShake function
                if (search_counter > 2){
                    vibrator.vibrate(400);
                    ready_to_search = true;
                    clickShake();
                    ready_to_search = false;
                    search_counter = 0;

                }
            }
            public void onAccuracyChanged(Sensor arg0, int arg1) {

            }
        };

        manager.registerListener(listener, sensors, sensor_rate);

        return root;
    }
    // Destroy the listener
    public void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(listener);
    }

    /**
     * Run the search algorithm if users click the button or shake the phone
     */

    private void clickShake() {
        initial_rec = false;
        if (invAdaptor.selected_ingredients.isEmpty()){
            Toast.makeText(getContext(),"Please select at least one ingredient!",
                    Toast.LENGTH_SHORT).show();
        } else {
            selected_ingredients=invAdaptor.selected_ingredients;
            if(last_selected.isEmpty()){
                selection_changed = true;
                last_selected.addAll(selected_ingredients);
            } else {
                int counter = 0;
                for(int i = 0;i<last_selected.size();i++){
                    for(int j=0; j<selected_ingredients.size();j++){
                        if (last_selected.get(i).getIngredientName().
                                equals(selected_ingredients.get(j).getIngredientName())){
                            counter += 1;
                        }
                    }
                }
                if (counter >= last_selected.size() && counter >= selected_ingredients.size()){
                    selection_changed = false;
                } else {
                    selection_changed = true;
                    last_selected.clear();
                    last_selected.addAll(selected_ingredients);
                }
                System.out.println(selection_changed);
            }
            if (run_out_choice) {
                run_out_choice =false;
                selection_changed = true;
            }
            if (search_mode_changed){
                search_mode_changed = false;
                selection_changed = true;
            }
            fetchRecipeData();
        }
    }

    /**
     * display the ingredients fetched from the database
     */
    private void fetchInventoryData() {

        daoInventory.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Inventory> inventoryList = new ArrayList<>();
                ArrayList<Inventory> local_inventoryList = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Inventory inventory=data.getValue(Inventory.class);
                    inventory.setKey(data.getKey());
                    inventory.setSelected(false);
                    inventoryList.add(inventory);
                }

                for(int i=0;i<inventoryList.size();i++){
                    if(Integer.parseInt(InventoryAdaptor.
                            getDayLeft(inventoryList.get(i).getExpiryDate())) <=0){
                        inventoryList.remove(i);
                    }
                }

                invAdaptor.setInventory(inventoryList);
                invAdaptor.notifyDataSetChanged();
                local_inventoryList.addAll(inventoryList);


                if(inventoryList.isEmpty()){
                    no_ingredients = true;
                } else {
                    // sort the ingredients by expiry days
                    while (local_inventoryList.size() > 0) {
                        int min_expiry_days = 10000;
                        Inventory temp_inventory = new Inventory();
                        for (int i = 0; i < local_inventoryList.size(); i++) {
                            if (Integer.parseInt(InventoryAdaptor.
                                    getDayLeft(local_inventoryList.get(i).getExpiryDate()))
                                    < min_expiry_days) {

                                temp_inventory = local_inventoryList.get(i);

                                min_expiry_days = Integer.parseInt(InventoryAdaptor.
                                        getDayLeft(local_inventoryList.get(i).getExpiryDate()));
                            }
                        }
                        prior_ingredients.add(temp_inventory);
                        local_inventoryList.remove(temp_inventory);
                    }
                    if (prior_ingredients.size() > 3){
                        for (int i=3;i<prior_ingredients.size();i++){
                            prior_ingredients.remove(prior_ingredients.get(i));
                        }
                    }
                }
                if(no_ingredients){
                    cookbook_title.setText("You don't have any ingredients. Please add ingredients! ");
                } else {
                    cookbook_title.setText("Choose ingredient from your inventory: ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Set the recipes that should be displayed when user first time get into this fragment
     * (or after swap pages)
     */
    private void initialFetchRecipeData(){
        daoRecipe.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<RecipeContent> recipeContentList = new ArrayList<>();
                ArrayList<RecipeContent> add_list = new ArrayList<>();
                Random rd = new Random();
                int matched_number = 0;
                for(int i=0;i<prior_ingredients.size();i++){
                    System.out.println(prior_ingredients.get(i).getIngredientName());
                }
                if(prior_ingredients.size()>0){
                    for (DataSnapshot data: snapshot.getChildren()){
                        RecipeContent recipeContent = data.getValue(RecipeContent.class);
                        recipeContent.setKey(data.getKey());

                        for (int i = 0; i < prior_ingredients.size(); i++) {
                            for (int j = 0; j < recipeContent.getIngredients().size(); j++) {
                                if (prior_ingredients.get(i).getIngredientName().
                                        equals(recipeContent.getIngredients().get(j))) {
                                    matched_number += 1;
                                }

                            }
                        }
                        if(matched_number>0) {
                            recipeContentList.add(recipeContent);
                        }
                        matched_number = 0;
                    }
                    if(recipeContentList.size() > 3){
                        for(int i=0;i<3;i++) {
                            int random_number = rd.nextInt(recipeContentList.size());
                            add_list.add(recipeContentList.get(random_number));
                            recipeContentList.remove(recipeContentList.get(random_number));
                        }
                        recAdaptor.setRecipeContent(add_list);
                    } else {
                        recAdaptor.setRecipeContent(recipeContentList);
                    }
                }

                if(prior_ingredients.isEmpty() || recipeContentList.isEmpty()){
                    if(initial_rec) {
                        if (recipeContentList.isEmpty() && !prior_ingredients.isEmpty()) {
                            matched_recipe_title.setText("No recipes matches with your " +
                                    "close-to-expiry ingredients. " + "We guess you would like: ");
                        }
                        if (recipeContentList.isEmpty() && prior_ingredients.isEmpty()) {
                            matched_recipe_title.setText("We guess you would like: ");
                        }
                    }
                    smart_match = false;
                    for (DataSnapshot data: snapshot.getChildren()){
                        RecipeContent recipeContent = data.getValue(RecipeContent.class);
                        recipeContent.setKey(data.getKey());
                        recipeContentList.add(recipeContent);
                    }
                    if(recipeContentList.size() > 3){
                        for(int i=0;i<3;i++) {
                            int random_number = rd.nextInt(recipeContentList.size());
                            add_list.add(recipeContentList.get(random_number));
                            recipeContentList.remove(recipeContentList.get(random_number));
                        }
                        recAdaptor.setRecipeContent(add_list);
                    } else {
                        recAdaptor.setRecipeContent(recipeContentList);
                    }
                } else {
                    if(initial_rec) {
                        matched_recipe_title.setText("We matched some recipes based on " +
                                "your close-to-expiry ingredients: ");
                    }
                    smart_match = true;
                }
                recAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Set the recipes that should be displayed on the pages once the user
     * clicks the search button or shakes the phone.
     */
    private void fetchRecipeData() {

        daoRecipe.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<RecipeContent> recipeContentList = new ArrayList<>();
                int matched_number = 0;
                Random rd = new Random();
                //If selection has been changed, fetch the data from database again.
                if(selection_changed) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        RecipeContent recipeContent = data.getValue(RecipeContent.class);
                        recipeContent.setKey(data.getKey());
                        for (int i = 0; i < selected_ingredients.size(); i++) {
                            for (int j = 0; j < recipeContent.getIngredients().size(); j++) {
                                if (selected_ingredients.get(i).getIngredientName().
                                        equals(recipeContent.getIngredients().get(j))) {
                                    matched_number += 1;
                                }

                            }
                        }
                        if(rough_search) {
                            if(matched_number > 0){
                                System.out.println("wow: " + matched_number + "are matched");
                                recipeContentList.add(recipeContent);
                            }
                        } else {
                            if (recipeContent.getIngredients().size() - matched_number < 1) {
                                System.out.println("wow: " + "perfect match");
                                recipeContentList.add(recipeContent);
                            }
                        }
                        matched_number = 0;
                    }
                    if (!local_recipe_content.isEmpty()) {
                        local_recipe_content.clear();
                    }

                    local_recipe_content.addAll(recipeContentList);

                    if(local_recipe_content.size() == 0){
                        System.out.println("dont have any");
                        initialFetchRecipeData();
                        if (smart_match){
                            System.out.println("smart!");
                            matched_recipe_title.setText(" ");
                            matched_recipe_title.setText("No recipe matched with the selected" +
                                    " ingredients. " + "We recommend these recipes based on your " +
                                    "close-to-expiry ingredients: ");
                        } else {
                            System.out.println("stupid");
                            matched_recipe_title.setText(" ");
                            matched_recipe_title.setText("No recipe matched with the selected " +
                                    "ingredients and close-to-expiry ingredients." +
                                    " We guess you would like: ");
                        }
                    } else if(local_recipe_content.size() > 0 && local_recipe_content.size() <= 3) {
                        matched_recipe_title.setText(" ");
                        matched_recipe_title.setText("Here are some matched recipes: ");
                        recAdaptor.setRecipeContent(local_recipe_content);
                        run_out_choice = true;
                        Toast.makeText(getContext(),"There are no more recommendations for" +
                                " the ingredients!",Toast.LENGTH_SHORT).show();
                    } else if(local_recipe_content.size() > 3){
                        matched_recipe_title.setText(" ");
                        matched_recipe_title.setText("Here are some matched recipes: ");
                        if(!displayed_recipe_content.isEmpty()){
                            displayed_recipe_content.clear();
                        }
                        for(int i=0;i<3;i++) {
                            int random_number = rd.nextInt(local_recipe_content.size());
                            displayed_recipe_content.add(local_recipe_content.get(random_number));
                            local_recipe_content.remove(local_recipe_content.get(random_number));
                            System.out.println(random_number);
                        }
                        recAdaptor.setRecipeContent(displayed_recipe_content);
                    }
                    //if selection is not changed, fetch the data from local recipe list
                    //the local recipe list has removed the previous three recipes that has been
                    //displayed.
                } else {
                    if(local_recipe_content.size() == 0){
                        System.out.println("dont have any");
                        initialFetchRecipeData();
                        if (smart_match){
                            System.out.println("smart!");
                            matched_recipe_title.setText(" ");
                            matched_recipe_title.setText("No recipe matched with the selected " +
                                    "ingredients. " + "We recommend these recipes based on " +
                                    "your close-to-expiry ingredients.");
                        } else {
                            System.out.println("stupid");
                            matched_recipe_title.setText(" ");
                            matched_recipe_title.setText("No recipe matched with the selected" +
                                    " ingredients and close-to-expiry ingredients." +
                                    " We guess you would like: ");
                        }

                    } else if(local_recipe_content.size() > 0 && local_recipe_content.size() <= 3) {
                        matched_recipe_title.setText(" ");
                        matched_recipe_title.setText("Here are some matched recipes: ");
                        recAdaptor.setRecipeContent(local_recipe_content);
                        run_out_choice = true;
                        Toast.makeText(getContext(),"There are no more recommendations" +
                                " for the ingredients!",Toast.LENGTH_SHORT).show();
                    } else if(local_recipe_content.size() > 3){
                        matched_recipe_title.setText(" ");
                        matched_recipe_title.setText("Here are some matched recipes: ");
                        if(!displayed_recipe_content.isEmpty()){
                            displayed_recipe_content.clear();
                        }
                        for(int i=0;i<3;i++) {
                            int random_number = rd.nextInt(local_recipe_content.size());
                            displayed_recipe_content.add(local_recipe_content.get(random_number));
                            local_recipe_content.remove(local_recipe_content.get(random_number));
                            System.out.println(random_number);
                        }
                        recAdaptor.setRecipeContent(displayed_recipe_content);
                    }

                }
                recAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}