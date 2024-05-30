package com.example.mathmate.Models;

public class Notification {
    private final String text;
    private final String forumId;

    public Notification(String text, String forumId) {
        this.text = text;
        this.forumId = forumId;
    }

    public String getText() {
        return text;
    }

    public String getForumId() {
        return forumId;
    }

}
