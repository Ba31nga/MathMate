package com.example.mathmate.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.mathmate.Adapters.ForumAdapter;
import com.example.mathmate.Adapters.UserAdapter;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.User;
import com.example.mathmate.Profile.ChangeProfilePictureActivity;
import com.example.mathmate.Profile.UpdateProfileActivity;
import com.example.mathmate.R;
import com.example.mathmate.Utils.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    // widgets
    private LinearLayout questions_asked;
    private TextView username_tv;
    private TextView bio_tv;
    private TextView QA_tv;
    private TextView points_tv;
    private TextView your_score;
    private TextView your_rank;
    private ImageView pfp;
    private ProgressBar progressBar;
    private RecyclerView forumRecycler, userRecycler;
    private View your_info;
    private ScrollView scrollView;

    // vars
    private String username, bio, QA, points;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ForumAdapter forumAdapter;
    private UserAdapter userAdapter;
    private List<Forum> forums;
    private List<User> users;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        scrollView = v.findViewById(R.id.main);

        // the info on the top of the screen
        username_tv = v.findViewById(R.id.username_tv);
        bio_tv = v.findViewById(R.id.bio_tv);
        QA_tv = v.findViewById(R.id.QA_tv);
        points_tv = v.findViewById(R.id.points_tv);
        pfp = v.findViewById(R.id.pfp);

        // authentication
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return v;
        }

        // your info under the leaderboard
        TextView your_username = v.findViewById(R.id.username);
        ImageView your_pfp = v.findViewById(R.id.profile_picture);
        your_rank = v.findViewById(R.id.rank);
        your_score = v.findViewById(R.id.score);
        your_info = v.findViewById(R.id.your_info);
        your_username.setText("Your score");
        Glide.with(getContext()).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.default_pfp).into(your_pfp);

        // recycler views + their settings
        forumRecycler = v.findViewById(R.id.forumRecycler);
        forumRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        forums = new ArrayList<>();

        userRecycler = v.findViewById(R.id.userRecycler);
        userRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();

        // progress bar
        progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // buttons
        ImageButton logout_btn = v.findViewById(R.id.logout_btn);
        ImageButton settings_btn = v.findViewById(R.id.settings_btn);

        // question asked linear layout which holds the text and the recycler view
        questions_asked = v.findViewById(R.id.questions_asked);
        questions_asked.setVisibility(View.GONE);

        // transfers the user to an activity where he can change his profile picture
        pfp.setOnClickListener(v12 -> {
            Intent intent = new Intent(getContext(), ChangeProfilePictureActivity.class);
            startActivity(intent);
        });

        settings_btn.setOnClickListener(v13 -> {
            // transfers user to the settings
            Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
            startActivity(intent);
        });

        // logging out the user from his account
        logout_btn.setOnClickListener(v1 -> {
            authProfile.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // shows the data on the screen
        showUserProfile(firebaseUser);
        // handles the recycler views
        setUpForumRecycler();
        setUpUserRecycler();

        // showing the info under the leaderboard only when it doesn't shows on one of the visible
        // items of the leaderboard recyclerview
        infoShowingHandler();

        return v;
    }

    private void infoShowingHandler() {
        // decides when to show the information under the leaderboard
        userRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                // Layout manager of the adapter
                assert layoutManager != null;
                // the positions of the first and the last visible items on the recycler view
                int firstItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                int lastItemPosition = layoutManager.findLastVisibleItemPosition();

                // the first and last shown user on the recyclerView
                UserAdapter.ViewHolder firstShownUser = (UserAdapter.ViewHolder) userRecycler.findViewHolderForAdapterPosition(firstItemPosition);
                UserAdapter.ViewHolder lastShownUser = (UserAdapter.ViewHolder) userRecycler.findViewHolderForAdapterPosition(lastItemPosition);

                assert firstShownUser != null;
                // the first rank that is shown on the recyclerview
                int firstRank = Integer.parseInt(((String) firstShownUser.rank.getText()).substring(1));
                assert lastShownUser != null;
                // the last rank that is shown on the recyclerview
                int lastRank = Integer.parseInt(((String) lastShownUser.rank.getText()).substring(1));
                // the rank of the current user
                int userRank = Integer.parseInt(((String) your_rank.getText()).substring(1));

                if (lastRank >= userRank && userRank >= firstRank) {
                    // the user is shown on the leaderboard recycler view
                    your_info.setVisibility(View.GONE);
                } else {
                    // the user isn't showing on the leaderboard recyclerview
                    your_info.setVisibility(View.VISIBLE);
                    // scrolls the user down to the bottom
                    scrollView.post(() -> scrollView.scrollTo(0, scrollView.getBottom()));
                }
            }
        });
    }

    private void setUpForumRecycler() {
        // all the forums that the current user created
        Query query = FirebaseDatabase.getInstance().getReference("Forums").orderByChild("authorUid").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                forums.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // adds all of the forums that the current user created to the list
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    forums.add(forum);
                }
                if (!forums.isEmpty()) {
                    // the user asked at least 1 question
                    questions_asked.setVisibility(View.VISIBLE);
                }
                // adds new adapter to the recycler view (with the list that was created)
                forumAdapter = new ForumAdapter(forums, getContext());
                forumRecycler.setAdapter(forumAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load forums: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpUserRecycler() {
        // manages the leaderboard
        // a reference to all of the registered users
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // adds all of the registered users to the list
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }

                // sorts them by the points they have
                users.sort((o1, o2) -> Integer.compare(o2.getUserPoints(), o1.getUserPoints()));
                int rank = 0;
                for (User user : users) {
                    rank++;
                    if (user.getUsername().equals(firebaseUser.getDisplayName())) {
                        your_rank.setText("#" + rank);
                    }
                }

                // adds the new adapter to the recycler view with the new list that was created
                userAdapter = new UserAdapter(users, getContext());
                userRecycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    your_score.setText(points);

                    // Set user DP (after user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();

                    // Imageview setImageURI() should not be used with regular URIs - so we are using Glide
                    Glide.with(getContext())
                            .load(uri)
                            .placeholder(R.drawable.default_pfp)
                            .error(R.drawable.default_pfp)
                            .into(pfp);

                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
