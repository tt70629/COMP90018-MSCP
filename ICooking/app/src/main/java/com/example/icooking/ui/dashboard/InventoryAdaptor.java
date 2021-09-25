package com.example.icooking.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.Inventory;
import com.example.icooking.R;
import com.example.icooking.helper.ItemTouchHelperAdaptor;

import java.util.ArrayList;

public class InventoryAdaptor extends RecyclerView.Adapter<InventoryAdaptor.ViewHolder> implements ItemTouchHelperAdaptor {
    ArrayList<Inventory> inventory = new ArrayList<Inventory>();

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
        notifyDataSetChanged();//similar to re-render the view...
    }
    /*
     * move items vertically
     * @see com.example.icooking.helper.ItemTouchHelperAdaptor#onItemMove(int fromPosition, int toPosition)
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Inventory pre=inventory.remove(fromPosition);
        inventory.add(toPosition>fromPosition?toPosition-1:toPosition,pre);
        notifyItemMoved(fromPosition,toPosition);
    }
    /*
     * move items vertically
     * @see com.example.icooking.helper.ItemTouchHelperAdaptor#onItemMove(int fromPosition, int toPosition)
     */
    @Override
    public void onItemDismiss(int position) {
         inventory.remove(position);
         notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientName;
        private TextView leftDay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName=itemView.findViewById(R.id.ingredientName);
            leftDay=itemView.findViewById(R.id.leftDay);

        }
    }
}
