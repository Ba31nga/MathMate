package com.example.mathmate.Models;

import java.util.UUID;

public class Notification {
    private final String text;
    private final String forumId;
    private final String id;

    public Notification() {
        text = "";
        forumId = "";
        id = "";
    }

    public Notification(String text, String forumId, String id) {
        this.text = text;
        this.forumId = forumId;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getForumId() {
        return forumId;
    }




}
