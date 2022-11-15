package com.example.suchetana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.suchetana.Activity.PdfDetailActivity;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowPdfFavoriteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterPdfFavorite  extends RecyclerView.Adapter<AdapterPdfFavorite.HolderPdfFavorite> {
    //context
    private Context context;
    //arrayList to hold list of data of type modelPdf
    public ArrayList<ModelPdf> pdfArrayList;

    //view binding of row_pdf_wishlist.xml
    private RowPdfFavoriteBinding binding;

    private static final String TAG = "PDF_ADAPTER_TAG";

    //constructor of above
    public AdapterPdfFavorite(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    public HolderPdfFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view of row_pdf_favorite.xml
        binding = RowPdfFavoriteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfFavorite.HolderPdfFavorite holder, int position) {
        /*Get data, Set Data, handle clicks, etc.*/

        //get data
        ModelPdf model = pdfArrayList.get(position);

        loadBookDetails(model, holder);

        //handle click, favorite Button
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in favorite, remove from favorite
                MyApplication.removeFromFavorite(context, "" + model.getId());
            }
        });

        //handle click, open pdf/book detail page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);
            }
        });
    }

    private void loadBookDetails(ModelPdf model, AdapterPdfFavorite.HolderPdfFavorite holder) {
        String bookId = model.getId();
        Log.d(TAG, "loadBookDetails: Book details of Book ID: "+bookId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //get book info
                        String bookTitle = "" + snapshot.child("title").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();
                        String bookUrl = "" + snapshot.child("pdfUrl").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String publisher = "" + snapshot.child("publisher").getValue();
                        Long price = Long.parseLong("" + snapshot.child("price").getValue());
                        String isbn = "" + snapshot.child("isbn").getValue();
                        String author = "" + snapshot.child("author").getValue();
                        String coverPageUrl = "" + snapshot.child("coverPageUrl").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();

                        //set to model
                        model.setFavorite(true);
                        model.setTitle(bookTitle);
                        model.setDescription(description);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setPdfUrl(bookUrl);
                        model.setPublisher(publisher);
                        model.setCoverPageUrl(coverPageUrl);
                        model.setPrice(price);
                        model.setIsbn(isbn);
                        model.setAuthor(author);


                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data to views
                        holder.titleTv.setText(bookTitle);
                        holder.authorTv.setText(author);
                        Glide.with(context)
                                .load(model.getCoverPageUrl())
                                .placeholder(R.drawable.back01)
                                .into(holder.pdfIv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    /*view holder class for row_pdf_favorite.xml*/
    class HolderPdfFavorite extends RecyclerView.ViewHolder {

        //UI views of row_pdf_wishlist.xml
        TextView titleTv, authorTv;
        ImageButton favoriteBtn;
        ImageView pdfIv;
        public HolderPdfFavorite(@NonNull View itemView) {
            super(itemView);

            //init ui views
            titleTv = binding.titleTv;
            authorTv = binding.authorTv;
            favoriteBtn = binding.favoriteBtn;
            pdfIv = binding.pdfIv;
        }
    }
}
