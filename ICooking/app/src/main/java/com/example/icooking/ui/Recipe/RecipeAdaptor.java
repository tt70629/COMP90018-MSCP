package com.example.icooking.ui.Recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.Inventory;
import com.example.icooking.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class RecipeAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> inventory = new ArrayList<String>();
    private HashMap<String, Integer> images;
    private Boolean isStepsList;

    public RecipeAdaptor(Context context, Boolean isStepsList){
        this.context = context;
        this.isStepsList = isStepsList;
    }
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            return new LinearViewHolderText(LayoutInflater.from(context).inflate(R.layout.recipe_text,parent,false));
        }
        return new LinearViewHolderImage(LayoutInflater.from(context).inflate(R.layout.recipe_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (isStepsList){
            if (getItemViewType(position) == 0){
                ((LinearViewHolderText)holder).textView.setText(list.get(position));
            } else {
                ((LinearViewHolderImage)holder).imageView.setImageResource(images.get(list.get(position)));
            }
        }
        else {
            if (!inventory.contains(list.get(position))){
                ((LinearViewHolderText)holder).textView.setText(list.get(position));
                ((LinearViewHolderText)holder).textView.setBackgroundColor(Color.parseColor("#32a852"));
            } else {
                ((LinearViewHolderText)holder).textView.setText(list.get(position));
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (!isStepsList || !images.containsKey(list.get(position))) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<String> list){
        this.list = list;
    }

    public void setInventory(ArrayList<Inventory> inventory){
        ArrayList<String> inventories = new ArrayList<String>();
        for (Inventory i : inventory){
            inventories.add(i.getIngredientName());
        }
        this.inventory = inventories;
    }

    public void setImages(HashMap<String, Integer> images){
        this.images = images;
    }
    public class LinearViewHolderText extends RecyclerView.ViewHolder{
        private TextView textView;
        //private ImageView imageView;
        public LinearViewHolderText(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_recipe_text);
            //imageView = itemView.findViewById(R.id.iv_recipe_image);
        }
    }

    public class LinearViewHolderImage extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public LinearViewHolderImage(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_recipe_image);
        }
    }

}
