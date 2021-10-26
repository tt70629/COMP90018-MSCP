package com.example.icooking.ui.home;

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

public class HomeAdaptor extends RecyclerView.Adapter<HomeAdaptor.myViewHolder> {
    @NonNull
    @NotNull
    ArrayList<Inventory> inventoryList = new ArrayList<>();
    ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<Buylist> buylist = new ArrayList<>();
    OnItemClickListener bonItemClickListener;


    public HomeAdaptor(OnItemClickListener onItemClickListener, ArrayList<Buylist> buylist) {
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
        //根据点击位置绑定数据
        //holder.itemgoodname.setText(buylist.get(position).getBuyName());;
        //holder.itemgoodcount.setText(buylist.get(position).getCount());
        //holder.itemgoodname.setChecked(buylist.get(position).isChecked());
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
    /*public interface OnItemClickListen{
        /*
        借口中的点击每一项的实现方法，参数自己定义
        view 点击的ITEM的视图
        DATA 点击的item的数据

        public void OnItemClick(View view, Buylist data);
    }*/
    public class myViewHolder extends RecyclerView.ViewHolder {
        //private ImageView itemgoodimg;
        private CheckBox itemgoodname;
        private TextView itemgoodcount;
        //private CheckBox itemcheck;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemgoodimg = (ImageView) itemView.findViewById(R.id.)
            itemgoodname = itemView.findViewById(R.id.buyname);
            //itemgoodcount=itemView.findViewById(R.id.itemcount);
            //itemcheck = (CheckBox)itemView.findFocus(R.id.buyname);
            //
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
