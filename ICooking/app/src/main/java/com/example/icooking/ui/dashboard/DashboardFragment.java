package com.example.icooking.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.Inventory;
import com.example.icooking.databinding.FragmentDashboardBinding;
import com.example.icooking.helper.InventoryItemTouchHelperCallBack;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //pseudo data for testing which should come from database or other source.
        ArrayList<Inventory> inventory= new ArrayList<Inventory>();
        inventory.add( new Inventory("milk","1"));
        inventory.add( new Inventory("ribeye steak","2"));
        inventory.add( new Inventory("chicken","2"));
        inventory.add( new Inventory("onion","3"));
        /*
         * set up TextView...
         */

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        /*
         * set up RecyclerView...
         */
        final RecyclerView recviewInventory= binding.recviewInventory;
        InventoryAdaptor adaptor= new InventoryAdaptor();
        adaptor.setInventory(inventory);
        recviewInventory.setAdapter(adaptor);
        recviewInventory.setLayoutManager( new LinearLayoutManager(getContext()));
        recviewInventory.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));//horizontal line between items.
        /*
         * use ItemTouchHelper to realize animation effect...
         */
        ItemTouchHelper.Callback callback = new InventoryItemTouchHelperCallBack(adaptor);
        ItemTouchHelper inventoryItemTouchHelper = new ItemTouchHelper(callback);
        inventoryItemTouchHelper.attachToRecyclerView(recviewInventory);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}