package com.example.mathmate.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Comment {
    private final String id;
    private final String forumId;
    private final String authorId;
    private final String message;
    private int likes;

    public Comment() {
        id = "";
        forumId = "";
        authorId = "";
        message = "";
        likes = 0;
    }

    public Comment(String forumId, String authorId, String message) {
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        this.forumId = forumId;
        this.authorId = authorId;
        this.message = message;
        likes = 0;
    }

    public void addLike() {
        likes++;
        updateDatabase();
    }

    public void removeLike() {
        likes--;
        updateDatabase();
    }

    private void updateDatabase() {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        commentsRef.child(forumId).child(id).setValue(this);
    }

    public int getLikes() {
        return likes;
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


}
