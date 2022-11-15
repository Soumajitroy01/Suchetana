package com.example.suchetana.Models;

public class ModelFeedback {
    //Note same spelling as firebase
    String email, id, message, name,uid;

    //empty constructor for firebase
    public ModelFeedback() {
    }

    //parameterized constructor
    public ModelFeedback(String email, String id, String message, String name, String uid) {
        this.email = email;
        this.id = id;
        this.message = message;
        this.name = name;
        this.uid = uid;
    }

    /*---Getter/Setter---*/
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
