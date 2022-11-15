package com.example.suchetana.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.suchetana.Models.ModelSliderImage;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowImagesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterSliderImage extends RecyclerView.Adapter<AdapterSliderImage.HolderSliderImage> {

    private Context context;
    private ArrayList<ModelSliderImage> imageArrayList;

    //view binding
    private RowImagesBinding binding;

    public AdapterSliderImage(Context context, ArrayList<ModelSliderImage> imageArrayList) {
        this.context = context;
        this.imageArrayList = imageArrayList;
    }

    @NonNull
    @Override
    public HolderSliderImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view of row_images.xml
        binding = RowImagesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderSliderImage(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSliderImage.HolderSliderImage holder, int position) {
        //get data
        ModelSliderImage model = imageArrayList.get(position);
        String id = model.getId();
        String title = model.getTitle();
        String sliderImage = model.getSliderImage();
        long timestamp = model.getTimestamp();

        //set data
        Glide.with(context)
                .load(model.getSliderImage())
                .placeholder(R.drawable.ic_image_gray)
                .into(binding.imagesIv);

        //handle click, delete image
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this image?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //begin delete
                                Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show();
                                deleteImage(model, holder);
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
    }

    private void deleteImage(ModelSliderImage model, HolderSliderImage holder) {
        //get id of image to delete
        String id = model.getId();
        String imageUrl = model.getSliderImage();
        //Firebase DB > Images > imagesId
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Images");
                        ref.child(id)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //deleted successfully
                                        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed to delete
                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }

    /*View Holder class to hold UI views for row_category.xml*/
    class HolderSliderImage extends RecyclerView.ViewHolder {

        //ui views of row_category.xml
        ShapeableImageView imagesIv;
        ImageButton deleteBtn;

        public HolderSliderImage(@NonNull View itemView) {
            super(itemView);

            //init ui views
            imagesIv = binding.imagesIv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
