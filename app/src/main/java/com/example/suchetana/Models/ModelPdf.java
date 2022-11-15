package com.example.suchetana.Models;

public class ModelPdf {
    //variables
    String uid, id, title, author, description, publisher, isbn, categoryId, pdfUrl, coverPageUrl;
    long timestamp, viewsCount, downloadsCount,price;
    boolean favorite;

    //empty constructor, required for firebase
    public ModelPdf() {
    }

    //constructor with all params
    public ModelPdf(String uid, String id, String title, String author, String description, Long price, String publisher, String isbn, String categoryId, String pdfUrl, String coverPageUrl, long timestamp, long viewsCount, long downloadsCount, boolean favorite) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.publisher = publisher;
        this.isbn = isbn;
        this.categoryId = categoryId;
        this.pdfUrl = pdfUrl;
        this.coverPageUrl = coverPageUrl;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.favorite = favorite;
    }

    /*--Getter/Setters--*/

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCoverPageUrl() {
        return coverPageUrl;
    }

    public void setCoverPageUrl(String coverPageUrl) {
        this.coverPageUrl = coverPageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
