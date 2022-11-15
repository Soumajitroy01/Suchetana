package com.example.suchetana.Adapters;

import android.content.Context;
import android.text.TextUtils;
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
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.MyApplication;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowCartBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.HolderCart> {

    //context
    private Context context;

    //arrayList to store pdf
    public ArrayList<ModelPdf> pdfArrayList;
    public ArrayList<String> priceArrayList, qtyArrayList, typeArrayList;

    //textview to update prices
    TextView cartTotal, delivery, total;

    //view binding
    private RowCartBinding binding;

    private static final String TAG = "CART_TAG";

    public AdapterCart(Context context, ArrayList<ModelPdf> pdfArrayList, ArrayList<String> priceArrayList, ArrayList<String> qtyArrayList, ArrayList<String> typeArrayList, TextView cartTotal, TextView delivery, TextView total) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.priceArrayList = priceArrayList;
        this.qtyArrayList = qtyArrayList;
        this.typeArrayList = typeArrayList;
        this.cartTotal = cartTotal;
        this.delivery = delivery;
        this.total = total;
    }

    @NonNull
    @Override
    public HolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCartBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCart(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCart.HolderCart holder, int position) {
        //get data
        ModelPdf model = pdfArrayList.get(position);

        loadBookDetails(model, holder);

        //set data
        holder.qtyTv.setText(qtyArrayList.get(position));
        String type = typeArrayList.get(position);
        if (TextUtils.equals(type, "ebook")) {
            holder.decBtn.setVisibility(View.GONE);
            holder.qtyTv.setVisibility(View.GONE);
            holder.incBtn.setVisibility(View.GONE);
        }

        //handle click, inc btn
        holder.incBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(holder.qtyTv.getText().toString());
                qtyArrayList.set(position, String.valueOf((qty+1)));

                updateToDb(model.getId(), String.valueOf((qty+1)));
            }
        });

        //handle click, dec btn
        holder.decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(holder.qtyTv.getText().toString());
                if(qty == 1) {
                    pdfArrayList.remove(position);
                    qtyArrayList.remove(position);
                    priceArrayList.remove(position);
                    typeArrayList.remove(position);

                    deleteItem(model.getId());
                }
                else {
                    qtyArrayList.set(position, String.valueOf((qty-1)));

                    updateToDb(model.getId(), String.valueOf((qty-1)));
                }
            }
        });

        //handle click delete btn
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfArrayList.remove(position);
                qtyArrayList.remove(position);
                priceArrayList.remove(position);
                typeArrayList.remove(position);

                deleteItem(model.getId());
            }
        });
    }

    private void deleteItem(String bookId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Cart").child(bookId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateData();
                    }
                });
    }

    private void updateToDb(String bookId, String qty) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", bookId);
        hashMap.put("quantity", qty);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Cart").child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateData();
                    }
                });
    }

    private void updateData() {
        int itemPrice = 0;
        for (int i = 0;i<priceArrayList.size();i++) {
            itemPrice += Integer.parseInt(qtyArrayList.get(i)) * Integer.parseInt(priceArrayList.get(i));
        }
        cartTotal.setText("₹" + itemPrice);

        if (itemPrice > 499) {
            delivery.setText("Free");
            total.setText("₹" + itemPrice);
        }
        else {
            delivery.setText("₹40");
            total.setText("₹" + (itemPrice + 40));
        }
    }

    private void loadBookDetails(ModelPdf model, HolderCart holder) {
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
                        holder.publisherTv.setText(publisher);
                        Glide.with(context)
                                .load(model.getCoverPageUrl())
                                .placeholder(R.drawable.back01)
                                .into(holder.pdfIv);
                        holder.priceTv.setText("₹" + price);
                        holder.isbnTv.setText("ISBN: "+isbn);
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

    class HolderCart extends RecyclerView.ViewHolder {

        ImageView pdfIv;
        TextView titleTv, authorTv, publisherTv, isbnTv, priceTv, qtyTv;
        ImageButton decBtn, incBtn, deleteBtn;

        public HolderCart(@NonNull View itemView) {
            super(itemView);

            pdfIv = binding.pdfIv;
            titleTv = binding.titleTv;
            authorTv = binding.authorTv;
            publisherTv = binding.publisherTv;
            isbnTv = binding.isbnTv;
            priceTv = binding.priceTv;
            qtyTv = binding.qtyTv;
            decBtn = binding.decBtn;
            incBtn = binding.incBtn;
            deleteBtn = binding.deleteBtn;
        }
    }
}
