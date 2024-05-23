package com.example.mathmate.Models;

public class Like {
    private final String commentId;
    private final String likerId;

    public Like(String commentId, String likerId) {
        this.commentId = commentId;
        this.likerId = likerId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getLikerId() {
        return likerId;
    }
}
