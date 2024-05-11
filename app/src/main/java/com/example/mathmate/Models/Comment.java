package com.example.mathmate.Models;

public class Comment {
    private String author;
    private int likes;

    public int compareTo(Comment c) {
        return this.likes - c.likes;
    }


}
