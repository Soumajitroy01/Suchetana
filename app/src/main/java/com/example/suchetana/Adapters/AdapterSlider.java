package com.example.suchetana.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.suchetana.Models.ModelSliderImage;
import com.example.suchetana.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class AdapterSlider extends SliderViewAdapter<AdapterSlider.Holder> {

    private Context context;
    private ArrayList<ModelSliderImage> imageArrayList;

    public AdapterSlider(ArrayList<ModelSliderImage> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        //bind view of slider_item.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        this.context = parent.getContext();
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        //get data
        ModelSliderImage model = imageArrayList.get(position);
        String id = model.getId();
        String title = model.getTitle();
        String sliderImage = model.getSliderImage();
        long timestamp = model.getTimestamp();

        //set data
        Glide.with(context)
                .load(model.getSliderImage())
                .placeholder(R.drawable.ic_image_gray)
                .into(viewHolder.sliderIv);
    }

    @Override
    public int getCount() {
        return imageArrayList.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView sliderIv;

        public Holder(View itemView) {
            super(itemView);

            //init ui views
            sliderIv = itemView.findViewById(R.id.sliderIv);
        }
    }
}
