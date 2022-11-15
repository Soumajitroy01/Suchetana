package com.example.suchetana.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfEditActivity extends AppCompatActivity {

    //view binding
    private ActivityPdfEditBinding binding;

    //bookId get from intent started from AdapterPdfAdmin activity
    private String bookId;

    //image uri
    private Uri imageUri;

    //progress dialog
    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    private static final String TAG = "BOOK_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //bookId get from intent started from AdapterPdfAdmin activity
        bookId = getIntent().getStringExtra("bookId");

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadBookInfo();

        //handle click, pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        //handle click, go to back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, select image
        binding.coverPageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAttachMenu();
            }
        });

        //handle click, begin upload
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void loadBookInfo() {
        Log.d(TAG, "loadBookInfo: Loading Book Info...");

        DatabaseReference refBooks = FirebaseDatabase.getInstance().getReference("Books");
        refBooks.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book info
                        selectedCategoryId = "" + snapshot.child("categoryId").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String title = "" + snapshot.child("title").getValue();
                        String author = "" + snapshot.child("author").getValue();
                        String coverPageUrl = "" + snapshot.child("coverPageUrl").getValue();
                        String price = "" + snapshot.child("price").getValue();
                        String publisher = "" + snapshot.child("publisher").getValue();
                        String isbn = "" + snapshot.child("isbn").getValue();

                        imageUrl = coverPageUrl;

                        progressDialog.setMessage("Loading");
                        progressDialog.show();

                        //set to view
                        binding.titleEt.setText(title);
                        binding.authorEt.setText(author);
                        binding.descriptionEt.setText(description);
                        binding.priceEt.setText("" + price);
                        binding.publisherEt.setText(publisher);
                        binding.isbnEt.setText(isbn);
                        Glide.with(PdfEditActivity.this)
                                .load(coverPageUrl)
                                .placeholder(R.drawable.ic_down_gray)
                                .into(binding.coverPageIv);

                        Log.d(TAG, "onDataChange: Loading book category info");
                        DatabaseReference refBookCategory = FirebaseDatabase.getInstance().getReference("Categories");
                        refBookCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get category
                                        String category = "" + snapshot.child("category").getValue();
                                        //set text to view
                                        binding.categoryTv.setText(category);
                                        progressDialog.dismiss();
                                        Toast.makeText(PdfEditActivity.this, "Loaded Successfully", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(PdfEditActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title = "", description = "", author= "", price = "", publisher = "", isbn = "", imageUrl="";

    private void validateData() {
        //get data
        title = binding.titleEt.getText().toString().trim();
        author = binding.authorEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        publisher = binding.publisherEt.getText().toString().trim();
        isbn = binding.isbnEt.getText().toString().trim();
        selectedCategoryTitle = binding.categoryTv.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Insert author", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Enter price", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(publisher)) {
            Toast.makeText(this, "Enter publisher", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(isbn)) {
            Toast.makeText(this, "Enter ISBN", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Pick category", Toast.LENGTH_SHORT).show();
        } else if (imageUri !=null) {
            //show progress
            progressDialog.setMessage("Uploading Image");
            progressDialog.show();

            //delete image
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            reference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            //image path and name, use uid to replace previous
            String imageFilePathAndName = "Cover Pages/" + title;

            //storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageFilePathAndName);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            String uploadedImageUrl = "" + uriTask.getResult();

                            //upload to firebase db
                            updatePdf(uploadedImageUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PdfEditActivity.this, "Failed to upload image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            updatePdf("");
        }
    }

    private void updatePdf(String uploadedImageUrl) {
        Log.d(TAG, "updatePdf: Starting Updating pdf info to db");

        //show progress
        progressDialog.setMessage("Updating Book Info");
        progressDialog.show();

        //setup data to update to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", "" + title);
        hashMap.put("author", "" + author);
        hashMap.put("description", "" + description);
        hashMap.put("price", "" + Long.parseLong(price));
        hashMap.put("publisher", "" + publisher);
        hashMap.put("isbn", "" + isbn);
        hashMap.put("categoryId", "" + selectedCategoryId);
        if (imageUri != null) {
            hashMap.put("coverPageUrl", "" + uploadedImageUrl);
        }

        //load category title
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Book Updated...");
                        progressDialog.dismiss();

                        updatePdfHome(hashMap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to update due to " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePdfHome(HashMap<String, Object> hashMap) {
        //db reference
        // Categories Home > categoryId > Books > bookId
        String categoryId = "" + hashMap.get("categoryId");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories Home");
        ref.child("" + categoryId).child("Books").child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfEditActivity.this, "Book info updated", Toast.LENGTH_SHORT).show();

                        //load category title
                        DatabaseReference refBookCategory = FirebaseDatabase.getInstance().getReference("Categories");
                        refBookCategory.child(categoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get category
                                        String category = "" + snapshot.child("category").getValue();

                                        Intent intent = new Intent(PdfEditActivity.this, PdfListAdminActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("categoryId",selectedCategoryId);
                                        intent.putExtra("categoryTitle",category);
                                        PdfEditActivity.this.startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PdfEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String selectedCategoryId = "", selectedCategoryTitle = "";

    private void categoryDialog() {
        //make string array from arrayList of string
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i < categoryTitleArrayList.size(); i++) {
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategoryId = categoryIdArrayList.get(which);
                        selectedCategoryTitle = categoryTitleArrayList.get(which);

                        //set to categoryTv
                        binding.categoryTv.setText(selectedCategoryTitle);
                    }
                })
                .show();
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: Loading Categories...");

        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArrayList.clear();
                categoryTitleArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = "" + ds.child("id").getValue().toString();
                    String category = "" + ds.child("category").getValue().toString();
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);

                    Log.d(TAG, "onDataChange: ID: " + id);
                    Log.d(TAG, "onDataChange: Category " + category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showImageAttachMenu() {
        //init/setup popup menu
        PopupMenu popupMenu = new PopupMenu(this, binding.coverPageIv, Gravity.CENTER_HORIZONTAL);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        popupMenu.show();

        //handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //get id of item clicked
                int which = item.getItemId();
                if (which == 0) {
                    //Camera clicked
                    pickImageCamera();
                } else if (which == 1) {
                    //Gallery clicked
                    pickImageGallery();
                }

                return false;
            }
        });
    }

    private void pickImageCamera() {
        //intent to pick image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Now Pick"); //image title
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample image description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }

    private void pickImageGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of camera intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData(); //no need here as in camera case we already have image in imageUri variable

                        binding.coverPageIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(PdfEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();

                        //set image to imagesIv
                        binding.coverPageIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(PdfEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}