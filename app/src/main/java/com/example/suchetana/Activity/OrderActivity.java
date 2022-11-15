package com.example.suchetana.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Adapters.AdapterOrder;
import com.example.suchetana.Models.ModelOrder;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityOrderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    //view binding
    private ActivityOrderBinding binding;

    //ArrayList to store Orders
    private ArrayList<ModelOrder> orderArrayList;

    //adapter
    private AdapterOrder adapterOrder;

    //Firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.orange));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        loadOrders();

        //handle click, backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadOrders() {
        //init arrayList
        orderArrayList = new ArrayList<>();

        //Load from db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        orderArrayList.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                //get data
                                ModelOrder order = ds.getValue(ModelOrder.class);

                                //add to list
                                orderArrayList.add(order);
                            }
                        }
                        //setup adapter
                        adapterOrder = new AdapterOrder(OrderActivity.this, orderArrayList);
                        binding.itemRv.setAdapter(adapterOrder);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }
}