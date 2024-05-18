package com.example.mathmate.Models;

import java.util.UUID;

public class Forum {
    public String id;
    public String authorUid;
    public String title;
    public String subject;
    public String description;
    public String imageUri;


    public Forum(String title, String subject, String description, String imageUri, String authorUid) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.imageUri = imageUri;
        this.authorUid = authorUid;
    }

    public String getAuthorUid() { return  authorUid; }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subject;
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
        this.subject = subTitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
