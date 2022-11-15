package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Adapters.AdapterPdfUser;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity {
    //view binding
    private ActivitySearchBinding binding;

    //arrayList to hold data of type ModelPdf
    private ArrayList<ModelPdf> pdfArrayList;

    //get intent
    private String categoryId, categoryTitle;

    //adapter
    private AdapterPdfUser adapterPdfUser;

    private static final String TAG = "SEARCH_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        loadPdfList();

        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //search as and when user type each letter
                try {
                    adapterPdfUser.getFilter().filter(s);
                } catch (Exception e) {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle click, goto previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        //init list before adding data
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        if (TextUtils.equals(categoryTitle,"MostViewed") || TextUtils.equals(categoryTitle,"MostDownloaded")) {
            ref.orderByChild(categoryId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pdfArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //get data
                                ModelPdf model = ds.getValue(ModelPdf.class);
                                //add to list
                                pdfArrayList.add(model);

                                Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                            }
                            Collections.reverse(pdfArrayList);

                            //setup adapter
                            adapterPdfUser = new AdapterPdfUser(SearchActivity.this, pdfArrayList);
                            binding.bookRv.setAdapter(adapterPdfUser);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else if (!TextUtils.isEmpty(categoryId)) {
            ref.orderByChild("categoryId").equalTo(categoryId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pdfArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //get data
                                ModelPdf model = ds.getValue(ModelPdf.class);
                                //add to list
                                pdfArrayList.add(model);

                                Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                            }

                            //setup adapter
                            adapterPdfUser = new AdapterPdfUser(SearchActivity.this, pdfArrayList);
                            binding.bookRv.setAdapter(adapterPdfUser);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pdfArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        //get data
                        ModelPdf model = ds.getValue(ModelPdf.class);
                        //add to list
                        pdfArrayList.add(model);

                        Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                    }

                    //setup adapter
                    adapterPdfUser = new AdapterPdfUser(SearchActivity.this, pdfArrayList);
                    binding.bookRv.setAdapter(adapterPdfUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}