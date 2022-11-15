package com.example.suchetana.Models;

import java.io.Serializable;

public class ModelOrder implements Serializable {
    String bookId, orderId, quantity, price, type, status, userUid, mode, name , address , phone , city , pin , state , country;
    long timestamp;

    public ModelOrder() {
    }

    public ModelOrder(String bookId, String orderId, String quantity, String price, String type, String status, String userUid, String mode, String name, String address, String phone, String city, String pin, String state, String country, long timestamp) {
        this.bookId = bookId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.status = status;
        this.userUid = userUid;
        this.mode = mode;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.pin = pin;
        this.state = state;
        this.country = country;
        this.timestamp = timestamp;
    }

    /*---Getters/Setters---*/
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
