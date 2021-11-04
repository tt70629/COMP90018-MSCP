package com.example.icooking.ui.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.icooking.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @RecipeAdaptor class is called mainly by Steps RecyclerView.
 */
public class RecipeAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> list = new ArrayList<String>();
    private HashMap<String, String> images;


    public RecipeAdaptor(Context context){
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            return new LinearViewHolderText(LayoutInflater.from(context).inflate(R.layout.recipe_text,parent,false));
        }
        else {
            return new LinearViewHolderImage(LayoutInflater.from(context).inflate(R.layout.recipe_image,parent,false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0){
            ((LinearViewHolderText)holder).textView.setText(list.get(position));
        } else {
            Glide.with(context).load(images.get(list.get(position))).into(((LinearViewHolderImage) holder).imageView);
        }
    }


    /**
     * @param position
     * Return the Item view type.
     */
    @Override
    public int getItemViewType(int position) {
        if (!images.containsKey(list.get(position))) {
            return 0;
        }
        return 1;
    }

    /**
     * Count how many items there are.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * @param list
     * Set the Recipe Steps list.
     */
    public void setList(ArrayList<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * @param images
     * Set the Hashmap of images.
     */
    public void setImages(HashMap<String, String> images){
        this.images = images;
        notifyDataSetChanged();
    }

    public class LinearViewHolderText extends RecyclerView.ViewHolder{
        private TextView textView;
        public LinearViewHolderText(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_recipe_text);
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
