package com.example.suchetana.Models;

public class ModelSliderImage {
    //Note same spelling as firebase
    String id, title, sliderImage;
    long timestamp;

    //empty constructor for firebase
    public ModelSliderImage() {
    }

    //parameterized constructor
    public ModelSliderImage(String id, String title, String sliderImage, long timestamp) {
        this.id = id;
        this.title = title;
        this.sliderImage = sliderImage;
        this.timestamp = timestamp;
    }

    /*---- Getter/setter ----*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSliderImage() {
        return sliderImage;
    }

    public void setSliderImage(String sliderImage) {
        this.sliderImage = sliderImage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
