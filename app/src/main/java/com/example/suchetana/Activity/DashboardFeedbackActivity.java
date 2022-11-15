package com.example.suchetana.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Adapters.AdapterFeedback;
import com.example.suchetana.Models.ModelFeedback;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityDashboardFeedbackBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardFeedbackActivity extends AppCompatActivity {

    //view binding
    private ActivityDashboardFeedbackBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist to store feedbacks
    private ArrayList<ModelFeedback> feedbackArrayList;

    //adapter
    private AdapterFeedback adapterFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardFeedbackBinding.inflate(getLayoutInflater());
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
        loadFeedback();

        //handle click: back button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadFeedback() {
        //init arrayList
        feedbackArrayList = new ArrayList<>();

        //get all feedback from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feedback");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear before adding elements
                feedbackArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    //get data
                    ModelFeedback model = ds.getValue(ModelFeedback.class);

                    //add to arrayList
                    feedbackArrayList.add(model);
                }
                //setup adapter
                adapterFeedback = new AdapterFeedback(DashboardFeedbackActivity.this, feedbackArrayList);
                //set adapter to recyclerview
                binding.feedbackRv.setAdapter(adapterFeedback);
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