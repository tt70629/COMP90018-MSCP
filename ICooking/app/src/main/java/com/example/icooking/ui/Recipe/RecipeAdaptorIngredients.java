package com.example.icooking.ui.Recipe;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.Inventory;
import com.example.icooking.R;
import com.example.icooking.helper.ItemTouchHelperAdaptor;
import com.example.icooking.ui.dashboard.DAOInventory;
import com.example.icooking.ui.dashboard.InventoryAdaptor;
import com.example.icooking.ui.home.Buylist;
import com.example.icooking.ui.home.DAObuylist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This adaptor is mainly called by the Ingredients RecyclerView, it also handles the functions of
 * Remove ingredients from Inventory, Add ingredients to ToBuy list.
 */
public class RecipeAdaptorIngredients extends RecyclerView.Adapter<RecipeAdaptorIngredients.ViewHolder>{
    private Context context;
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();
    ArrayList<Buylist> toBuyList = new ArrayList<>();
    HashMap<String,String> toRemove = new HashMap<>();
    ArrayList<String> toBuy = new ArrayList<>();


    public RecipeAdaptorIngredients(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeAdaptorIngredients.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_text,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdaptorIngredients.ViewHolder holder, int position) {
        ArrayList<String> inventoryNames = new ArrayList<>();
        HashMap<String,String> tempInventory = new HashMap<>();
        for (Inventory i : inventoryList){
            inventoryNames.add(i.getIngredientName());

            // Make copy of the inventory name alone with its key.
            tempInventory.put(i.getIngredientName(), i.getKey());
        }

        // If it's in Inventory.
        if (inventoryNames.contains(ingredients.get(position))){
            toRemove.put(ingredients.get(position), tempInventory.get(ingredients.get(position)));
            holder.ingredientName.setText(ingredients.get(position));
            holder.ingredientName.setBackgroundColor(Color.parseColor("#32a852"));
        }
        // If it's not in Inventory.
        else {
            toBuy.add(ingredients.get(position));
            holder.ingredientName.setText(ingredients.get(position));
        }
    }

    public void addToBuy (){
        DAObuylist daoToBuy = new DAObuylist();
        ArrayList<String> alreadyHave = new ArrayList<>();
        ArrayList<String> confirmedToBuy = new ArrayList<>();
        for (Buylist i : toBuyList){
            alreadyHave.add(i.getBuyName());
        }

        for (String i : toBuy) {
            if (alreadyHave.contains(i)){
                // Do nothing
            }
            else {
                confirmedToBuy.add(i);
            }
        }

        // Remove duplicate
        Set<String> set = new HashSet<>(confirmedToBuy);
        confirmedToBuy.clear();
        confirmedToBuy.addAll(set);

        // Add to ToBuy list
        if (confirmedToBuy.size() != 0){
            for (String i : confirmedToBuy) {
                Buylist item = new Buylist(i);
                daoToBuy.add(item);
            }
            Toast.makeText(context, "Missing ingredients added to your ToBuy list!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "You already have all the ingredients in your ToBuy list", Toast.LENGTH_SHORT).show();
        }

    }

    public void remove (){
        DAOInventory daoInventory = new DAOInventory();
        for (String i : toRemove.keySet()){
            daoInventory.remove(toRemove.get(i));
        }
    }
    public HashMap<String, String> getToRemove() {
        return toRemove;
    }

    public void setToRemove(HashMap<String, String> toRemove) {
        this.toRemove = toRemove;
    }

    public ArrayList<String>  getToBuy() {
        return toBuy;
    }

    public void setToBuy(ArrayList<String> toBuy) {
        this.toBuy = toBuy;
    }

    public void setList(ArrayList<String> list){
        this.ingredients = list;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView ingredientName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName=itemView.findViewById(R.id.tv_recipe_text);
        }

    }
    public String getKey(int position){
        return inventoryList.get(position).getKey();
    }
    public Inventory getInventory(int position){
        return inventoryList.get(position);
    }
    public void setInventory(ArrayList<Inventory> inventoryList){
        this.inventoryList=inventoryList;
        notifyDataSetChanged();
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public void setToBuyList(ArrayList<Buylist> toBuyList){
        this.toBuyList = toBuyList;
        notifyDataSetChanged();
    }
}
