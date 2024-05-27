package com.example.mathmate.Models;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    public void addAnswers() {
        questionAnswered++;
        updateDatabase();
    }

    public void RemoveAnswers() {
        questionAnswered--;
        updateDatabase();
    }

    public void addPoint() {
        userPoints++;
        updateDatabase();
    }

    public void removePoint() {
        userPoints--;
        updateDatabase();
    }

    public void removePoint(int points) {
        userPoints -= points;
        updateDatabase();
    }

    private void updateDatabase() {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = commentsRef.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DatabaseReference userRef = dataSnapshot.getRef();
                    userRef.setValue(User.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
