package com.example.icooking.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icooking.ui.Inventory.Inventory;
import com.example.icooking.R;
import com.example.icooking.databinding.FragmentHomeBinding;
import com.example.icooking.helper.RecyclerTouchListener;
import com.example.icooking.ui.Inventory.DAOInventory;
import com.example.icooking.ui.Inventory.InventoryFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements HomeAdaptor.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private View view;
    private FragmentHomeBinding binding;
    private HomeAdaptor buyadaptor;
    private ArrayList<Buylist> buylist = new ArrayList<Buylist>();
    private DAObuylist daoBuylist;

    private DAOInventory daoInventory;
    private String key = null;
    private Button buybtm;
    private Button delbtm;
    private Button addbtm;
    private BottomSheetDialog bottomSheetDialogs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        //get RECYCLERVIEW
        final RecyclerView mRvbuy = binding.buyMain;
        final Button buybtm = binding.BoughtButton;
        final Button delbtm = binding.deletebtn;
        final Button addbum = binding.addbtn;
        //sent data to ingredient(button)
        buybtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buylist = buyadaptor.getBuylist();
                int count = buylist.size();
                String tmpkey = null;
                ArrayList temp = new ArrayList();
                ArrayList<String> keyArrayList = new ArrayList<String>();
                Map<String, Inventory> inventoryMap = new HashMap<>();
                for (int i = 0; i < count; i++) {

                    if (buylist.get(i).isIschecked()) {
                        //ingredient name
                        temp.add(buylist.get(i));
                        tmpkey = buyadaptor.getKey(i);
                        keyArrayList.add(tmpkey);
                        String etName = buylist.get(i).getBuyName();
                        // use static method from inventoryframent.
                        daoInventory.add(new Inventory(etName, InventoryFragment.getDefaultDateString()))
                                .addOnSuccessListener(success -> {
                                    Toast.makeText(getContext(), "Add ingredient successfully", Toast.LENGTH_SHORT).show();

                                }).addOnFailureListener(err -> {
                            Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }


                }

                int counter = keyArrayList.size();
                for(int j = 0; j < counter;j++){
                    daoBuylist.remove(keyArrayList.get(j));
                }

                buyadaptor.notifyDataSetChanged();

            }

        });

        //delete function
        delbtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buylist = buyadaptor.getBuylist();
                int count = buylist.size();
                ArrayList temp = new ArrayList();
                String tmpkey = null;
                ArrayList<String> keyArrayList = new ArrayList<String>();
                for (int i = 0; i < count; i++) {

                    if (buylist.get(i).isIschecked()) {
                        //ingredient name
                        temp.add(buylist.get(i));
                        tmpkey = buyadaptor.getKey(i);
                        keyArrayList.add(tmpkey);
                    }
                    int counter = keyArrayList.size();
                    for(int j = 0; j < counter;j++){
                        daoBuylist.remove(keyArrayList.get(j));
                    }
                }

                buyadaptor.notifyDataSetChanged();

            }

        });
        addbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buylist = buyadaptor.getBuylist();
                bottomSheetDialogs = new BottomSheetDialog(getContext());
                bottomSheetDialogs.setContentView(R.layout.bottom_sheet_dialog_buy);
                bottomSheetDialogs.setCanceledOnTouchOutside(true);
                bottomSheetDialogs.setDismissWithAnimation(true);
                EditText editName = bottomSheetDialogs.findViewById(R.id.et_tobuyName);
                Button bsubmit = bottomSheetDialogs.findViewById(R.id.btn_addsubmit);
                bsubmit.setText("Add");
                assert bsubmit != null;
                bsubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        assert editName != null;
                        if (!InventoryFragment.validateIngredientName(editName)) {
                            return;}
                        buyadaptor.notifyDataSetChanged();

                        daoBuylist.add(new Buylist(editName.getText().toString()))
                                .addOnSuccessListener(success -> {
                                    Toast.makeText(getContext(),"Add ingredient successfully",Toast.LENGTH_SHORT).show();
                                    bottomSheetDialogs.dismiss();
                                }).addOnFailureListener(err->{
                            Toast.makeText(getContext(),err.getMessage(),Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                bottomSheetDialogs.show();


            }
        });
        //create ADAPTER
        buyadaptor = new

                HomeAdaptor(this, buylist);
        //set recyclerview for adapter
        mRvbuy.setAdapter(buyadaptor);
        //set layoutmanager
        mRvbuy.setLayoutManager(new

                LinearLayoutManager(getContext()));
        daoBuylist = new DAObuylist();
        daoInventory = new DAOInventory();
        fetchBuyData();

        RecyclerTouchListener touchListener = new RecyclerTouchListener(getActivity(), mRvbuy);

        mRvbuy.addOnItemTouchListener(touchListener);
        //final View view
        return root;
    }

    @Override
    public void addbuy(Buylist buy) {
        buylist.add(0, buy);
        buyadaptor.setBuys(buylist);
    }

    @Override
    public void removebuy(int position) {
        buylist.remove(position);
        buyadaptor.setBuys(buylist);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * This method is used to fetch Inventory data form database.
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
                buyadaptor.setInventory(inventoryList);
                buyadaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void fetchBuyData() {

        daoBuylist.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Buylist> buylists = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Buylist buylist = data.getValue(Buylist.class);
                    buylist.setKey(data.getKey());
                    buylists.add(buylist);
                }
                buyadaptor.setBuys(buylists);
                buyadaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}