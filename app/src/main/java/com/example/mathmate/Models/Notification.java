package com.example.mathmate.Models;

public class Notification {
    private String userId;
    private String text;
    private String forumId;

    public Notification(String userId, String text, String forumId) {
        this.userId = userId;
        this.text = text;
        this.forumId = forumId;
    }


}
