package com.example.icooking.ui.notifications;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.icooking.ui.dashboard.Inventory;
import com.example.icooking.R;

//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DisplayInventoryAdaptor extends RecyclerView.Adapter<DisplayInventoryAdaptor.ViewHolder> {
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    ArrayList<Inventory> selected_ingredients = new ArrayList<>();

    private Context mcontext;
    //private OnItemClickListener mListener;
    //OnItemClickHandler mOnItemClickListener;




    public DisplayInventoryAdaptor() {
        //this.mOnItemClickListener=onItemClickHandler;
        //this.mcontext = mcontext;
       /* test_list[0] = 1;
        test_list[1] =4;
        test_list[2] =6;*/
        //this.inventoryList = inventoryList;
        //this.mListener = mListener;
        //this.selected_ingredients = selected_ingredients;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_display_text, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ingredientName.setText(inventoryList.get(position).getIngredientName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inventoryList.get(position).isSelected()) {
                    inventoryList.get(position).setSelected(true);
                    //view.setBackgroundColor(Color.GREEN);
                    view.setBackgroundResource(R.drawable.selected_round);
                    selected_ingredients.add(inventoryList.get(position));

                } else {
                    inventoryList.get(position).setSelected(false);
                    //view.setBackgroundColor(Color.WHITE);
                    view.setBackgroundResource(R.drawable.round_edittext);
                    selected_ingredients.remove(inventoryList.get(position));
                }
                //Toast.makeText(mcontext, selected_ingredients.size() + "is selected:" + inventoryList.get(position).isSelected(), Toast.LENGTH_SHORT).show();

                //mListener.onItemClick(position);
                //Toast.makeText(mcontext,"here:" + inventoryList.get(0).getIngredientName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientName;
        //OnItemClickListener onItemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.inventory_display);
            //this.onItemClickListener = onItemClickListener;
            //itemView.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View view) { onItemClickListener.onClick(getAdapterPosition());
        }*/
    }
    /*interface OnItemClickHandler {
        void ingredient_selected(Inventory inventory);
        //void removeItem(int position);
    }*/

    public String getKey(int position) {
        return inventoryList.get(position).getKey();
    }

    /*public ArrayList<Inventory> getInventory() {
        return selected_ingredients;
    }*/
    public Inventory getInventory(int position){
        return inventoryList.get(position);
    }

    public void setInventory(ArrayList<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        notifyDataSetChanged();
    }

    /*public void setmListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }*/

    /*public interface OnItemClickListener{
        void onClick(int position);
    }*/
}

