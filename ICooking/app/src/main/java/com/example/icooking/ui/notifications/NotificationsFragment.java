package com.example.icooking.ui.notifications;

import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.icooking.R;
import com.example.icooking.Inventory;
import com.example.icooking.databinding.FragmentNotificationsBinding;
import com.example.icooking.ui.Recipe.DAORecipe;
import com.example.icooking.ui.Recipe.Recipe;
import com.example.icooking.ui.Recipe.RecipeAdaptorIngredients;
import com.example.icooking.ui.Recipe.RecipeContent;
import com.example.icooking.ui.dashboard.DAOInventory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsFragment extends Fragment {


    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private DAOInventory daoInventory;
    private DAORecipe daoRecipe;
    private String key=null;
    private DisplayInventoryAdaptor invAdaptor;
    private SearchedRecipeAdaptor recAdaptor;
    private ArrayList<Inventory> inventory;
    private ArrayList<Inventory> selected_inv;
    private RecipeAdaptorIngredients adaptorIngred;
    private TextView cookbook_title;
    private TextView matched_recipe_title;
    private static final String TAG = "Here is the tag:";
    private Context bcontext;

    private HashMap<String, String> images;
    private ImageView imageDemo;



    ArrayList<Inventory> selected_ingredients = new ArrayList<>();

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
        cookbook_title.setText("Choose ingredient you from your inventory: ");

        matched_recipe_title = binding.matchedRecipeTitle;
        //matched_recipe_title.setText("");

        //imageDemo = ;

        images = new HashMap<String,String>();
        //Set options from inventory
        final RecyclerView currentInventory= binding.currentInventory;
        invAdaptor= new DisplayInventoryAdaptor();
        currentInventory.setAdapter(invAdaptor);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        currentInventory.setLayoutManager(layoutManager);
        daoInventory = new DAOInventory();
        fetchInventoryData();

        final RecyclerView searchedRecipe = binding.searchedRecipe;
        recAdaptor = new SearchedRecipeAdaptor(getContext());
        searchedRecipe.setAdapter(recAdaptor);
        searchedRecipe.setLayoutManager(new LinearLayoutManager(getContext()));
        daoRecipe = new DAORecipe();



        final Button button = binding.btnSearch;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (invAdaptor.selected_ingredients.isEmpty()){
                    Toast.makeText(getContext(),"Please select at least one ingredient!",Toast.LENGTH_SHORT).show();
                } else {
                    matched_recipe_title.setText("Here are some recommendations: ");
                    selected_ingredients=invAdaptor.selected_ingredients;
                    for(int i=0;i<selected_ingredients.size();i++) {
                        System.out.println(selected_ingredients.get(i).getIngredientName());
                    }
                    fetchRecipeData();
                }
            }
        });


        final Button button_test = binding.btnToRecipe;
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Recipe.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void fetchInventoryData() {

        daoInventory.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Inventory> inventoryList = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Inventory inventory=data.getValue(Inventory.class);
                    inventory.setKey(data.getKey());
                    inventory.setSelected(false);
                    inventoryList.add(inventory);
                }
                invAdaptor.setInventory(inventoryList);
                invAdaptor.notifyDataSetChanged();
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
                for (DataSnapshot data: snapshot.getChildren()){
                    RecipeContent recipeContent = data.getValue(RecipeContent.class);
                    recipeContent.setKey(data.getKey());
                    recipeContentList.add(recipeContent);
                    for(int i=0;i<selected_ingredients.size();i++){
                        for(int j=0;j<recipeContent.getIngredients().size();j++){
                            if(selected_ingredients.get(i).getIngredientName().equals(recipeContent.getIngredients().get(j))){
                                matched_number += 1;
                            }
                        }
                    }
                    if (matched_number > 1){
                        System.out.println("wow: "+matched_number);
                    }
                    //System.out.println(matched_number);
                    if(recipeContentList.size() > 3){
                        // counter +=1, recipecontentList = recipecontentList
                        //recipeContentList.get(counter*3),(counter*3+1),(counter*3+2) (if < size);
                        //if selected_ingredient changed, counter = 0;
                    }
                }
                recAdaptor.setRecipeContent(recipeContentList);
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