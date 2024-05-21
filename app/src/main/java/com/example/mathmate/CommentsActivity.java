package com.example.mathmate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CommentsActivity extends AppCompatActivity {
    private TextView username;
    private ImageView profile_picture;

    private ImageButton go_back_btn;
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

        go_back_btn = findViewById(R.id.back_btn);
        go_back_btn.setOnClickListener(v -> {
            finish();
        });

        forumId = getIntent().getStringExtra("forumid");
        Toast.makeText(this, forumId, Toast.LENGTH_SHORT).show();

        showInformation();
    }

    private void showInformation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        assert currentUser != null;
        username.setText(currentUser.getDisplayName());
        Glide.with(this).load(currentUser.getPhotoUrl()).placeholder(R.drawable.default_pfp).into(profile_picture);
    }


}