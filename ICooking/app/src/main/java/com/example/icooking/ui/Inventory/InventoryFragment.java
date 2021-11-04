package com.example.icooking.ui.Inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.R;
import com.example.icooking.databinding.FragmentDashboardBinding;
import com.example.icooking.helper.RecyclerTouchListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class InventoryFragment extends Fragment implements InventoryAdaptor.OnItemClickHandler {

    // private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private InventoryAdaptor adaptor;
    private BottomSheetDialog bottomSheetDialog;
    private DAOInventory daoInventory;
    private String key = null;
    static final long DEFAULT_DURATION = 10;
    static final int LEN_NAME = 12;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
         * Use data binding method...
         */
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*
         * Set up RecyclerView then render data accessing from DAO
         */
        final RecyclerView recviewInventory = binding.recviewInventory;
        adaptor = new InventoryAdaptor(this, getContext());
        recviewInventory.setAdapter(adaptor);
        recviewInventory.setLayoutManager(new LinearLayoutManager(getContext()));
        daoInventory = new DAOInventory();
        fetchInventoryData();




        /*
         * Set up addButton to trigger BottomSheetDialog for adding new ingredient
         */
        final Button addInventoryBtn = binding.addInventory;
        addInventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.botton_sheet_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.setDismissWithAnimation(true);
                TextView name_text = bottomSheetDialog.findViewById(R.id.test_ingredient);
                TextView expiryDate_text = bottomSheetDialog.findViewById(R.id.expiryDate_text);
                EditText etName = bottomSheetDialog.findViewById(R.id.et_ingredientName);
                DatePicker datePicker = bottomSheetDialog.findViewById(R.id.expiryDate);
                name_text.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);
                expiryDate_text.setVisibility(View.GONE);
                Button btnSubmit = bottomSheetDialog.findViewById(R.id.btn_submit);
                assert btnSubmit != null;
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assert etName != null;
                        if (!validateIngredientName(etName)) {
                            return;
                        }
                        String trim_etName = etName.getText().toString().trim();
                        if (!adaptor.doesInventoryNameExist(trim_etName)) {
                            daoInventory.add(new Inventory(trim_etName, getDefaultDateString()))
                                    .addOnSuccessListener(success -> {
                                        Toast.makeText(getContext(), "Add ingredient successfully", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }).addOnFailureListener(err -> {
                                Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "The ingredient has already listed in your inventory", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bottomSheetDialog.show();
            }
        });

        /*
         * use RecyclerTouchListener to realize operations like deleting and editing after swiping.
         */

        RecyclerTouchListener touchListener = new RecyclerTouchListener(getActivity(), recviewInventory);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.delete_box, R.id.edit_box)
                .setSwipeable(R.id.upperLayout, R.id.lowerLayout, (viewID, position) -> {
                    switch (viewID) {
                        case R.id.delete_box:
                            /*tip: The touchListener we used only deliver position as parameter, hence
                             *we need to utilize methods from adaptor to get key to interact with DAO.
                             */
                            String key = adaptor.getKey(position);
                            daoInventory.remove(key);

                            break;
                        case R.id.edit_box:

                            Inventory editInventory = adaptor.getInventory(position);
                            bottomSheetDialog = new BottomSheetDialog(getContext());
                            bottomSheetDialog.setContentView(R.layout.botton_sheet_dialog);
                            bottomSheetDialog.setCanceledOnTouchOutside(false);
                            bottomSheetDialog.setDismissWithAnimation(true);
                            EditText etName = bottomSheetDialog.findViewById(R.id.et_ingredientName);
                            //EditText etDay= bottomSheetDialog.findViewById(R.id.et_leftDay);
                            DatePicker datePicker = bottomSheetDialog.findViewById(R.id.expiryDate);
                            etName.setText(editInventory.getIngredientName());
                            String expiry_date = editInventory.getExpiryDate();
                            String[] date_arr = expiry_date.split("-");
                            datePicker.updateDate(
                                    Integer.parseInt(date_arr[0]),
                                    Integer.parseInt(date_arr[1]) - 1,
                                    Integer.parseInt(date_arr[2]));
                            //etDay.setText(editInventory.getDayLeft());
                            Button btnSubmit = bottomSheetDialog.findViewById(R.id.btn_submit);
                            btnSubmit.setText("Update");
                            assert btnSubmit != null;
                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!validateIngredientName(etName)) {
                                        return;
                                    }
                                    //Inventory newInventory=new Inventory(etName.getText().toString(),etDay.getText().toString());
                                    HashMap<String, Object> hashMapInventory = new HashMap<>();
                                    hashMapInventory.put("ingredientName", etName.getText().toString().trim());
                                    hashMapInventory.put("expiryDate", getDateStringDP(datePicker));
                                    assert etName != null;

                                        daoInventory.update(editInventory.getKey(), hashMapInventory)
                                                .addOnSuccessListener(success -> {
                                                    Toast.makeText(getContext(), "Update ingredient successfully", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog.dismiss();
                                                }).addOnFailureListener(err -> {
                                            Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
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
     * Method that fetches data from db then delivers it to inventoryAdaptor class
     * to display as recyclerViews.
     */
    private void fetchInventoryData() {

        daoInventory.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ArrayList<Inventory> inventoryList = new ArrayList<>();
                    Log.d("DS_STATUS", "Fetching start");
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Inventory inventory = data.getValue(Inventory.class);
                        inventory.setKey(data.getKey());
                        inventoryList.add(inventory);
                    }
                    Log.d("DS_STATUS", "Fetching complete");
                    Collections.sort(inventoryList);
                    adaptor.setInventory(inventoryList);
                    adaptor.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("DS_STATUS_ERROR", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
     * Methods that are implemented from InventoryAdaptor.OnItemClickHandler, however these methods
     * were used in interacting with pseudo data that is no longer in use.
     */

    @Override
    public void show(Inventory inv) {
    }

    public static boolean validateIngredientName(EditText name) {
        String val = name.getText().toString();
        if (val.isEmpty()) {
            name.setError("This field should not be empty");
            return false;
        }
        if (val.length() > LEN_NAME) {
            name.setError("The length of ingredient name should be less than " + LEN_NAME + " digitals");
            return false;
        }
        return true;
    }

    public String getDateStringDP(DatePicker datepicker) {
        int year = datepicker.getYear();
        //remember to add one in month, weird default setting.
        int month = datepicker.getMonth() + 1;
        int day = datepicker.getDayOfMonth();
        String date = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        return date;
    }
    public static String getDefaultDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime defaultExpiryDate = LocalDateTime.now().plusDays(DEFAULT_DURATION);
        String expiryDate = defaultExpiryDate.format(formatter);
        return expiryDate;
    }
}