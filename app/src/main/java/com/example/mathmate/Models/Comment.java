package com.example.mathmate.Models;

public class Comment {
    public String forumId;
    public String authorId;
    public String message;

    public Comment(String forumId, String authorId, String message) {
        this.forumId = forumId;
        this.authorId = authorId;
        this.message = message;
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
