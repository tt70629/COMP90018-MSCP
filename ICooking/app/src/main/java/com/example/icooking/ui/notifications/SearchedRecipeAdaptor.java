package com.example.icooking.ui.notifications;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.icooking.ui.Inventory.Inventory;
import com.example.icooking.R;
import com.example.icooking.ui.Recipe.Recipe;
import com.example.icooking.ui.Recipe.RecipeContent;

//import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
public class SearchedRecipeAdaptor extends RecyclerView.Adapter<SearchedRecipeAdaptor.ViewHolder>{
    ArrayList<Inventory> selected_ingredients = new ArrayList<>();
    ArrayList<RecipeContent> all_recipes = new ArrayList<>();
    ArrayList<RecipeContent> matched_recipes = new ArrayList<>();
    private Context mcontext;
    private Context tcontext;


    public SearchedRecipeAdaptor(Context mcontext){
        //this.matched_recipes = matched_recipes;
        this.mcontext = mcontext;
        //this.tcontext = tcontext;
    }

    @NonNull
    @NotNull
    @Override
    public SearchedRecipeAdaptor.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matched_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        holder.matched_recipe_name.setText(matched_recipes.get(position).getTitle());
        String temp_text = "";

        for(int i=0; i<matched_recipes.get(position).getIngredients().size();i++) {
            temp_text += matched_recipes.get(position).getIngredients().get(i);
            temp_text += "; ";
        }
        temp_text = temp_text.substring(0,temp_text.length()-2);
        holder.matched_recipe_ingredient.setText(temp_text);
        Glide.with(mcontext).load(matched_recipes.get(position).getImages().get("demo")).into(((ViewHolder) holder).matched_recipe_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext,Recipe.class);
                mcontext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount(){
        return matched_recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView matched_recipe_name;
        private TextView matched_recipe_ingredient;
        private ImageView matched_recipe_image;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            matched_recipe_name = itemView.findViewById(R.id.matched_recipe_names);
            matched_recipe_ingredient = itemView.findViewById(R.id.matched_recipe_ingredients);
            matched_recipe_image = itemView.findViewById(R.id.matched_recipe_images);
        }
    }
    public String getKey(int position){
        return matched_recipes.get(position).getKey();
    }
    public RecipeContent getInventory(int position){
        return matched_recipes.get(position);
    }
    public void setRecipeContent(ArrayList<RecipeContent> matched_recipes){
        this.matched_recipes=matched_recipes;
        notifyDataSetChanged();
    }

    /*public Drawable GetDrawableFromUrl(String url){
        try{
            URLConnection urls =new URL(url).openConnection();
            return  Drawable.createFromStream(urls.getInputStream(),"image");
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return  null;
    }*/

}
