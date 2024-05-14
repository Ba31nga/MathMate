package com.example.mathmate.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.User;
import com.example.mathmate.Profile.ChangeProfilePictureActivity;
import com.example.mathmate.R;
import com.example.mathmate.Utils.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private TextView username_tv, bio_tv, QA_tv, points_tv;
    private String username, bio, QA, points;
    private FirebaseAuth authProfile;
    private ImageView pfp;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        username_tv = v.findViewById(R.id.username_tv);
        bio_tv = v.findViewById(R.id.bio_tv);
        QA_tv = v.findViewById(R.id.QA_tv);
        points_tv = v.findViewById(R.id.points_tv);
        progressBar = v.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        ImageButton logout_btn = v.findViewById(R.id.logout_btn);
        ImageButton settings_btn = v.findViewById(R.id.settings_btn);

        pfp = v.findViewById(R.id.pfp);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangeProfilePictureActivity.class);
                startActivity(intent);

            }
        });

        logout_btn.setOnClickListener(v1 -> {
            authProfile.signOut();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }


        return v;
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // Extracting user information from the firebase realtime database
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User UserDetails = snapshot.getValue(User.class);
                if (UserDetails != null) {
                    username = firebaseUser.getDisplayName();
                    bio = UserDetails.getBio();
                    QA = String.valueOf(UserDetails.getQuestionAnswered());
                    points = String.valueOf(UserDetails.getUserPoints());

                    username_tv.setText(username);
                    bio_tv.setText(bio);
                    QA_tv.setText(QA);
                    points_tv.setText(points);

                    // Set user DP (after user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();

                    // Imageview setImageURI() should not be used with regular URIs - so we are using Picasso
                    Glide.with(getContext()).load(uri).placeholder(R.drawable.default_pfp).into(pfp);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}