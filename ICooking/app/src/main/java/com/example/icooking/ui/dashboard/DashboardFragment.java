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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardFragment extends Fragment implements InventoryAdaptor.OnItemClickHandler {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private ArrayList<Inventory> inventory;
    private InventoryAdaptor adaptor;
    private BottomSheetDialog bottomSheetDialog;
    private DAOInventory daoInventory; //Data Access Object
    private String key=null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
         * Set up view model, might be redundant...
         */
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        /*
         * Use data binding method...
         */
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


//        inventory= new ArrayList<>();
//        inventory.add( new Inventory("milk","1"));
//        inventory.add( new Inventory("ribeye steak","2"));
//        inventory.add( new Inventory("chicken","2"));
//        inventory.add( new Inventory("onion","3"));
//        inventory.add( new Inventory("q","3"));
//        inventory.add( new Inventory("o","3"));
//        inventory.add( new Inventory("i","3"));

        /*
         * set up TextView...
         */

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        /*
         * set up RecyclerView...
         */
        final RecyclerView recviewInventory= binding.recviewInventory;
        adaptor= new InventoryAdaptor(this);
        recviewInventory.setAdapter(adaptor);
        recviewInventory.setLayoutManager( new LinearLayoutManager(getContext()));
        daoInventory = new DAOInventory();
        fetchInventoryData();

        /*
         * use ItemTouchHelper to realize animation effect...
         */
        //ItemTouchHelper.Callback callback = new InventoryItemTouchHelperCallBack(adaptor,getContext());
        //ItemTouchHelper inventoryItemTouchHelper = new ItemTouchHelper(callback);
        //inventoryItemTouchHelper.attachToRecyclerView(recviewInventory);


        /*
         * set up BottomSheetDialog...
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
                assert btnSubmit != null;
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //addItem(new Inventory(etName.getText().toString(),etDay.getText().toString()));
                        assert etName != null;
                        assert etDay != null;
                        daoInventory.add( new Inventory(etName.getText().toString(),etDay.getText().toString()) )
                                .addOnSuccessListener(success -> {
                                    Toast.makeText(getContext(),"Add ingredient successfully",Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();
                                }).addOnFailureListener(err->{
                            Toast.makeText(getContext(),err.getMessage(),Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                bottomSheetDialog.show();
            }
        });
        /*
         * use RecyclerTouchListener to realize swipe and operations like deleting and editing ...
         */
        RecyclerTouchListener touchListener = new RecyclerTouchListener(getActivity(),recviewInventory);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        //Toast.makeText(getContext(),inventory.get(position).getIngredientName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.delete_box,R.id.edit_box)
                .setSwipeable(R.id.upperLayout, R.id.lowerLayout, (viewID, position) -> {
                    switch (viewID){
                        case R.id.delete_box:
                            String key=adaptor.getKey(position);
                            daoInventory.remove(key);

                            break;
                        case R.id.edit_box:
                            //Toast.makeText(getContext(),"Edit Not Available",Toast.LENGTH_SHORT).show();
                            Inventory editInventory = adaptor.getInventory(position);
                            bottomSheetDialog = new BottomSheetDialog(getContext());
                            bottomSheetDialog.setContentView(R.layout.botton_sheet_dialog);
                            bottomSheetDialog.setCanceledOnTouchOutside(false);
                            bottomSheetDialog.setDismissWithAnimation(true);
                            EditText etName= bottomSheetDialog.findViewById(R.id.et_ingredientName);
                            EditText etDay= bottomSheetDialog.findViewById(R.id.et_leftDay);
                            etName.setText(editInventory.getIngredientName());
                            etDay.setText(editInventory.getDayLeft());
                            Button btnSubmit= bottomSheetDialog.findViewById(R.id.btn_submit);
                            btnSubmit.setText("Update");
                            assert btnSubmit != null;
                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Inventory newInventory=new Inventory(etName.getText().toString(),etDay.getText().toString());
                                    HashMap<String,Object> hashMapInventory= new HashMap<>();
                                    hashMapInventory.put("ingredientName",etName.getText().toString());
                                    hashMapInventory.put("dayLeft",etDay.getText().toString());
                                    assert etName != null;
                                    assert etDay != null;
                                    daoInventory.update( editInventory.getKey(),hashMapInventory)
                                            .addOnSuccessListener(success -> {
                                                Toast.makeText(getContext(),"Update ingredient successfully",Toast.LENGTH_SHORT).show();
                                                bottomSheetDialog.dismiss();
                                            }).addOnFailureListener(err->{
                                        Toast.makeText(getContext(),err.getMessage(),Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                            bottomSheetDialog.show();
                            break;

                    }
                });
        recviewInventory.addOnItemTouchListener(touchListener);
        return root;
    }
    /*
     * method of fetching data from db...
     */
    private void fetchInventoryData() {


        daoInventory.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Inventory> inventoryList = new ArrayList<>();
                for (DataSnapshot data: snapshot.getChildren()){
                    Inventory inventory=data.getValue(Inventory.class);
                    inventory.setKey(data.getKey());
                    inventoryList.add(inventory);
                }
                adaptor.setInventory(inventoryList);
                adaptor.notifyDataSetChanged();


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
    /*
     * Implement from InventoryAdaptor.OnItemClickHandler
     */
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