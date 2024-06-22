package com.example.mathmate.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mathmate.CommentsActivity;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DisplayForumFragment extends Fragment {

    // Variable to store the ID of the forum to be displayed
    private String forumId;

    // UI elements for displaying forum information
    private TextView username, title, subject, description;
    private ImageView profile_picture, forum_picture;
    private Button go_to_comments_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_forum, container, false);

        // Initialize UI elements
        username = v.findViewById(R.id.username);
        title = v.findViewById(R.id.title);
        subject = v.findViewById(R.id.subject);
        description = v.findViewById(R.id.descrption);
        profile_picture = v.findViewById(R.id.profile_picture);
        forum_picture = v.findViewById(R.id.forum_image);
        go_to_comments_btn = v.findViewById(R.id.go_to_comments_btn);

        // Initially hide UI elements until data is loaded
        username.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        subject.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        profile_picture.setVisibility(View.GONE);
        forum_picture.setVisibility(View.GONE);
        go_to_comments_btn.setVisibility(View.GONE);

        // Retrieve the forum ID from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        forumId = prefs.getString("forumid", "none");

        // Set a click listener for the comments button to open the CommentsActivity
        go_to_comments_btn.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), CommentsActivity.class);
            intent.putExtra("forumid", forumId);
            startActivity(intent);
        });

        // Load and display forum information
        showForumInformation();

        return v;
    }

    @SuppressLint("SetTextI18n")
    private void showForumInformation() {
        // Get reference to the specific forum in the Firebase Database
        DatabaseReference forumReference = FirebaseDatabase.getInstance().getReference("Forums");
        Query forumQuery = forumReference.child(forumId);

        // Attach a listener to read the forum data
        forumQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the Forum object from the database snapshot
                Forum forum = snapshot.getValue(Forum.class);

                // Get reference to the author of the forum from the database
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users");
                Query userQuery = userReference.child(forum.getAuthorUid());

                // Attach a listener to read the author data
                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Get the User object from the database snapshot
                        User author = snapshot.getValue(User.class);

                        // Load the author's profile picture and forum picture using Glide
                        assert author != null;
                        Uri pfp = Uri.parse(author.getUri());
                        Uri forumImage = Uri.parse(forum.getImageUri());

                        Glide.with(getContext()).load(pfp).placeholder(R.drawable.default_pfp).into(profile_picture);
                        Glide.with(getContext()).load(forumImage).placeholder(R.drawable.blankscreen).into(forum_picture);

                        // Set the forum information to the TextViews
                        title.setText(forum.getTitle());
                        subject.setText("Subject : " + forum.getSubject());
                        description.setText(forum.getDescription());
                        username.setText(author.getUsername());

                        // Make the UI elements visible after setting the data
                        username.setVisibility(View.VISIBLE);
                        title.setVisibility(View.VISIBLE);
                        subject.setVisibility(View.VISIBLE);
                        description.setVisibility(View.VISIBLE);
                        profile_picture.setVisibility(View.VISIBLE);
                        // If there is no picture, then it won't show the forum picture
                        if (!forum.getImageUri().isEmpty())
                            forum_picture.setVisibility(View.VISIBLE);
                        go_to_comments_btn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
