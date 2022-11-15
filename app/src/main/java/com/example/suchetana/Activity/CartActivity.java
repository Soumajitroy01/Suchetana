package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Adapters.AdapterCart;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    //view binding
    private ActivityCartBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arrayList to hold pdf
    private ArrayList<ModelPdf> pdfArrayList;
    private ArrayList<String> priceArrayList, qtyArrayList, typeArrayList;

    //adapter
    private AdapterCart adapterCart;

    //boolean to check whether address is present
    boolean isAddress = true;

    //email and phone number of user
    private String email, mobile;

    private static final String TAG = "CART_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.orange));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Checkout.preload(CartActivity.this);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            binding.bodySv.setVisibility(View.VISIBLE);
            binding.submitBtn.setVisibility(View.VISIBLE);
            binding.emptyCartIv.setVisibility(View.GONE);
        }

        loadCart();
        loadAddress();
        loadUserDetails();

        //handle click add new address
        binding.addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, AddAddressActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CartActivity.this.startActivity(intent);
                finish();
            }
        });

        //handle click delete address
        binding.deleteAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAddress) {
                    MyApplication.deleteAddress(CartActivity.this);
                    loadAddress();
                }
            }
        });

        //handle click submit Btn
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        //handle click back button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void validateData() {
        if (!isAddress) {
            Toast.makeText(this, "Add address", Toast.LENGTH_SHORT).show();
        } else {
            int checkId = binding.paymentRadioGrp.getCheckedRadioButtonId();
            switch (checkId) {
                case R.id.offlineRadioBtn:
                    break;
                case R.id.onlineRadioBtn:
                    startPayment();
                    break;
            }
        }
    }

    private void startPayment() {
        //initiate razorpay checkout
        Checkout checkout = new Checkout();
        //set key id
        checkout.setKeyID("rzp_test_xGDIs1zmD2hJOo");
        int amount = Math.round(Float.parseFloat(binding.totalPriceTv.getText().toString().substring(1)) * 100);

        String id = "";
        try {
            RazorpayClient razorpay = new RazorpayClient("rzp_test_xGDIs1zmD2hJOo", "N49HGQDwqVmbXhlhAvGH0WAP");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");

            Order order = razorpay.orders.create(orderRequest);
            id = order.get("id");
        } catch (Exception e) {
            // Handle Exception
            Log.d(TAG, "startPayment: " + e.getMessage());
        }

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Suchetana");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", id);//from response of step 3.
            options.put("theme.color", "#F36F21");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact", mobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(CartActivity.this, options);

        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    private void loadUserDetails() {
        firebaseAuth = FirebaseAuth.getInstance();

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        email = "" + snapshot.child("email").getValue();
                        mobile = "" + snapshot.child("mobile").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    String name = "", address = "", phone = "", city = "", pin = "", state = "", country = "";

    private void loadAddress() {
        //init boolean
        isAddress = true;

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isAddress = snapshot.exists();

                        if (!isAddress) {
                            binding.addressCv.setVisibility(View.GONE);
                            binding.addAddressBtn.setVisibility(View.VISIBLE);
                        } else {
                            binding.addressCv.setVisibility(View.VISIBLE);
                            binding.addAddressBtn.setVisibility(View.GONE);

                            //get data
                            name = "" + snapshot.child("name").getValue();
                            address = "" + snapshot.child("address").getValue();
                            phone = "" + snapshot.child("mobile").getValue();
                            city = "" + snapshot.child("city").getValue();
                            pin = "" + snapshot.child("pin").getValue();
                            state = "" + snapshot.child("state").getValue();
                            country = "" + snapshot.child("country").getValue();

                            //set data
                            binding.nameTv.setText(name);
                            binding.addressTv.setText(address);
                            binding.mobileTv.setText(phone);
                            binding.cityTv.setText(city + " - " + pin);
                            binding.stateCountryTv.setText(state + ", " + country);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCart() {
        //init arrayList
        pdfArrayList = new ArrayList<>();
        qtyArrayList = new ArrayList<>();
        priceArrayList = new ArrayList<>();
        typeArrayList = new ArrayList<>();

        //get data
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        qtyArrayList.clear();
                        priceArrayList.clear();

                        //get data
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String bookId = "" + ds.child("id").getValue();
                            String qty = "" + ds.child("quantity").getValue();
                            String price = "" + ds.child("price").getValue();
                            String type = "" + ds.child("type").getValue();

                            //set id to model
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            //add model to list
                            pdfArrayList.add(modelPdf);
                            qtyArrayList.add(qty);
                            priceArrayList.add(price);
                            typeArrayList.add(type);
                        }

                        if (pdfArrayList.size() == 0) {
                            binding.bodySv.setVisibility(View.GONE);
                            binding.submitBtn.setVisibility(View.GONE);
                            binding.emptyCartIv.setVisibility(View.VISIBLE);
                        }

                        //setup adapter
                        adapterCart = new AdapterCart(CartActivity.this, pdfArrayList, priceArrayList, qtyArrayList, typeArrayList,
                                binding.cartTotalPriceTv, binding.deliveryPriceTv, binding.totalPriceTv);

                        binding.itemRv.setAdapter(adapterCart);

                        int itemPrice = 0;
                        for (int i = 0; i < priceArrayList.size(); i++) {
                            itemPrice += Integer.parseInt(qtyArrayList.get(i)) * Integer.parseInt(priceArrayList.get(i));
                        }
                        binding.cartTotalPriceTv.setText("₹" + itemPrice);

                        if (itemPrice > 499) {
                            binding.deliveryPriceTv.setText("Free");
                            binding.totalPriceTv.setText("₹" + itemPrice);
                        } else {
                            binding.deliveryPriceTv.setText("₹40");
                            binding.totalPriceTv.setText("₹" + (itemPrice + 40));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        long timestamp = System.currentTimeMillis();
        this.pdfArrayList = adapterCart.pdfArrayList;
        this.qtyArrayList = adapterCart.qtyArrayList;
        this.priceArrayList = adapterCart.priceArrayList;
        this.typeArrayList = adapterCart.typeArrayList;

        //add pdf to users>uid>downloads>bookId
        for (int i = 0; i < pdfArrayList.size(); i++) {
            if (TextUtils.equals(typeArrayList.get(i), "ebook")) {
                ModelPdf model = pdfArrayList.get(i);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("bookId", model.getId());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(firebaseAuth.getUid()).child("Downloads").child(model.getId())
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                MyApplication.incrementBookDownloadCount(model.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {

                            }
                        });
            }
        }
        //add order details within user
        for (int i = 0; i < pdfArrayList.size(); i++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("bookId", pdfArrayList.get(i).getId());
            hashMap.put("userUid", firebaseAuth.getUid());
            hashMap.put("name", name);
            hashMap.put("address", address);
            hashMap.put("mobile", phone);
            hashMap.put("city", city);
            hashMap.put("pin", pin);
            hashMap.put("state", state);
            hashMap.put("country", country);
            hashMap.put("quantity", qtyArrayList.get(i));
            hashMap.put("price", priceArrayList.get(i));
            hashMap.put("type", typeArrayList.get(i));
            hashMap.put("orderId", paymentData.getPaymentId());
            hashMap.put("mode", "Online");
            hashMap.put("timestamp", timestamp);

            if (TextUtils.equals(typeArrayList.get(i), "ebook"))
                hashMap.put("status", "Delivered");
            else
                hashMap.put("status", "Ordered");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Orders").child(paymentData.getPaymentId()).child(pdfArrayList.get(i).getId())
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });
        }

        //refresh cart
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Cart")
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });

        //goto order activity
        startActivity(new Intent(CartActivity.this, OrderActivity.class));
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }
}