package com.example.icooking.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.Inventory;
import com.example.icooking.R;
import com.example.icooking.databinding.FragmentDashboardBinding;
import com.example.icooking.helper.InventoryItemTouchHelperCallBack;
import com.example.icooking.helper.RecyclerTouchListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements InventoryAdaptor.OnItemClickHandler {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private ArrayList<Inventory> inventory;
    private InventoryAdaptor adaptor;
    private BottomSheetDialog bottomSheetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //pseudo data for testing which should come from database or other source.
        inventory= new ArrayList<Inventory>();
        inventory.add( new Inventory("milk","1"));
        inventory.add( new Inventory("ribeye steak","2"));
        inventory.add( new Inventory("chicken","2"));
        inventory.add( new Inventory("onion","3"));
        inventory.add( new Inventory("q","3"));
        inventory.add( new Inventory("o","3"));
        inventory.add( new Inventory("i","3"));
        /*
         * set up TextView...
         */

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        /*
         * set up RecyclerView...
         */
        final RecyclerView recviewInventory= binding.recviewInventory;
        adaptor= new InventoryAdaptor(inventory,this);

        recviewInventory.setAdapter(adaptor);
        recviewInventory.setLayoutManager( new LinearLayoutManager(getContext()));

        /*
         * use ItemTouchHelper to realize animation effect...
         */
        //ItemTouchHelper.Callback callback = new InventoryItemTouchHelperCallBack(adaptor,getContext());
        //ItemTouchHelper inventoryItemTouchHelper = new ItemTouchHelper(callback);
        //inventoryItemTouchHelper.attachToRecyclerView(recviewInventory);

        /*
         * set up BottomsheetDialog...
         */
        final FloatingActionButton addInventoryBtn= binding.addInventory;

        addInventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.botton_sheet_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.setDismissWithAnimation(true);

                EditText etName= bottomSheetDialog.findViewById(R.id.et_ingredientName);
                EditText etDay= bottomSheetDialog.findViewById(R.id.et_leftDay);
                Button btnSubmit= bottomSheetDialog.findViewById(R.id.btn_submit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addItem(new Inventory(etName.getText().toString(),etDay.getText().toString()));
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
        /*
         * use RecyclerTouchListener to realize swipe and operation like deleting and editing ...
         */
        RecyclerTouchListener touchListener = new RecyclerTouchListener(getActivity(),recviewInventory);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Toast.makeText(getContext(),inventory.get(position).getIngredientName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.delete_box,R.id.edit_box)
                .setSwipeable(R.id.upperLayout, R.id.lowerLayout, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.delete_box:
                                removeItem(position);
                                break;
                            case R.id.edit_box:
                                Toast.makeText(getContext(),"Edit Not Available",Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
        recviewInventory.addOnItemTouchListener(touchListener);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void addItem(Inventory inv) {
      inventory.add(0, inv);
      adaptor.setInventory(inventory);

    }

    @Override
    public void removeItem(int position) {
       inventory.remove(position);
       adaptor.setInventory(inventory);
    }


}