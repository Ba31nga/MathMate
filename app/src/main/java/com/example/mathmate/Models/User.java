package com.example.mathmate.Models;

import android.net.Uri;

public class User {
    private String username;
    private String bio;
    private int questionAnswered;
    private int userPoints;
    private String uri;

    public User() {
        username = "";
        bio = "no bio";
        uri = "";
        questionAnswered = 0;
        userPoints = 0;
    }

    public User(String username) {
        this.username = username;
        bio = "no bio";
        uri = "";
        questionAnswered = 0;
        userPoints = 0;
    }
    public User(String username, String uri) {
        this.username = username;
        bio = "no bio";
        this.uri = uri;
        questionAnswered = 0;
        userPoints = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getUri() {
        return uri;
    }

    public String getBio() {
        return bio;
    }

    public int getQuestionAnswered() {
        return questionAnswered;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setQuestionAnswered(int questionAnswered) {
        this.questionAnswered = questionAnswered;
    }

    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }
}
