package com.example.mathmate;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mathmate.Adapters.CommentAdapter;
import com.example.mathmate.Models.Comment;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.Notification;
import com.example.mathmate.Models.User;
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

public class CommentsActivity extends AppCompatActivity {


    // Views that are related to the "writing a comment" section
    private TextView username;
    private ImageView profile_picture;
    private FirebaseUser currentUser;

    private EditText message;


    // recycler view for the list of comments

    private RecyclerView recyclerView;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;


    // forum id that the comments section are related to
    private String forumId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // current logged user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // comment views
        username = findViewById(R.id.username);
        profile_picture = findViewById(R.id.profile_picture);
        message = findViewById(R.id.comment_input);

        // initiating the recycler view and the comments adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments, CommentsActivity.this);

        // go back button
        ImageButton go_back_btn = findViewById(R.id.back_btn);
        go_back_btn.setOnClickListener(v -> finish());

        // the id of the forum that the comment section is related to
        forumId = getIntent().getStringExtra("forumid");

        // showing the information on the "write a comment" section
        showUserInformation();
        // showing the lists of the comments related to the forum
        showComments();

        // "post comment" button
        Button post_btn = findViewById(R.id.post_btn);
        post_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(message.getText())){
                message.setError("You must write something");
                message.requestFocus();
            }
            else {
                addComment();
            }
        });
    }

    private void showComments() {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        commentsRef.child(forumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                comments.sort((o1, o2) -> Integer.compare(o2.getLikes(), o1.getLikes()));

                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateAdapter() {
        commentAdapter = new CommentAdapter(comments, CommentsActivity.this);
        recyclerView.setAdapter(commentAdapter);
    }

    private void addComment() {
        Comment comment = new Comment(forumId, currentUser.getUid(), message.getText().toString());
        DatabaseReference referenceComments = FirebaseDatabase.getInstance().getReference("Comments");
        // Adding the comment to the realtime database
        referenceComments.child(forumId).child(comment.getId()).setValue(comment);

        // adds more questions answered to the user
        addUserAnswerPoint();

        // resets the message box
        message.setText("");

        // gives the user a notification that he got answered
        addNotification(comment);
    }

    private void addUserAnswerPoint() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query userQuery = userReference.child(currentUser.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                user.addAnswers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addNotification(Comment comment) {
        // First let's take the information about what question was answered and the name of the user who answered it

        DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("Forums");
        Query forumQuery = forumRef.child(forumId);
        forumQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // The forum that was answered
                Forum forum = snapshot.getValue(Forum.class);
                assert forum != null;
                if (!forum.getAuthorUid().equals(comment.getAuthorId())) {
                    // the author of the comment is not the author of the forum

                    // getting the name of the author of the comment
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
                    Query usersQuery = usersRef.child(comment.getAuthorId());
                    usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User author_of_comment = snapshot.getValue(User.class);
                            assert author_of_comment != null;
                            String username = author_of_comment.getUsername();

                            // adds the notification for the author of the forum
                            String text = username + " has answered your question : " + forum.getTitle();
                            Notification notification = new Notification(text, forumId, comment.getId());
                            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
                            notificationRef.child(forum.getAuthorUid()).child(comment.getId()).setValue(notification);
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


    private void showUserInformation() {
        assert currentUser != null;
        username.setText(currentUser.getDisplayName());
        Glide.with(this).load(currentUser.getPhotoUrl()).placeholder(R.drawable.default_pfp).into(profile_picture);
    }


}