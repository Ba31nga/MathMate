package com.example.mathmate.Models;

import androidx.annotation.NonNull;

import java.util.UUID;

public class Forum {
    public String id;
    public String authorUid;
    public String title;
    public String subject;
    public String description;
    public String imageUri;


    public Forum() {
        id = "";
        authorUid = "";
        title = "";
        subject = "";
        description = "";
        imageUri = "";
    }

    public Forum(String title, String subject, String description, String imageUri, String authorUid) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.imageUri = imageUri;
        this.authorUid = authorUid;
    }

    public Forum(String title, String subject, String description, String authorUid) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        this.title = title;
        this.subject = subject;
        this.description = description;
        imageUri = "";
        this.authorUid = authorUid;
    }

    public String getAuthorUid() { return  authorUid; }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public String getSubject() {
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

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
