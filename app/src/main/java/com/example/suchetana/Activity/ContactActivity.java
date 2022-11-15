package com.example.suchetana.Activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityContactBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {
    //view binding
    private ActivityContactBinding binding;

    private FirebaseAuth firebaseAuth;

    private MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.orange));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //to handle error showing
        binding.nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.nameTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.emailTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.messageTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDialog = new MaterialDialog.Builder(this)
                .setTitle("Thank You!")
                .setMessage("We have received your feedback.")
                .setAnimation(R.raw.thank_you_anim)
                .setCancelable(false)
                .setPositiveButton("Rate Us", R.drawable.ic_star_red, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Delete Operation
                        mDialog.dismiss();
                        onBackPressed();
                    }
                })
                .setNegativeButton("Not Now", R.drawable.ic_cross_red, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Delete Operation
                        mDialog.dismiss();
                        onBackPressed();
                    }
                })
                .build();

        //handle click, back btn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click, submit btn
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String name="",email="",message="";

    private void validateData() {
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        message = binding.messageEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            binding.nameTil.setError("Name Required*");
        }
        else if (TextUtils.isEmpty(email)){
            binding.emailTil.setError("Email Required*");
        }
        else if (TextUtils.isEmpty(message)){
            binding.messageTil.setError("Message Required*");
        }
        else {
            uploadData();
        }
    }

    private void uploadData() {
        //uid & timestamp
        String uid = firebaseAuth.getUid();
        long timestamp = System.currentTimeMillis();

        //setup data
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id","" + timestamp);
        hashMap.put("uid","" + uid);
        hashMap.put("name","" + name);
        hashMap.put("email","" + email);
        hashMap.put("message","" + message);

        //db reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feedback");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mDialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}