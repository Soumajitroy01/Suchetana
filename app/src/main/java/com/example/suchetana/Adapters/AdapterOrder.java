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
import com.example.suchetana.Activity.OrderDetailActivity;
import com.example.suchetana.Models.ModelOrder;
import com.example.suchetana.R;
import com.example.suchetana.databinding.RowOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.HolderOrder> {

    //context
    private Context context;

    //ArrayList to store order details
    private ArrayList<ModelOrder> orderArrayList;

    //view binding
    private RowOrdersBinding binding;

    private static final String TAG = "CART_TAG";

    public AdapterOrder(Context context, ArrayList<ModelOrder> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderOrder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        binding = RowOrdersBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderOrder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderOrder holder, int position) {
        /*Get data, Set Data, handle clicks, etc.*/

        //get data
        ModelOrder model = orderArrayList.get(position);

        loadBookDetails(model, holder);

        holder.statusTv.setText(model.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", model.getOrderId());
                intent.putExtra("bookId", model.getBookId());
                context.startActivity(intent);
            }
        });
    }

    private void loadBookDetails(ModelOrder model, HolderOrder holder) {
        String bookId = model.getBookId();
        Log.d(TAG, "loadBookDetails: Book details of Book ID: "+bookId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //get book info
                        String coverPageUrl = "" + snapshot.child("coverPageUrl").getValue();

                        //set data to views
                        Glide.with(context)
                                .load(coverPageUrl)
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
        return orderArrayList.size();
    }

    class HolderOrder extends RecyclerView.ViewHolder {

        ImageView pdfIv;
        ImageButton orderNextBtn;
        TextView statusTv;

        public HolderOrder(@NonNull @NotNull View itemView) {
            super(itemView);

            pdfIv = binding.pdfIv;
            orderNextBtn = binding.orderNextBtn;
            statusTv = binding.statusTv;
        }
    }
}
