package com.example.mathmate;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContinueAddForumActivity extends AppCompatActivity {

    Uri imageUri;
    String title, subject;
    ImageButton backBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_continue_add_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBTN = findViewById(R.id.go_back_btn);
        backBTN.setOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        title = extras.getString("title");
        subject = extras.getString("subject");
        imageUri = Uri.parse(extras.getString("imageUri"));


    }
}