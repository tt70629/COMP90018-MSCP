package com.example.icooking.ui.MatchRecipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.icooking.ui.Inventory.Inventory;
import com.example.icooking.R;
import com.example.icooking.ui.Inventory.InventoryAdaptor;

//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DisplayInventoryAdaptor extends RecyclerView.Adapter<DisplayInventoryAdaptor.ViewHolder> {
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    ArrayList<Inventory> selected_ingredients = new ArrayList<>();

    public DisplayInventoryAdaptor() {

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_display_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.ingredientName.setText(inventoryList.get(position).getIngredientName());
        for(int i=0; i<inventoryList.size();i++) {
            holder.itemView.findViewById(holder.getLayoutPosition());
        }

        if(Integer.parseInt(InventoryAdaptor.getDayLeft(inventoryList.get(position).getExpiryDate())) <=3){
            holder.itemView.setBackgroundResource(R.drawable.expiry_ingredients_box);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inventoryList.get(position).isSelected()) {
                    inventoryList.get(position).setSelected(true);
                    holder.ingredientName.setTextColor(Color.WHITE);
                    if(Integer.parseInt(InventoryAdaptor.getDayLeft(inventoryList.get(position).getExpiryDate())) <=3){
                        holder.itemView.setBackgroundResource(R.drawable.expiry_ingredients_selected);
                    } else {
                        view.setBackgroundResource(R.drawable.selected_round);
                    }
                    selected_ingredients.add(inventoryList.get(position));

                } else {
                    inventoryList.get(position).setSelected(false);
                    holder.ingredientName.setTextColor(Color.GRAY);
                    if(Integer.parseInt(InventoryAdaptor.getDayLeft(inventoryList.get(position).getExpiryDate())) <=3){
                        holder.itemView.setBackgroundResource(R.drawable.expiry_ingredients_box);
                    } else {
                        view.setBackgroundResource(R.drawable.round_edittext);
                    }
                    selected_ingredients.remove(inventoryList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.inventory_display);
            ingredientName.setSelected(true);
        }
    }


    public String getKey(int position) {
        return inventoryList.get(position).getKey();
    }

    public Inventory getInventory(int position){
        return inventoryList.get(position);
    }

    public void setInventory(ArrayList<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        notifyDataSetChanged();
    }
}

