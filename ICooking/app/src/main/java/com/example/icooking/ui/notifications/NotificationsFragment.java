package com.example.icooking.ui.notifications;

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

import static android.content.Context.PEOPLE_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class NotificationsFragment extends Fragment {


    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private DAOInventory daoInventory;
    private DAORecipe daoRecipe;
    private String key=null;
    private DisplayInventoryAdaptor invAdaptor;
    private SearchedRecipeAdaptor recAdaptor;
    private ArrayList<Inventory> inventory = new ArrayList<>();
    private ArrayList<Inventory> selected_inv;
    private RecipeAdaptorIngredients adaptorIngred;
    private TextView cookbook_title;
    private TextView matched_recipe_title;
    private static final String TAG = "Here is the tag:";
    private Context bcontext;
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
    //private long current_time = 0;


    ArrayList<Inventory> selected_ingredients = new ArrayList<>();
    ArrayList<Inventory> last_selected = new ArrayList<>();
    ArrayList<Inventory> prior_ingredients = new ArrayList<>();
    ArrayList<RecipeContent> local_recipe_content = new ArrayList<>();
    ArrayList<RecipeContent> displayed_recipe_content = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        //Set title
        cookbook_title = binding.titleCookbook;
        //cookbook_title.setText("Choose ingredient you from your inventory: ");

        matched_recipe_title = binding.matchedRecipeTitle;
        //matched_recipe_title.setText("");

        images = new HashMap<String,String>();
        //Set options from inventory
        final RecyclerView currentInventory= binding.currentInventory;
        invAdaptor= new DisplayInventoryAdaptor();
        currentInventory.setAdapter(invAdaptor);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,10);

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,10);

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,20);

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,20);
        currentInventory.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        currentInventory.setLayoutManager(layoutManager);
        daoInventory = new DAOInventory();
        fetchInventoryData();

        final RecyclerView searchedRecipe = binding.searchedRecipe;
        recAdaptor = new SearchedRecipeAdaptor(getContext());
        searchedRecipe.setAdapter(recAdaptor);
        searchedRecipe.setLayoutManager(new LinearLayoutManager(getContext()));
        daoRecipe = new DAORecipe();

        initialFetchRecipeData();


        final Button button = binding.btnSearch;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickShake();
            }
        });

        CheckBox ck_rough_search = binding.roughSearchCheckbox;
        //ck_rough_search.setChecked(true);
        ck_rough_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //System.out.println(b);
                rough_search = b;
                search_mode_changed = true;
            }
        });
        ck_rough_search.setChecked(true);

        manager=(SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        vibrator=(Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        sensors=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_rate=SensorManager.SENSOR_DELAY_NORMAL;
        listener=new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                float[] ary=event.values;
                float x=ary[0];
                float y=ary[1];
                float z=ary[2];
                float f=10;
                long temp_current_time = System.currentTimeMillis();
                if(Math.abs(x)>f||Math.abs(y)>f||Math.abs(z)>f){
                    //System.out.println("!!!");
                    //vibrator.vibrate(1000);
                    if(temp_current_time > last_time + 1000){
                        search_counter = 0;
                    }
                    if(temp_current_time > last_time + 300){
                        last_time = temp_current_time;
                        //System.out.println("This is current:" + current_time);
                        System.out.println("This is temp:" + last_time);
                        search_counter += 1;
                    }
                }
                if (search_counter > 2){
                    ready_to_search = true;
                    System.out.println("This is ready!!!!!" + ready_to_search);
                    /*if (invAdaptor.selected_ingredients.isEmpty()){
                        Toast.makeText(getContext(),"Please select at least one ingredient!",Toast.LENGTH_SHORT).show();
                    } else {
                        selected_ingredients = invAdaptor.selected_ingredients;
                    }*/
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

    public void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(listener);
    }

    private void clickShake() {
        initial_rec = false;
        if (invAdaptor.selected_ingredients.isEmpty()){
            Toast.makeText(getContext(),"Please select at least one ingredient!",Toast.LENGTH_SHORT).show();
        } else {
            selected_ingredients=invAdaptor.selected_ingredients;
            if(last_selected.isEmpty()){
                selection_changed = true;
                //System.out.println(selection_changed);
                last_selected.addAll(selected_ingredients);
            } else {
                int counter = 0;
                for(int i = 0;i<last_selected.size();i++){
                    for(int j=0; j<selected_ingredients.size();j++){
                        if (last_selected.get(i).getIngredientName().equals(selected_ingredients.get(j).getIngredientName())){
                            counter += 1;
                        } else {
                            //last_selected = selected_ingredients;
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

                //int expiry_count = 0;
                for(int i=0;i<inventoryList.size();i++){
                    if(Integer.parseInt(InventoryAdaptor.getDayLeft(inventoryList.get(i).getExpiryDate())) <=0){
                        //expiry_count += 1;
                        inventoryList.remove(i);
                    }
                }

                invAdaptor.setInventory(inventoryList);
                invAdaptor.notifyDataSetChanged();
                local_inventoryList.addAll(inventoryList);


                if(inventoryList.isEmpty()){
                    no_ingredients = true;
                } else {
                    while (local_inventoryList.size() > 0) {
                        int min = 10000;
                        Inventory temp_inventory = new Inventory();
                        for (int i = 0; i < local_inventoryList.size(); i++) {
                            if (Integer.parseInt(InventoryAdaptor.getDayLeft(local_inventoryList.get(i).getExpiryDate())) < min) {
                                temp_inventory = local_inventoryList.get(i);
                                min = Integer.parseInt(InventoryAdaptor.getDayLeft(local_inventoryList.get(i).getExpiryDate()));
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
                    //System.out.println(prior_ingredients.size());
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
                                if (prior_ingredients.get(i).getIngredientName().equals(recipeContent.getIngredients().get(j))) {
                                    matched_number += 1;
                                }

                            }
                        }
                        if(matched_number > 0){
                            boolean add = true;
                            //System.out.println("wow: " + matched_number + "are matched");
                            for(int i=0;i<recipeContentList.size();i++){
                                if(recipeContentList.contains(recipeContent)){
                                    add = false;
                                    //System.out.println("??????????????????????????");
                                }
                            }
                            if(add) {
                                recipeContentList.add(recipeContent);
                                System.out.println(recipeContent.getTitle());
                            }
                            //System.out.println(recipeContentList.size());
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
                        //System.out.println(add_list.size());
                        //System.out.println(recipeContentList.size());
                    } else {
                        recAdaptor.setRecipeContent(recipeContentList);
                        //System.out.println(recipeContentList.size());
                    }


                }

                if(prior_ingredients.isEmpty() || recipeContentList.isEmpty()){

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
                    smart_match = true;
                }

                //recAdaptor.setRecipeContent(add_list);
                recAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchRecipeData() {

        daoRecipe.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<RecipeContent> recipeContentList = new ArrayList<>();
                ArrayList<RecipeContent> matched_recipes = new ArrayList<>();
                int matched_number = 0;
                Random rd = new Random();
                if(selection_changed) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        RecipeContent recipeContent = data.getValue(RecipeContent.class);
                        recipeContent.setKey(data.getKey());
                        //recipeContentList.add(recipeContent);
                        for (int i = 0; i < selected_ingredients.size(); i++) {
                            for (int j = 0; j < recipeContent.getIngredients().size(); j++) {
                                if (selected_ingredients.get(i).getIngredientName().equals(recipeContent.getIngredients().get(j))) {
                                    matched_number += 1;
                                }

                            }
                        }
                        if(rough_search) {
                            //if (recipeContent.getIngredients().size() - matched_number < 3) {
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
                   
                    //recAdaptor.setRecipeContent(local_recipe_content);
                    if(local_recipe_content.size() == 0){
                        System.out.println("dont have any");
                        initialFetchRecipeData();
                        if (smart_match){
                            System.out.println("smart!");
                        } else {
                            System.out.println("stupid");
                        }
                    } else if(local_recipe_content.size() > 0 && local_recipe_content.size() <= 3) {
                        recAdaptor.setRecipeContent(local_recipe_content);
                        run_out_choice = true;
                        Toast.makeText(getContext(),"There are no more recommendations for the ingredients!",Toast.LENGTH_SHORT).show();
                    } else if(local_recipe_content.size() > 3){
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

                } else {
                    if(local_recipe_content.size() == 0){
                        System.out.println("dont have any");

                    } else if(local_recipe_content.size() > 0 && local_recipe_content.size() <= 3) {
                        recAdaptor.setRecipeContent(local_recipe_content);
                        run_out_choice = true;
                        Toast.makeText(getContext(),"There are no more recommendations for the ingredients!",Toast.LENGTH_SHORT).show();
                    } else if(local_recipe_content.size() > 3){
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
                /*if (recipeContentList.isEmpty()){
                    recAdaptor.setRecipeContent(recipeContentList);
                    matched_recipe_title.setText("Here are no more recommendations!");
                } else{
                    matched_recipe_title.setText("Here are some recommendations: ");
                    if (recipeContentList.size() <= 3){
                        recAdaptor.setRecipeContent(recipeContentList);
                    }
                    if (recipeContentList.size() > 3){

                    }
                }*/
                //recAdaptor.setRecipeContent(recipeContentList);
                recAdaptor.notifyDataSetChanged();
                //System.out.println(recipeContentList.get(0).getTitle());
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


    /*@Override
    public void onClick(int position) {
        {

            Toast.makeText(bcontext, "here: +", Toast.LENGTH_SHORT).show();
            System.out.println("???????????");
            //adaptor.getInventory(position).setSelected(true);
            //Toast.makeText(bcontext,"here: " + adaptor.getInventory(position).isSelected(), Toast.LENGTH_SHORT).show();
            //view.setBackgroundColor(Color.GREEN);
            // selected_ingredients.add(inventoryList.get(position));


            //Toast.makeText(mcontext, selected_ingredients.size() + "is selected:" + inventoryList.get(position).isSelected(), Toast.LENGTH_SHORT).show();

            //mListener.onItemClick(position);
            //Toast.makeText(mcontext,"here:" + inventoryList.get(0).getIngredientName(), Toast.LENGTH_SHORT).show();
        }
    }*/
}