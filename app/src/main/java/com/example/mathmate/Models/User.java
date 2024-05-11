package com.example.mathmate.Models;

public class User {
    private String username;
    private String bio;
    private String profileImage;
    private int questionAnswered;
    private int userPoints;

    public User() {
        username = "";
        bio = "no bio";
        profileImage = "";
        questionAnswered = 0;
        userPoints = 0;
    }

    public User(String username) {
        this.username = username;
        bio = "no bio";
        profileImage = "";
        questionAnswered = 0;
        userPoints = 0;
    }

    public String getUsername() {
        return username;
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
