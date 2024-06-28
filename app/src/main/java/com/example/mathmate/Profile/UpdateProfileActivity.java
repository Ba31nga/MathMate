package com.example.mathmate.Profile;

import static com.example.mathmate.Utils.NotesUtil.errorMessage;
import static com.example.mathmate.Utils.NotesUtil.successMessage;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.example.mathmate.Utils.NetworkChangeReceiver;
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
    private DatabaseReference usersRef; // Reference to Firebase database
    FirebaseUser currentUser; // Current logged-in user
    ProgressBar progressBar; // progress bar
    private NetworkChangeReceiver networkChangeReceiver;

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

        // Button to go back
        ImageButton backBtn = findViewById(R.id.go_back_btn);
        backBtn.setOnClickListener(v -> finish());


        // Initialize save button and set its click listener
        // Button to save changes
        Button saveBtn = findViewById(R.id.save_btn);
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
                    if (snapshot.exists() && !newUsername.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
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
                                    successMessage(UpdateProfileActivity.this, "Changes were successfully made");
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error if the update is cancelled
                                errorMessage(UpdateProfileActivity.this, "Something went wrong, please try again");
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if the query is cancelled
                    errorMessage(UpdateProfileActivity.this, "Something went wrong, please try again");
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
                errorMessage(UpdateProfileActivity.this, "Something went wrong, please try again");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(networkChangeReceiver);
    }
}
