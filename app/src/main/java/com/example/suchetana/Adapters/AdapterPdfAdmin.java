package com.example.suchetana.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.suchetana.Activity.PdfDetailActivity;
import com.example.suchetana.Activity.PdfEditActivity;
import com.example.suchetana.Filters.FilterPdfAdmin;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowPdfAdminBinding;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    //context
    private Context context;
    //arrayList to hold list of data of type modelPdf
    public ArrayList<ModelPdf> pdfArrayList, filterList;

    //view binding of row_pdf_admin.xml
    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    //constructor of above
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view of row_pdf_admin.xml
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfAdmin.HolderPdfAdmin holder, int position) {
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

        //handle click, show dialog with options 1) Edit, 2) Delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model, holder);
            }
        });

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

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {
        //options to show in dialog
        String[] options = {"Edit", "Delete"};

        String bookId = model.getId();
        String bookUrl = model.getPdfUrl();
        String bookTitle = model.getTitle();
        String coverPageUrl = model.getCoverPageUrl();
        String categoryId = model.getCategoryId();

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog option click
                        if (which == 0) {
                            //Edit clicked, open PdfEditActivity to edit the book info
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);
                        } else {
                            //Delete clicked
                            MyApplication.deleteBook(context,
                                    "" + bookId,
                                    "" + bookUrl,
                                    "" + bookTitle,
                                    "" + coverPageUrl,
                                    "" + categoryId
                            );
                            //deleteBook(model, holder);
                        }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        //return size of arrayList
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPdfAdmin(filterList, this);
        }
        return filter;
    }

    /*view holder class for row_pdf_admin.xml*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder {

        //UI views of row_pdf_admin.xml
        TextView titleTv, priceTv, authorTv, publisherTv, isbnTv;
        ImageButton moreBtn;
        ImageView pdfIv;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            //init ui views
            titleTv = binding.titleTv;
            authorTv = binding.authorTv;
            priceTv = binding.priceTv;
            publisherTv = binding.publisherTv;
            isbnTv = binding.isbnTv;
            moreBtn = binding.moreBtn;
            pdfIv = binding.pdfIv;
        }
    }
}