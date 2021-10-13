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
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    OnItemClickHandler mOnItemClickListener;


    public InventoryAdaptor(OnItemClickHandler onItemClickHandler) {
        this.mOnItemClickListener=onItemClickHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list_item,parent,false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     holder.ingredientName.setText(inventoryList.get(position).getIngredientName());
     holder.leftDay.setText(inventoryList.get(position).getDayLeft());

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView ingredientName;
        private TextView leftDay;
        private OnItemClickHandler mOnItemClickHandler;

        public ViewHolder(@NonNull View itemView, OnItemClickHandler mOnItemClickListener) {
            super(itemView);
            ingredientName=itemView.findViewById(R.id.ingredientName);
            leftDay=itemView.findViewById(R.id.leftDay);
            this.mOnItemClickHandler=mOnItemClickListener;

        }

    }
    /*
     * This interface is used when data is processed in activity.
     * Not in used for now.
     */
    interface OnItemClickHandler {
        void show (Inventory inventory);

    }
    /*
     * These getters and setter are for activities or fragments to
     * operate with inventory list from DB
     */
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
}
