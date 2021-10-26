package com.example.icooking.ui.Inventory;

import android.graphics.Color;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.R;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class InventoryAdaptor extends RecyclerView.Adapter<InventoryAdaptor.ViewHolder>  {
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    OnItemClickHandler mOnItemClickListener;
    final String EXPIRED="EXPIRED";
    static final int WARNING_DAYS=3;


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
        String DayLeft = getDayLeft(inventoryList.get(position).getExpiryDate());
        holder.ingredientName.setText(inventoryList.get(position).getIngredientName());
        holder.leftDay.setText(DayLeft);
        if(Integer.parseInt(DayLeft)<=WARNING_DAYS){
            holder.leftDay.setTextColor(Color.RED);
            if(Integer.parseInt(DayLeft)<=0){
                holder.leftDay.setText(EXPIRED);
            }
        }else{
            holder.leftDay.setTextColor(Color.BLACK);
        }


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
    public boolean doesInventoryNameExist(String name){
        boolean exist=false;
        for (int i=0;i<inventoryList.size();i++){
            Inventory inv=inventoryList.get(i);
            //also remove all whitespaces 
            if (name.toLowerCase().replaceAll("\\s+","").equals(inv.getIngredientName())){
                exist=true;
            }
        }
        return exist;
    }
    public void setInventory(ArrayList<Inventory> inventoryList){
        this.inventoryList=inventoryList;
        notifyDataSetChanged();
    }
    public static String getDayLeft(String expiryDate) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ HH:mm:ss]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            LocalDateTime _expiryDate = LocalDateTime.parse(expiryDate, formatter);
            LocalDateTime todayDate = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(todayDate,_expiryDate);
            String daysBetween_str = Long.toString(daysBetween);

            return daysBetween_str;
        } catch (Exception e) {
            return e.toString();
        }

    }
}
