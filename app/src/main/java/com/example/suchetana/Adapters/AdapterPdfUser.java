package com.example.suchetana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.suchetana.Activity.PdfDetailActivity;
import com.example.suchetana.Filters.FilterPdfUser;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowPdfUserBinding;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {

    //context
    private Context context;
    //arrayList to hold list of data of type modelPdf
    public ArrayList<ModelPdf> pdfArrayList, filterList;

    //view binding of row_pdf_admin.xml
    private RowPdfUserBinding binding;

    private FilterPdfUser filter;

    //constructor of above
    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view of row_pdf_admin.xml
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfUser.HolderPdfUser holder, int position) {
        /*Get data, Set Data, handle clicks, etc.*/

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String author = model.getAuthor();
        String description = model.getDescription();
        Long price = model.getPrice();
        String publisher = model.getPublisher();
        String isbn = model.getIsbn();
        String pdfUrl = model.getPdfUrl();
        long timestamp = model.getTimestamp();

        //set data
        holder.titleTv.setText(title);
        holder.authorTv.setText(author);
        holder.publisherTv.setText(publisher);
        Glide.with(context)
                .load(model.getCoverPageUrl())
                .placeholder(R.drawable.back01)
                .into(holder.pdfIv);
        holder.priceTv.setText("₹" + price);
        holder.isbnTv.setText("ISBN: "+isbn);

        //handle click, open pdf/book detail page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //return size of arrayList
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPdfUser(filterList, this);
        }
        return filter;
    }

    /*view holder class for row_pdf_admin.xml*/
    class HolderPdfUser extends RecyclerView.ViewHolder {

        //UI views of row_pdf_admin.xml
        TextView titleTv, priceTv, authorTv, publisherTv, isbnTv;
        ImageView pdfIv;

        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);

            //init ui views
            titleTv = binding.titleTv;
            authorTv = binding.authorTv;
            priceTv = binding.priceTv;
            publisherTv = binding.publisherTv;
            isbnTv = binding.isbnTv;
            pdfIv = binding.pdfIv;
        }
    }
}
