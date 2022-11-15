package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityOrderAdminBinding;

public class OrderAdminActivity extends AppCompatActivity {
    //view binding
    private ActivityOrderAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //handle click, backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click, ordered
        binding.orderedCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto orderListAdminActivity
                Intent intent = new Intent(OrderAdminActivity.this, OrderListAdminActivity.class);
                intent.putExtra("status", "Ordered");
                startActivity(intent);
            }
        });

        //handle click, shipped
        binding.shippedCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto orderListAdminActivity
                Intent intent = new Intent(OrderAdminActivity.this, OrderListAdminActivity.class);
                intent.putExtra("status", "Shipped");
                startActivity(intent);
            }
        });

        //handle click, delivered
        binding.deliveredCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto orderListAdminActivity
                Intent intent = new Intent(OrderAdminActivity.this, OrderListAdminActivity.class);
                intent.putExtra("status", "Delivered");
                startActivity(intent);
            }
        });
    }
}