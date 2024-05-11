package com.example.mathmate.Models;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Forum {
    private String title;
    private String subtitle;
    private String description;
    private String author;
    LinkedList<Comment> comments;

    public Forum(String title, String subtitle, String description, String author) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.author = author;
        comments = new LinkedList<Comment>();
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public LinkedList<Comment> getComments() {
        return comments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void AddComment(Comment comment) {
        comments.add(comment);
        sortByLikes();
    }

    private void sortByLikes() {
        comments.sort(Comment::compareTo);
    }
}
