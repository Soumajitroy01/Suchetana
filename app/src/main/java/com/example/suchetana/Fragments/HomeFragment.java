package com.example.suchetana.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.suchetana.Activity.SearchActivity;
import com.example.suchetana.Adapters.AdapterCategoryHome;
import com.example.suchetana.Adapters.AdapterPdfMostDownloaded;
import com.example.suchetana.Adapters.AdapterPdfMostViewed;
import com.example.suchetana.Adapters.AdapterRv1;
import com.example.suchetana.Adapters.AdapterSlider;
import com.example.suchetana.Models.ModelCategory;
import com.example.suchetana.Models.ModelPdf;
import com.example.suchetana.Models.ModelSliderImage;
import com.example.suchetana.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    //view binding
    private FragmentHomeBinding binding;

    private ArrayList<ModelSliderImage> imageArrayList;
    private ArrayList<ModelPdf> pdfMostViewed;
    private ArrayList<ModelPdf> pdfMostDownloaded;
    private ArrayList<ModelPdf> pdfArrayList;
    private ArrayList<ModelCategory> categoryArrayList;

    private AdapterSlider adapterSliderImage;
    private AdapterCategoryHome adapterCategoryHome;
    private AdapterPdfMostViewed adapterPdfMostViewed;
    private AdapterPdfMostDownloaded adapterPdfMostDownloaded;
    private AdapterRv1 adapterRv1;

    private HashMap<String, ArrayList<ModelPdf>> map;

    public static final String TAG = "HOME_TAG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadImages();
        loadCategory();
        String orderBy ="viewsCount";
        loadMostViewed(orderBy);
        orderBy = "downloadsCount";
        loadMostDownloaded(orderBy);

        //handle click, view more MostViewed
        binding.viewMore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("categoryId","viewsCount");
                intent.putExtra("categoryTitle","MostViewed");
                getContext().startActivity(intent);
            }
        });

        //handle click, view more MostDownloaded
        binding.viewMore3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("categoryId","downloadsCount");
                intent.putExtra("categoryTitle","MostDownloaded");
                getContext().startActivity(intent);
            }
        });
    }

//    private void loadRv1() {
//        map = new HashMap<>();
//        pdfArrayList = new ArrayList<>();
//        for (ModelCategory model:categoryArrayList)
//        {
//            loadMap(model.getId());
//            Log.d(TAG, "loadRv1: "+model.getId());
//        }
//        //setup adapter
//        //adapterRv1 = new AdapterRv1(getContext(),categoryArrayList,map);
//        //binding.rv1.setAdapter(adapterRv1);
//    }
//
//    private void loadMap(String categoryId) {
//        //database reference
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");
//        databaseReference.orderByChild("categoryId").equalTo(categoryId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        pdfArrayList.clear();
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            //get data
//                            ModelPdf model = ds.getValue(ModelPdf.class);
//                            //add to list
//                            pdfArrayList.add(model);
//
//                            Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
//                        }
//                        map.put(categoryId, new ArrayList<>());
//                        //set data in hashMap
//                        map.put(categoryId,pdfArrayList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    private void loadMostDownloaded(String orderBy) {
        //init arrayList
        pdfMostDownloaded = new ArrayList<>();

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(orderBy).limitToLast(6)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arrayList before adding data to it
                        pdfMostDownloaded.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelPdf model = ds.getValue(ModelPdf.class);

                            //add category to arrayList
                            pdfMostDownloaded.add(model);
                        }
                        Collections.reverse(pdfMostDownloaded);
                        //setup adapter
                        adapterPdfMostDownloaded = new AdapterPdfMostDownloaded(getContext(),pdfMostDownloaded);
                        //set adapter to recycler view
                        binding.mostDownloadedRv.setAdapter(adapterPdfMostDownloaded);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMostViewed(String orderBy) {
        //init arrayList
        pdfMostViewed = new ArrayList<>();

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(orderBy).limitToLast(6)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arrayList before adding data to it
                        pdfMostViewed.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelPdf model = ds.getValue(ModelPdf.class);

                            //add category to arrayList
                            pdfMostViewed.add(model);
                        }
                        Collections.reverse(pdfMostViewed);
                        //setup adapter
                        adapterPdfMostViewed = new AdapterPdfMostViewed(getContext(),pdfMostViewed);
                        //set adapter to recycler view
                        binding.mostViewedRv.setAdapter(adapterPdfMostViewed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCategory() {
        //init before adding data
        categoryArrayList = new ArrayList<>();

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arrayList before adding data to it
                categoryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //add category to arrayList
                    categoryArrayList.add(model);
                }
                //setup adapter
                adapterCategoryHome = new AdapterCategoryHome(getContext(),categoryArrayList);
                //set adapter to recycler view
                binding.categoryRv.setAdapter(adapterCategoryHome);

//                loadRv1();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImages() {
        //init arrayList
        imageArrayList = new ArrayList<>();

        //get all images from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Images");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arrayList before adding data to it
                imageArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelSliderImage model = ds.getValue(ModelSliderImage.class);

                    //add category to arrayList
                    imageArrayList.add(model);
                }
                //setup adapter
                adapterSliderImage = new AdapterSlider(imageArrayList);
                //set adapter to recyclerView
                binding.homeSv.setSliderAdapter(adapterSliderImage);
                binding.homeSv.setIndicatorAnimation(IndicatorAnimationType.WORM);
                binding.homeSv.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                binding.homeSv.startAutoCycle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
