package com.example.suchetana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suchetana.Activity.SearchActivity;
import com.example.suchetana.Models.ModelCategory;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.databinding.RowRv1Binding;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterRv1 extends RecyclerView.Adapter<AdapterRv1.HolderCategory> {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;
    private HashMap<String, ArrayList<ModelPdf>> map;

    //view binding
    private RowRv1Binding binding;

    //adapter
    private AdapterRv2 adapterRv2;

    public AdapterRv1(Context context, ArrayList<ModelCategory> categoryArrayList, HashMap<String, ArrayList<ModelPdf>> map) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.map = map;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowRv1Binding.inflate(LayoutInflater.from(context),parent,false);
        return new AdapterRv1.HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRv1.HolderCategory holder, int position) {
        //get data
        ModelCategory model = categoryArrayList.get(position);
        String categoryTitle = model.getCategory();
        String categoryId = model.getId();

        //set data
        holder.categoryTv.setText(categoryTitle);

        //handle click view more
        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("categoryId",categoryId);
                intent.putExtra("categoryTitle",categoryTitle);
                context.startActivity(intent);
            }
        });

        loadBooks(model,holder);
    }

    private void loadBooks(ModelCategory model, HolderCategory holder) {
        //set adapter
        adapterRv2 = new AdapterRv2(context,map.get(model.getId()));
        holder.rv2.setAdapter(adapterRv2);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    /*View Holder class to hold UI views for row_rv1.xml*/
    class HolderCategory extends RecyclerView.ViewHolder {

        //ui views of row_rv1.xml
        TextView categoryTv, viewMore;
        RecyclerView rv2;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            //init ui views
            categoryTv = binding.categoryTv;
            viewMore = binding.viewMore;
            rv2 = binding.rv2;
        }
    }
}
