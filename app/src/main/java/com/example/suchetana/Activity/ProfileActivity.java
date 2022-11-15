package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.suchetana.Adapters.AdapterComment;
import com.example.suchetana.Adapters.AdapterPdfFavorite;
import com.example.suchetana.Models.ModelComment;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    //view binding
    private ActivityProfileBinding binding;

    //firebaseAuth for loading user data using user uid
    private FirebaseAuth firebaseAuth;

    //arrayList to hold the books
    private ArrayList<ModelPdf> pdfArrayList;
    //arrayList to hold comment
    private ArrayList<ModelComment> commentArrayList;

    //adapter to set in recyclerView
    private AdapterPdfFavorite adapterPdfFavorite;
    private AdapterComment adapterComment;

    public static final String TAG = "PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        loadFavoriteBooks();

        loadComments();

        //handle click, start profile edit page
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });

        //handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadComments() {
        //init arrayList before inserting data to it
        commentArrayList = new ArrayList<>();

        //db path to load comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref//.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arrayList before adding data into it
                        commentArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DataSnapshot snap = ds.child("Comments");
                            for (DataSnapshot comments: snap.getChildren()) {
                                //get data as model, spellings in variables must be same as in firebase
                                if (TextUtils.equals(comments.child("uid").getValue().toString(), firebaseAuth.getUid())) {
                                    ModelComment model = comments.getValue(ModelComment.class);
                                    //add to arrayList
                                    commentArrayList.add(model);
                                }
                            }
                        }
                        //setup adapter
                        adapterComment = new AdapterComment(ProfileActivity.this, commentArrayList);
                        //set adapter to recyclerView
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFavoriteBooks() {
        //init list
        pdfArrayList = new ArrayList<>();

        //load favorite books from database
        //Users > userId > Favorites
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arrayList before adding to list
                        pdfArrayList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //we will only get bookId here, all the other details we will get from adapter
                            String bookId = "" + ds.child("bookId").getValue();

                            //set id to model
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            //add model to list
                            pdfArrayList.add(modelPdf);
                        }

                        //set number of favorite books
                        binding.favoriteBookCountTv.setText("" + pdfArrayList.size());

                        //setup adapter
                        adapterPdfFavorite = new AdapterPdfFavorite(ProfileActivity.this, pdfArrayList);
                        //set adapter to recyclerView
                        binding.booksRv.setAdapter(adapterPdfFavorite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info of user " + firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info of user here from snapshot
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String address = "" + snapshot.child("address").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String userType = "" + snapshot.child("userType").getValue();

                        //format date to dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data to ui
                        binding.emailTv.setText(email);
                        binding.addressTv.setText(address);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);

                        //set image using glide
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.profileIv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}