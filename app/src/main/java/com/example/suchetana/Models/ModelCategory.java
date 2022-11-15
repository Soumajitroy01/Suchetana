package com.example.suchetana.Models;

public class ModelCategory {
    //make sure to use same spellings for model variables as in firebase
    String id, category, uid, image;
    long timestamp;

    //constructor empty required for firebase
    public ModelCategory() {
    }

    //parametrized constructor
    public ModelCategory(String id, String category, String uid, String image, long timestamp) {
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.image = image;
        this.timestamp = timestamp;
    }

    /*---Getters/Setters---*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}