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
import com.example.suchetana.Activity.PdfDetailActivity;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowRv2Binding;

import java.util.ArrayList;

public class AdapterRv2 extends RecyclerView.Adapter<AdapterRv2.HolderPdf> {

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;

    //view binding
    private RowRv2Binding binding;

    public AdapterRv2(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowRv2Binding.inflate(LayoutInflater.from(context),parent,false);
        return new AdapterRv2.HolderPdf(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRv2.HolderPdf holder, int position) {
        //get data
        ModelPdf model = pdfArrayList.get(position);
        String coverPageUrl = model.getCoverPageUrl();
        String title = model.getTitle();
        String author = model.getAuthor();
        String pdfId = model.getId();

        //set data
        holder.titleTv.setText(title);
        holder.authorTv.setText(author);
        Glide.with(context)
                .load(coverPageUrl)
                .placeholder(R.drawable.back01)
                .into(holder.pdfIv);

        //handle click itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    /*View Holder class to hold UI views for row_category_home.xml*/
    class HolderPdf extends RecyclerView.ViewHolder {

        //ui views of row_category_home.xml
        ImageView pdfIv;
        TextView titleTv, authorTv;

        public HolderPdf(@NonNull View itemView) {
            super(itemView);

            //init ui views
            pdfIv = binding.pdfIv;
            titleTv = binding.titleTv;
            authorTv = binding.authorTv;
        }
    }
}
