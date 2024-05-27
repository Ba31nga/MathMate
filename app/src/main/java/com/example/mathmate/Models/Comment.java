package com.example.mathmate.Models;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Comment {
    public String id;
    public String forumId;
    public String authorId;
    public String message;

    public Comment() {
        id = "";
        forumId = "";
        authorId = "";
        message = "";
    }

    public Comment(String forumId, String authorId, String message) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        this.forumId = forumId;
        this.authorId = authorId;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getForumId() {
        return forumId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getMessage() {
        return message;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
