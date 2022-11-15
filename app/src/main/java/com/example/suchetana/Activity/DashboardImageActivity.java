package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Adapters.AdapterSliderImage;
import com.example.suchetana.Models.ModelSliderImage;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityDashboardImageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardImageActivity extends AppCompatActivity {

    //view binding
    private ActivityDashboardImageBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arrayList to store sliderImage
    private ArrayList<ModelSliderImage> imageArrayList;

    //adapter
    private AdapterSliderImage adapterSliderImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //function to set email in textView
        loadEmail();
        loadImages();

        //handle click: back button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click: add imageFAB
        binding.addImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardImageActivity.this, SliderImageAddActivity.class));
            }
        });
    }

    private void loadImages() {
        //init arrayList
        imageArrayList = new ArrayList<>();

        //get all images from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Images");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arrayList before adding data to it
                imageArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelSliderImage model = ds.getValue(ModelSliderImage.class);

                    //add category to arrayList
                    imageArrayList.add(model);
                }
                //setup adapter
                adapterSliderImage = new AdapterSliderImage(DashboardImageActivity.this, imageArrayList);
                //set adapter to recyclerView
                binding.imagesRv.setAdapter(adapterSliderImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadEmail() {
        //db ref for email Users > user uid > email
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get email
                String email = "" + snapshot.child("email").getValue();
                //set email in subtitleTv
                binding.subtitleTv.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}