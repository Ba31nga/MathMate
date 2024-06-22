package com.example.mathmate.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText username_input; // EditText for username input
    private EditText bio_input; // EditText for bio input
    private Button saveBtn; // Button to save changes
    private Button backBtn; // Button to go back
    private DatabaseReference usersRef; // Reference to Firebase database
    FirebaseUser currentUser; // Current logged-in user
    ProgressBar progressBar; // progress bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Progress bar
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        // Initialize EditTexts for username and bio inputs
        username_input = findViewById(R.id.username_input);
        bio_input = findViewById(R.id.bio_input);

        // Initialize Firebase database reference and current user
        usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Show current user information in the input fields
        showInformation();

        // Initialize save button and set its click listener
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(v -> {
            // Get the new username and bio from the input fields
            String newUsername = username_input.getText().toString();
            String newBio = bio_input.getText().toString();

            // shows progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Check if the new username already exists in the database
            Query query = usersRef.orderByChild("username").equalTo(newUsername);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // If username already exists, show an error message
                        username_input.requestFocus();
                        username_input.setError("The username is already in use");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        // If username is unique, update the user information in the database
                        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    user.setBio(newBio);
                                    user.setUsername(newUsername);
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(newUsername).build();
                                    currentUser.updateProfile(profileChangeRequest);
                                    snapshot.getRef().setValue(user);

                                    // Show success message and finish the activity
                                    Toast.makeText(UpdateProfileActivity.this, "Changes were successfully made", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error if the update is cancelled
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if the query is cancelled
                }
            });
        });
    }

    // Method to display current user information in the input fields
    private void showInformation() {
        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    username_input.setText(user.getUsername());
                    bio_input.setText(user.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if data retrieval is cancelled
            }
        });
    }
}
