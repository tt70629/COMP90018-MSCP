package com.example.icooking.ui.dashboard;

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

import java.util.ArrayList;

public class InventoryAdaptor extends RecyclerView.Adapter<InventoryAdaptor.ViewHolder>  {
    ArrayList<Inventory> inventory = new ArrayList<Inventory>();
    OnItemClickHandler mOnItemClickListener;

    public InventoryAdaptor(ArrayList<Inventory> inventory, OnItemClickHandler onItemClickHandler) {
        this.inventory = inventory;
        this.mOnItemClickListener=onItemClickHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     holder.ingredientName.setText(inventory.get(position).getIngredientName());
     holder.leftDay.setText(inventory.get(position).getDayLeft());
    }

    @Override
    public int getItemCount() {
        return inventory.size();
    }

    public void setInventory(ArrayList<Inventory> inventory){
        this.inventory=inventory;
        notifyDataSetChanged();
    }




    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView ingredientName;
        private TextView leftDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName=itemView.findViewById(R.id.ingredientName);
            leftDay=itemView.findViewById(R.id.leftDay);

        }

    }
    interface OnItemClickHandler {
        void addItem(Inventory inventory);
        void removeItem(int position);
    }
}
