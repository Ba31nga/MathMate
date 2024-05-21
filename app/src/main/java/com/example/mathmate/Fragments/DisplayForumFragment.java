package com.example.mathmate.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private String forumId;
    private Forum forum;
    private User author;

    TextView username, title, subject, description;
    ImageView profile_picture, forum_picture;
    Button go_to_comments_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display_forum, container, false);

        username = v.findViewById(R.id.username);
        title = v.findViewById(R.id.title);
        subject = v.findViewById(R.id.subject);
        description = v.findViewById(R.id.descrption);
        profile_picture = v.findViewById(R.id.profile_picture);
        forum_picture = v.findViewById(R.id.forum_image);
        go_to_comments_btn = v.findViewById(R.id.go_to_comments_btn);

        username.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        subject.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        profile_picture.setVisibility(View.GONE);
        forum_picture.setVisibility(View.GONE);

        SharedPreferences prefs = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        forumId = prefs.getString("forumid", "none");

        go_to_comments_btn.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), CommentsActivity.class);
            intent.putExtra("forumid", forumId);
            startActivity(intent);
        });





        showForumInformation();

        return v;
    }

    @SuppressLint("SetTextI18n")
    private void showForumInformation() {

        // Extracting the forum reference from the database
        DatabaseReference forumReference = FirebaseDatabase.getInstance().getReference("Forums");
        Query forumQuery = forumReference.orderByKey().equalTo(forumId);
        forumQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    forum = dataSnapshot.getValue(Forum.class);
                    // Extracting the user (the author) reference from the database
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    Query userQuery = userReference.orderByKey().equalTo(forum.getAuthorUid());
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                author = dataSnapshot.getValue(User.class);
                                // Dealing with the pictures
                                Uri pfp = Uri.parse(author.getUri());
                                Uri forumImage = Uri.parse(forum.getImageUri());

                                Glide.with(getContext()).load(pfp).placeholder(R.drawable.default_pfp).into(profile_picture);
                                if (pfp.equals(Uri.EMPTY)) {
                                    forum_picture.setVisibility(View.GONE);
                                } else {
                                    Glide.with(getContext()).load(forumImage).placeholder(R.drawable.blankscreen).into(forum_picture);
                                }

                                // dealing with text views
                                title.setText(forum.getTitle());
                                subject.setText("Subject : " + forum.getSubject());
                                description.setText(forum.getDescription());
                                username.setText(author.getUsername());

                                username.setVisibility(View.VISIBLE);
                                title.setVisibility(View.VISIBLE);
                                subject.setVisibility(View.VISIBLE);
                                description.setVisibility(View.VISIBLE);
                                profile_picture.setVisibility(View.VISIBLE);
                                forum_picture.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}