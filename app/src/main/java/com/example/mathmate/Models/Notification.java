package com.example.mathmate.Models;

public class Notification {
    private final String userId;
    private final String text;
    private final String forumId;

    public Notification(String userId, String text, String forumId) {
        this.userId = userId;
        this.text = text;
        this.forumId = forumId;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getForumId() {
        return forumId;
    }
}
