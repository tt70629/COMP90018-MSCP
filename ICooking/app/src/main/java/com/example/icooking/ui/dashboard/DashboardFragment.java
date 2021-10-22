package com.example.icooking.ui.dashboard;

import android.os.Bundle;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;

public class DashboardFragment extends Fragment implements InventoryAdaptor.OnItemClickHandler {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private ArrayList<Inventory> inventory;
    private InventoryAdaptor adaptor;
    private BottomSheetDialog bottomSheetDialog;
    private DAOInventory daoInventory; //Data Access Object
    private String key = null;
    private final static long DEFAULT_DURATION = 10;

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

        /*
         * Set up RecyclerView then render data accessing from DAO
         */
        final RecyclerView recviewInventory = binding.recviewInventory;
        adaptor = new InventoryAdaptor(this);
        recviewInventory.setAdapter(adaptor);
        recviewInventory.setLayoutManager(new LinearLayoutManager(getContext()));
        daoInventory = new DAOInventory();
        fetchInventoryData();



        /*
         * Set up addButton to trigger BottomSheetDialog for adding new ingredient
         */
        final FloatingActionButton addInventoryBtn = binding.addInventory;
        addInventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.botton_sheet_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.setDismissWithAnimation(true);

                TextView expiryDate_text = bottomSheetDialog.findViewById(R.id.expiryDate_text);
                EditText etName = bottomSheetDialog.findViewById(R.id.et_ingredientName);
                DatePicker datePicker = bottomSheetDialog.findViewById(R.id.expiryDate);
                datePicker.setVisibility(View.GONE);
                expiryDate_text.setVisibility(View.GONE);
                //EditText etDay= bottomSheetDialog.findViewById(R.id.et_leftDay);
                Button btnSubmit = bottomSheetDialog.findViewById(R.id.btn_submit);
                assert btnSubmit != null;
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //addItem(new Inventory(etName.getText().toString(),etDay.getText().toString()));
                        assert etName != null;
                        //assert etDay != null;
                        if (!validateIngredientName(etName)) {
                            return;}
                        if (!adaptor.doesInventoryNameExist(etName.getText().toString())) {

                            daoInventory.add(new Inventory(etName.getText().toString(), getDefaultDateString()))
                                    .addOnSuccessListener(success -> {
                                        Toast.makeText(getContext(), "Add ingredient successfully", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }).addOnFailureListener(err -> {
                                Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "Bro, the ingredient has already listed in your inventory", Toast.LENGTH_SHORT).show();
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
                                    Integer.parseInt(date_arr[1]),
                                    Integer.parseInt(date_arr[2]));
                            //etDay.setText(editInventory.getDayLeft());
                            Button btnSubmit = bottomSheetDialog.findViewById(R.id.btn_submit);
                            btnSubmit.setText("Update");
                            assert btnSubmit != null;
                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Inventory newInventory=new Inventory(etName.getText().toString(),etDay.getText().toString());
                                    HashMap<String, Object> hashMapInventory = new HashMap<>();
                                    hashMapInventory.put("ingredientName", etName.getText().toString());
                                    hashMapInventory.put("expiryDate", getDateStringDP(datePicker));
                                    assert etName != null;
                                    //assert etDay != null;
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
                ArrayList<Inventory> inventoryList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Inventory inventory = data.getValue(Inventory.class);
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
     * Methods that are implemented from InventoryAdaptor.OnItemClickHandler, however these methods
     * were used in interacting with pseudo data that is no longer in use.
     */
    @Override
    public void show(Inventory inv) {
    }

    public boolean validateIngredientName(EditText name) {
        String val = name.getText().toString();
        if (val.isEmpty()) {
            name.setError("This field should not be empty");
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

    public String getDefaultDateString() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ HH:mm:ss]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        LocalDateTime defaultExpiryDate = LocalDateTime.now().plusDays(DEFAULT_DURATION);
        String expiryDate = defaultExpiryDate.format(formatter);
        return expiryDate;
    }

    ;


}