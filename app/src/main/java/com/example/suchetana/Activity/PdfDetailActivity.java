package com.example.suchetana.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.suchetana.Adapters.AdapterComment;
import com.example.suchetana.Models.ModelComment;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityPdfDetailBinding;
import com.example.suchetana.databinding.DialogCommentAddBinding;
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

public class PdfDetailActivity extends AppCompatActivity {
    //view binding
    private ActivityPdfDetailBinding binding;

    //pdfId, get from intent
    String bookId, bookTitle, bookUrl;

    boolean isInMyFavorite = false;
    boolean isInMyDownload = false;

    private FirebaseAuth firebaseAuth;

    public static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    //progress dialog
    private ProgressDialog progressDialog;
    private Dialog dialogLoad;

    //arrayList to store comments
    private ArrayList<ModelComment> commentArrayList;

    //adapter for comment
    private AdapterComment adapterComment;

    private static final String TAG = "PDF_DETAIL_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.orange));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //get data from intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //setup dialog
        dialogLoad = new Dialog(PdfDetailActivity.this);
        dialogLoad.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoad.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLoad.setContentView(R.layout.dialog_loading);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.show();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Log.d(TAG, "onCreate: called" );
            checkIsFavorite();
            checkIsDownload();
        }

        loadBookDetails();
        loadComments();
        //increment book views count, whenever this page is visited
        MyApplication.incrementBookViewCount(bookId);

        //handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, open to view pdf
        binding.readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent1.putExtra("bookId", bookId);
                intent1.putExtra("isInMyDownload", isInMyDownload);
                startActivity(intent1);
            }
        });

        //handle click add to cart Btn
        binding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null) {
                    MyApplication.addToCart(PdfDetailActivity.this, bookId, price, binding.typeRadioGrp.getCheckedRadioButtonId());
                }
                else {
                    Toast.makeText(PdfDetailActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //handle click, add/remove favorite
        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(PdfDetailActivity.this, "You're not logged in", Toast.LENGTH_SHORT).show();
                } else {
                    if (isInMyFavorite) {
                        //in favorite, remove from favorite
                        MyApplication.removeFromFavorite(PdfDetailActivity.this, "" + bookId);
                    } else {
                        //not in favorite, add to favorite
                        MyApplication.addToFavorite(PdfDetailActivity.this, "" + bookId);
                    }
                }
            }
        });

        //handle click, comment add dialog
        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Requirements: User must be logged in to add comments*/
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(PdfDetailActivity.this, "You're not logged in...", Toast.LENGTH_SHORT).show();
                } else {
                    addCommentDialog();
                }
            }
        });

        //handle change in radio group
        binding.typeRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.hardcoverRadioBtn:
                        binding.cartBtn.setVisibility(View.VISIBLE);
                        break;
                    case R.id.ebookRadioBtn:
                        if (isInMyDownload) {
                            //exists in downloads
                            binding.cartBtn.setVisibility(View.GONE);
                        }
                        else {
                            //not exists in downloads
                            binding.cartBtn.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });
    }

    private void checkIsDownload() {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //not logged in, don't check
            Toast.makeText(PdfDetailActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //logged in, check if it's in downloads list or not
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Downloads").child(bookId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            isInMyDownload = snapshot.exists(); //true if exists, false if not exists

                            if(isInMyDownload) {
                                binding.readBtn.setText("Read");
                            }
                            else {
                                binding.readBtn.setText("Preview");
                            }

                            binding.cartBtn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void loadComments() {
        //init arrayList before inserting data to it
        commentArrayList = new ArrayList<>();

        //db path to load comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arrayList before adding data into it
                        commentArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            //get data as model, spellings in variables must be same as in firebase
                            ModelComment model = ds.getValue(ModelComment.class);
                            //add to arrayList
                            commentArrayList.add(model);
                        }
                        //setup adapter
                        adapterComment = new AdapterComment(PdfDetailActivity.this,commentArrayList);
                        //set adapter to recyclerView
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String comment = "";

    private void addCommentDialog() {
        //inflate bind view for dialog
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        //setup alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());

        //create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //handle click, dismiss dialog
        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //handle click add comment
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data
                comment = commentAddBinding.commentEt.getText().toString().trim();

                //validate data
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(PdfDetailActivity.this, "Enter your comment...", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    private void addComment() {
        //show progress
        progressDialog.setMessage("Adding comment");
        progressDialog.show();

        //timestamp for comment id, comment time
        String timestamp = "" + System.currentTimeMillis();

        //setup data to add in db for comment
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + timestamp);
        hashMap.put("bookId", "" + bookId);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("comment", "" + comment);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        //DB path to add data into it
        //Books > bookId > Comments > commentId > commentData
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfDetailActivity.this, "Comment added...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to add comment
                        progressDialog.dismiss();
                        Toast.makeText(PdfDetailActivity.this, "Failed to add comment due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //request storage permission
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG_DOWNLOAD, "Permission Granted");
                    MyApplication.downloadBook(this, "" + bookId, "" + bookTitle, "" + bookUrl);
                } else {
                    Log.d(TAG_DOWNLOAD, "Permission was denied...");
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });

    Long price;

    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        bookTitle = "" + snapshot.child("title").getValue();
                        String author = "" + snapshot.child("author").getValue();
                        String publisher = "" + snapshot.child("publisher").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        price  = Long.parseLong("" + snapshot.child("price").getValue());
                        String coverPageUrl = "" + snapshot.child("coverPageUrl").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        bookUrl = "" + snapshot.child("pdfUrl").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();

                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(
                                "" + categoryId,
                                binding.categoryTv
                        );

                        MyApplication.loadPageCount(
                                "" + bookUrl,
                                "" + bookTitle,
                                dialogLoad,
                                binding.pdfView,
                                binding.pagesTv
                        );

                        //set data
                        Glide.with(PdfDetailActivity.this)
                                .load(coverPageUrl)
                                .placeholder(R.drawable.back01)
                                .into(binding.pdfIv);
                        binding.titleTv.setText(bookTitle);
                        binding.authorTv.setText(author);
                        binding.publisherTv.setText(publisher);
                        binding.descriptionTv.setText(description);
                        binding.viewsTv.setText(viewsCount.replace("null", "N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null", "N/A"));
                        binding.priceTv.setText("₹"+price);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsFavorite() {
        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "checkIsFavorite: called");
        if (firebaseAuth.getCurrentUser() == null) {
            //not logged in, don't check
            Toast.makeText(PdfDetailActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //logged in, check if it's in favorite list or not
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            isInMyFavorite = snapshot.exists(); //true if exists, false if not exists

                            if (isInMyFavorite) {
                                //exists in favorite
                                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_orange);
                            } else {
                                //not exists in favorite
                                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_border_orange);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}