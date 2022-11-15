package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Adapters.AdapterOrderAdmin;
import com.example.suchetana.Models.ModelOrder;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityOrderListAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OrderListAdminActivity extends AppCompatActivity {

    //view binding
    private ActivityOrderListAdminBinding binding;

    //ArrayList of orders
    private ArrayList<ModelOrder> orderArrayList;

    //adapter for recyclerView
    private AdapterOrderAdmin adapterOrderAdmin;

    public static final String TAG = "ORDER_LIST_ADMIN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.orange));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //intent data
        Intent intent = getIntent();
        String status = intent.getStringExtra("status");
        Log.d(TAG, "onCreate: " + status);
        loadData(status);

        //handle click, backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadData(String status) {
        //init arrayList
        orderArrayList = new ArrayList<>();

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                orderArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    DataSnapshot dataSnapshot = ds.child("Orders");

                    for (DataSnapshot dsnap : dataSnapshot.getChildren()) {
                        for (DataSnapshot snap : dsnap.getChildren()) {
                            //get data
                            ModelOrder order = snap.getValue(ModelOrder.class);

                            //check status
                            if (TextUtils.equals(order.getStatus(), status)) {
                                //add to list
                                orderArrayList.add(order);
                            }
                        }
                    }
                }

                Log.d(TAG, "onDataChange: " + orderArrayList.size());
                //setup adapter
                adapterOrderAdmin = new AdapterOrderAdmin(OrderListAdminActivity.this, orderArrayList);
                binding.itemRv.setAdapter(adapterOrderAdmin);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}