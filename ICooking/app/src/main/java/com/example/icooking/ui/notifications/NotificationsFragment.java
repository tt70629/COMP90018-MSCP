package com.example.icooking.ui.notifications;

import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.icooking.R;
import com.example.icooking.Inventory;
import com.example.icooking.databinding.FragmentNotificationsBinding;
import com.example.icooking.ui.Recipe.RecipeAdaptorIngredients;
import com.example.icooking.ui.dashboard.DAOInventory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {


    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private DAOInventory daoInventory;
    private String key=null;
    private DisplayInventoryAdaptor adaptor;
    private ArrayList<Inventory> inventory;
    private ArrayList<Inventory> selected_inv;
    private RecipeAdaptorIngredients adaptorIngred;
    private TextView cookbook_title;
    private static final String TAG = "Here is the tag:";
    private Context bcontext;

    ArrayList<Inventory> selected_ingredients = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        //Set title
        cookbook_title = binding.titleCookbook;
        cookbook_title.setText("Choose ingredient you from your inventory: ");

        //Set options from inventory
        final RecyclerView currentInventory= binding.currentInventory;
        adaptor= new DisplayInventoryAdaptor(getContext());
        currentInventory.setAdapter(adaptor);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        currentInventory.setLayoutManager(layoutManager);
        daoInventory = new DAOInventory();
        fetchInventoryData();



        final Button button = binding.btnFinish2;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adaptor.selected_ingredients.isEmpty()){
                    //makeText(bcontext,"Please select at least one ingredient!",Toast.LENGTH_SHORT).show();
                } else {
                    selected_ingredients=adaptor.selected_ingredients;
                    for(int i=0;i<selected_ingredients.size();i++) {
                        System.out.println(selected_ingredients.get(i).getIngredientName());
                    }
                }
            }
        });


        final Button button_test = binding.btnToRecipe;
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.icooking.ui.Recipe.Recipe.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void fetchInventoryData() {

        daoInventory.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Inventory> inventoryList = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Inventory inventory=data.getValue(Inventory.class);
                    inventory.setKey(data.getKey());
                    inventory.setSelected(false);
                    inventoryList.add(inventory);
                }
                adaptor.setInventory(inventoryList);
                //inventoryList.addAll(selected_ingredients);
                adaptor.notifyDataSetChanged();
                //adaptor.setmListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /*@Override
    public void onClick(int position) {
        {

            Toast.makeText(bcontext, "here: +", Toast.LENGTH_SHORT).show();
            System.out.println("???????????");
            //adaptor.getInventory(position).setSelected(true);
            //Toast.makeText(bcontext,"here: " + adaptor.getInventory(position).isSelected(), Toast.LENGTH_SHORT).show();
            //view.setBackgroundColor(Color.GREEN);
            // selected_ingredients.add(inventoryList.get(position));


            //Toast.makeText(mcontext, selected_ingredients.size() + "is selected:" + inventoryList.get(position).isSelected(), Toast.LENGTH_SHORT).show();

            //mListener.onItemClick(position);
            //Toast.makeText(mcontext,"here:" + inventoryList.get(0).getIngredientName(), Toast.LENGTH_SHORT).show();
        }
    }*/
}