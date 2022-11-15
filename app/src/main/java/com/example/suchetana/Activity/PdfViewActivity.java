package com.example.suchetana.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.Constants;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityPdfViewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PdfViewActivity extends AppCompatActivity {
    //view binding
    private ActivityPdfViewBinding binding;

    private String bookId;
    private boolean isInMyDownload;

    //loading dialog
    private Dialog dialogLoad;

    private static final String TAG = "PDF_VIEW_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.orange));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //get bookId from intent we have passed on
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        isInMyDownload = intent.getBooleanExtra("isInMyDownload", false);
        Log.d(TAG, "onCreate: BookId: " + bookId);

        //init dialog
        dialogLoad = new Dialog(PdfViewActivity.this);
        dialogLoad.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoad.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLoad.setContentView(R.layout.dialog_loading);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.show();

        loadBookDetails();

        //handle click go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get pdf URL from db...");

        //Database reference to get book details e.g. book url, etc.
        //Step (1) Get book url using bookId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get url
                        String pdfUrl = "" + snapshot.child("pdfUrl").getValue();
                        Log.d(TAG, "onDataChange: Pdf URL: " + pdfUrl);

                        //Step (2) Load pdf using the url from firebase storage
                        loadBookFromUrl(pdfUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBookFromUrl(String pdfUrl) {
        Log.d(TAG, "loadBookFromUrl: Get book from storage");

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //if isInMyDownload
                        if(isInMyDownload) {
                            binding.pdfView.fromBytes(bytes)
                                    .onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            //set current and total pages in toolbar subtitle
                                            int currentPage = (page + 1); //do + 1 because page starts from 0
                                            binding.toolbarSubtitleTv.setText(currentPage + "/" + pageCount); //e.g. 3/290
                                            Log.d(TAG, "onPageChanged: " + currentPage + "/" + pageCount);
                                        }
                                    })
                                    .swipeHorizontal(true) //set false to scroll vertical, true to swipe horizontal
                                    .onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onError: " + t.getMessage());
                                        }
                                    })
                                    .onPageError(new OnPageErrorListener() {
                                        @Override
                                        public void onPageError(int page, Throwable t) {
                                            Toast.makeText(PdfViewActivity.this, "Error on page " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onPageError: " + t.getMessage());
                                        }
                                    })
                                    .load();;
                        }
                        else {
                            binding.pdfView.fromBytes(bytes)
                                    .pages(new int[]{0, 1, 2, 3, 4})
                                    .onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {
                                            //set current and total pages in toolbar subtitle
                                            int currentPage = (page + 1); //do + 1 because page starts from 0
                                            binding.toolbarSubtitleTv.setText(currentPage + "/" + "5"); //e.g. 3/5
                                            Log.d(TAG, "onPageChanged: " + currentPage + "/" + "5");
                                        }
                                    })
                                    .swipeHorizontal(true) //set false to scroll vertical, true to swipe horizontal
                                    .onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onError: " + t.getMessage());
                                        }
                                    })
                                    .onPageError(new OnPageErrorListener() {
                                        @Override
                                        public void onPageError(int page, Throwable t) {
                                            Toast.makeText(PdfViewActivity.this, "Error on page " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onPageError: " + t.getMessage());
                                        }
                                    })
                                    .load();
                            ;
                        }

                        dialogLoad.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to load
                        dialogLoad.dismiss();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
}