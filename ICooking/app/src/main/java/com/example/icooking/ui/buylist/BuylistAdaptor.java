package com.example.icooking.ui.buylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.ui.Inventory.Inventory;
import com.example.icooking.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BuylistAdaptor extends RecyclerView.Adapter<BuylistAdaptor.myViewHolder> {
    @NonNull
    @NotNull
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<Buylist> buylist = new ArrayList<>();
    OnItemClickListener bonItemClickListener;


    public BuylistAdaptor(OnItemClickListener onItemClickListener, ArrayList<Buylist> buylist) {
        this.buylist = buylist;
        this.bonItemClickListener = onItemClickListener;

    }

    @NonNull
    public ArrayList<Buylist> getBuylist() {
        this.buylist = buylist;
        return buylist;
    }

    @NonNull
    @NotNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item,parent,false);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myViewHolder holder, int position) {
        //bind view to fragment
        holder.itemgoodname.setText(buylist.get(position).getBuyName().toString());
        holder.itemgoodname.setChecked(buylist.get(position).isIschecked());
        holder.itemgoodname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton!=null)
                {
                    buylist.get(position).setIschecked(b);
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return buylist.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private CheckBox itemgoodname;
        private TextView itemgoodcount;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            itemgoodname = itemView.findViewById(R.id.buyname);
        }
    }
    public interface OnItemClickListener{
        void addbuy(Buylist buylist);
        void removebuy(int position);
    }


    public void setBuys(ArrayList<Buylist> buylist) {
        this.buylist = buylist;
        notifyDataSetChanged();
    }

    public void setInventory(ArrayList<Inventory> inventoryList){
        this.inventoryList=inventoryList;
        notifyDataSetChanged();
    }
    public String getKey(int position){
        return buylist.get(position).getKey();
    }
    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }
}
