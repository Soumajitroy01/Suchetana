package com.example.suchetana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.suchetana.Activity.SearchActivity;
import com.example.suchetana.Models.ModelCategory;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowCategoryHomeBinding;

import java.util.ArrayList;

public class AdapterCategoryHome extends RecyclerView.Adapter<AdapterCategoryHome.HolderCategory> {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;

    //view binding
    private RowCategoryHomeBinding binding;

    public AdapterCategoryHome(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view of row_category_home.xml
        binding = RowCategoryHomeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdapterCategoryHome.HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCategoryHome.HolderCategory holder, int position) {
        //get data
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        String imageUrl = model.getImage();
        long timestamp = model.getTimestamp();

        //set data
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .into(holder.categoryIv);
        holder.categoryTv.setText(category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("categoryId",id);
                intent.putExtra("categoryTitle",category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    /*View Holder class to hold UI views for row_category_home.xml*/
    class HolderCategory extends RecyclerView.ViewHolder {

        //ui views of row_category_home.xml
        TextView categoryTv;
        ImageView categoryIv;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            //init ui views
            categoryTv = binding.categoryTv;
            categoryIv = binding.categoryIv;
        }
    }
}
