package com.example.mathmate.Models;

public class Forum {
    public String title;
    public String subTitle;
    public String description;
    public String imageUri;

    public Forum(String title, String subTitle, String description, String imageUri) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
