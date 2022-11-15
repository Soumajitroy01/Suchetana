package com.example.suchetana.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityAddAddressBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddAddressActivity extends AppCompatActivity {

    //view binding
    private ActivityAddAddressBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress Dialog
    private Dialog dialogLoad;

    private static final String TAG = "SPINNER_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
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

        //init progress dialog
        dialogLoad = new Dialog(AddAddressActivity.this);
        dialogLoad.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoad.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLoad.setContentView(R.layout.dialog_loading);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.show();

        //load country, states & cities respectively
        loadCountry();

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

        binding.addressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.addressTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.mobileEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.mobileTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.countryTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.countryTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.stateTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.stateTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.cityTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.cityTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.pinEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.pinTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //handle click submit btn
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        //handle click backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void validateData() {
        //validate data
        if (TextUtils.isEmpty(binding.nameEt.getText().toString().trim())) {
            binding.nameTil.setError("Name required*");
        } else if (TextUtils.isEmpty(binding.addressEt.getText().toString().trim())) {
            binding.addressTil.setError("Address required*");
        } else if (TextUtils.isEmpty(binding.mobileEt.getText().toString().trim())) {
            binding.mobileEt.setError("Mobile No. required*");
        } else if (binding.mobileEt.getText().toString().trim().length()!=10) {
            binding.mobileEt.setError("Mobile No. invalid*");
        } else if (TextUtils.isEmpty(binding.countryTv.getText().toString().trim())) {
            binding.countryTil.setError("Country required*");
        } else if (TextUtils.isEmpty(binding.stateTv.getText().toString().trim())) {
            binding.stateTil.setError("State required*");
        } else if (TextUtils.isEmpty(binding.cityTv.getText().toString().trim())) {
            binding.cityTil.setError("City required*");
        } else if (TextUtils.isEmpty(binding.pinEt.getText().toString())) {
            binding.pinTil.setError("Pin code required*");
        } else {
            updateData();
        }
    }

    private void updateData() {
        firebaseAuth = FirebaseAuth.getInstance();

        //get data
        String name = binding.nameEt.getText().toString().trim();
        String address = binding.addressEt.getText().toString().trim();
        String mobile = binding.mobileEt.getText().toString().trim();
        String country = binding.countryTv.getText().toString().trim();
        String state = binding.stateTv.getText().toString().trim();
        String city = binding.cityTv.getText().toString().trim();
        String pin = binding.pinEt.getText().toString().trim();

        //setup data to upload to dataBase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("address", address);
        hashMap.put("mobile", mobile);
        hashMap.put("country", country);
        hashMap.put("state", state);
        hashMap.put("city", city);
        hashMap.put("pin", pin);

        //upload to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Address")
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(AddAddressActivity.this, CartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AddAddressActivity.this.startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAddressActivity.this, "Failed due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCountry() {
        ArrayList<String> countryList = new ArrayList<>();
        HashMap<String, Integer> countryHashMap = new HashMap<>();

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Country");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countryList.clear();

                int i = 0;
                //get data
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String country = "" + ds.child("name").getValue();

                    //add to list
                    countryList.add(country);
                    countryHashMap.put(country, i);
                    i++;
                }

                //dismiss dialog
                dialogLoad.dismiss();

                ArrayAdapter<String> countryListAdapter = new ArrayAdapter<>(AddAddressActivity.this, R.layout.drop_down, countryList);

                binding.countryTv.setAdapter(countryListAdapter);

                binding.countryTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int countryPos, long l) {
                        ArrayList<String> stateList = new ArrayList<>();
                        HashMap<String, Integer> stateHashMap = new HashMap<>();
                        Integer curCountryPos = countryHashMap.get(binding.countryTv.getText().toString());

                        Log.d(TAG, "onItemClick: country: " + binding.countryTv.getText());
                        Log.d(TAG, "onItemClick: countryPos: " + curCountryPos);

                        //get data
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Country");
                        ref.child("" + curCountryPos).child("states")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        stateList.clear();

                                        int i = 0;
                                        //get data
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            String state = "" + ds.child("name").getValue();

                                            //add to list
                                            stateList.add(state);
                                            stateHashMap.put(state, i);
                                            i++;
                                        }

                                        ArrayAdapter<String> stateListAdapter = new ArrayAdapter<>(AddAddressActivity.this, R.layout.drop_down, stateList);

                                        binding.stateTv.setAdapter(stateListAdapter);

                                        binding.stateTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                final ArrayList<String> cityList = new ArrayList<>();
                                                Integer curStatePos = stateHashMap.get(binding.stateTv.getText().toString());

                                                //get data
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Country");
                                                ref.child("" + curCountryPos).child("states").child("" + curStatePos).child("cities")
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                cityList.clear();

                                                                //get data
                                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                                    String city = "" + ds.child("name").getValue();

                                                                    //add to list
                                                                    cityList.add(city);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                ArrayAdapter<String> cityListAdapter = new ArrayAdapter<>(AddAddressActivity.this, R.layout.drop_down, cityList);

                                                binding.cityTv.setAdapter(cityListAdapter);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}