package com.example.mathmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentsActivity extends AppCompatActivity {
    private TextView username;
    private ImageView profile_picture;
    FirebaseUser currentUser;

    private ImageButton go_back_btn;
    private Button post_btn;
    private EditText message;
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

        username = findViewById(R.id.username);
        profile_picture = findViewById(R.id.profile_picture);
        message = findViewById(R.id.comment_input);
        post_btn = findViewById(R.id.post_btn);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        go_back_btn = findViewById(R.id.back_btn);
        go_back_btn.setOnClickListener(v -> {
            finish();
        });

        forumId = getIntent().getStringExtra("forumid");

        showInformation();

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

    private void addComment() {
        Comment comment = new Comment(forumId, currentUser.getUid(), message.getText().toString());
        DatabaseReference referenceComments = FirebaseDatabase.getInstance().getReference("Comments");
        referenceComments.child(comment.getId()).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this, "Comment was added successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CommentsActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showInformation() {
        assert currentUser != null;
        username.setText(currentUser.getDisplayName());
        Glide.with(this).load(currentUser.getPhotoUrl()).placeholder(R.drawable.default_pfp).into(profile_picture);
    }


}