package com.example.suchetana.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityOrderDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class OrderDetailActivity extends AppCompatActivity {

    //view binding
    private ActivityOrderDetailBinding binding;

    //firebaseAuth
    private FirebaseAuth firebaseAuth;

    //intent data
    String orderId, bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
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

        //get data from intent
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        bookId = intent.getStringExtra("bookId");

        checkUser();

        binding.changeStatusBtn.setVisibility(View.GONE);

        //handle click, change status Btn
        binding.changeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = binding.changeStatusBtn.getText().toString();

                //confirm update dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                builder.setTitle(status + "?")
                        .setMessage("Are you sure you have " + status + " ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //begin update
                                updateStatus(status, orderId);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //handle click bookInfoRl
        binding.bookInfoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailActivity.this, PdfDetailActivity.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            }
        });

        //handle click return TV
        binding.returnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromDb(orderId, bookId);
            }
        });

        //handle click return next
        binding.returnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromDb(orderId, bookId);
            }
        });

        //handle click, backBtn
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void deleteFromDb(String orderId, String bookId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child(bookId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                        builder.setTitle("Cancel")
                                .setMessage("Are you sure to cancel the order?")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(OrderDetailActivity.this, "Not able to cancel", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatus(String status, String orderId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(TextUtils.equals(status, "Delivered")) {
                            MyApplication.incrementBookDownloadCount(bookId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });

    }

    private void checkUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // get user type
                        String userType = "" + snapshot.child("userType").getValue();
                        // check user type
                        if (userType.equals("admin")) {
                            // this is admin, open admin dashboard
                            binding.changeStatusBtn.setVisibility(View.VISIBLE);
                        }

                        loadBookDetails();
                        loadOrderDetails();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    private void loadOrderDetails() {
        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //get data
                        String name = "" + snapshot.child("name").getValue();
                        String address = "" + snapshot.child("address").getValue();
                        String mobile = "" + snapshot.child("mobile").getValue();
                        String city = "" + snapshot.child("city").getValue();
                        String pin = "" + snapshot.child("pin").getValue();
                        String state = "" + snapshot.child("state").getValue();
                        String country = "" + snapshot.child("country").getValue();
                        String status = "" + snapshot.child("status").getValue();
                        String mode = "" + snapshot.child("mode").getValue();
                        String type = "" + snapshot.child("type").getValue();
                        String qty = "" + snapshot.child("quantity").getValue();

                        //set data
                        binding.nameTv.setText(name);
                        binding.addressTv.setText(address);
                        binding.mobileTv.setText(mobile);
                        binding.cityTv.setText(city + " - " + pin);
                        binding.stateCountryTv.setText(state + ", " + country);
                        binding.statusTv.setText("Status: " + status);
                        binding.modeTv.setText("Payment mode: " + mode);
                        binding.typeTv.setText("Type: " + type);
                        binding.qtyTv.setText("Quantity: " + qty);

                        if (TextUtils.equals(status, "Ordered")) {
                            binding.changeStatusBtn.setText("Shipped");
                        } else if (TextUtils.equals(status, "Shipped")) {
                            binding.changeStatusBtn.setText("Delivered");

                            binding.connector1.setBackgroundResource(R.color.green);
                            binding.shippedView.setBackgroundResource(R.drawable.shape_status_current);

                            binding.returnTv.setVisibility(View.GONE);
                            binding.returnNext.setVisibility(View.GONE);

                            binding.divider.setVisibility(View.GONE);
                        } else if (TextUtils.equals(status, "Delivered")) {
                            binding.changeStatusBtn.setVisibility(View.GONE);

                            binding.connector1.setBackgroundResource(R.color.green);
                            binding.shippedView.setBackgroundResource(R.drawable.shape_status_current);

                            binding.connector2.setBackgroundResource(R.color.green);
                            binding.deliveredView.setBackgroundResource(R.drawable.shape_status_current);

                            binding.returnTv.setVisibility(View.GONE);
                            binding.returnNext.setVisibility(View.GONE);

                            binding.divider.setVisibility(View.GONE);
                        }

                        if (TextUtils.equals(type, "ebook")) {
                            binding.qtyTv.setVisibility(View.GONE);
                            binding.divider.setVisibility(View.GONE);
                            binding.returnTv.setVisibility(View.GONE);
                            binding.returnNext.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void loadBookDetails() {
        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //get data
                        String bookTitle = "" + snapshot.child("title").getValue();
                        String author = "" + snapshot.child("author").getValue();
                        String publisher = "" + snapshot.child("publisher").getValue();
                        String coverPageUrl = "" + snapshot.child("coverPageUrl").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();

                        MyApplication.loadCategory(
                                "" + categoryId,
                                binding.categoryTv
                        );

                        //set data
                        Glide.with(OrderDetailActivity.this)
                                .load(coverPageUrl)
                                .placeholder(R.drawable.back01)
                                .into(binding.pdfIv);
                        binding.titleTv.setText(bookTitle);
                        binding.authorTv.setText(author);
                        binding.publisherTv.setText(publisher);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }
}