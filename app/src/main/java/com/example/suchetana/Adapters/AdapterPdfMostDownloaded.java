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
import com.example.suchetana.databinding.RowPdfHomeBinding;

import java.util.ArrayList;

public class AdapterPdfMostDownloaded extends RecyclerView.Adapter<AdapterPdfMostDownloaded.HolderPdf> {
    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;

    //view binding
    private RowPdfHomeBinding binding;

    public AdapterPdfMostDownloaded(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfHomeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdapterPdfMostDownloaded.HolderPdf(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfMostDownloaded.HolderPdf holder, int position) {
        //get data
        ModelPdf model = pdfArrayList.get(position);

        //set data
        Glide.with(context)
                .load(model.getCoverPageUrl())
                .placeholder(R.drawable.back01)
                .into(holder.pdfIv);
        holder.titleTv.setText(model.getTitle());
        holder.authorTv.setText(model.getAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class HolderPdf extends RecyclerView.ViewHolder {
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
