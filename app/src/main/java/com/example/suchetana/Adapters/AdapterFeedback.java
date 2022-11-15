package com.example.suchetana.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suchetana.Models.ModelFeedback;
import com.example.suchetana.databinding.RowFeedbackBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterFeedback extends RecyclerView.Adapter<AdapterFeedback.HolderFeedback> {

    private Context context;
    private ArrayList<ModelFeedback> feedbackArrayList;

    //view binding
    private RowFeedbackBinding binding;

    public AdapterFeedback(Context context, ArrayList<ModelFeedback> feedbackArrayList) {
        this.context = context;
        this.feedbackArrayList = feedbackArrayList;
    }

    @NonNull
    @Override
    public HolderFeedback onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view of row_feedback.xml
        binding = RowFeedbackBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderFeedback(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFeedback.HolderFeedback holder, int position) {
        //get data
        ModelFeedback model = feedbackArrayList.get(position);
        String id = model.getId();
        String email = model.getEmail();
        String name = model.getName();
        String message = model.getMessage();

        //set data
        holder.nameTv.setText(name);
        holder.emailTv.setText(email);
        holder.messageTv.setText(message);

        //handle click, resolve feedback
        holder.resolveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Reply");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    context.startActivity(intent);
                } catch(Exception e) {
                    Toast.makeText(context, "Sorry...You don't have any mail app", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Resolve")
                        .setMessage("Are you sure you have resolved the issue?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //begin delete
                                Toast.makeText(context, "Resolving", Toast.LENGTH_SHORT).show();
                                deleteFeedback(model, holder);
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

    private void deleteFeedback(ModelFeedback model, HolderFeedback holder) {
        //get id to delete feedback
        String id = model.getId();

        //FirebaseDB > Feedback > id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feedback");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //resolved successfully
                        Toast.makeText(context, "Resolved Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return feedbackArrayList.size();
    }

    /*View Holder class to hold UI views for row_feedback.xml*/
    class HolderFeedback extends RecyclerView.ViewHolder {

        TextView nameTv, emailTv, messageTv;
        ImageButton resolveBtn;

        public HolderFeedback(@NonNull View itemView) {
            super(itemView);

            //init ui views
            nameTv = binding.nameTv;
            emailTv = binding.emailTv;
            messageTv = binding.messageTv;
            resolveBtn = binding.resolveBtn;
        }
    }
}
