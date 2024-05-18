package com.example.mathmate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.util.Util;

public class AddForumActivity extends AppCompatActivity {

    ImageButton backBTN;
    Button continueBTN;
    Uri imageUri;
    EditText titleET, subjectET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleET = findViewById(R.id.title_et);
        subjectET = findViewById(R.id.subject_et);
        imageUri = Uri.EMPTY;

        backBTN = findViewById(R.id.go_back_btn);
        backBTN.setOnClickListener(v -> finish());

        // Passing the necessary information to the next activity
        continueBTN = findViewById(R.id.continue_btn);
        continueBTN.setOnClickListener(v -> {
            if (TextUtils.isEmpty(titleET.getText().toString())){
                titleET.setError("Title is required");
                titleET.requestFocus();
            } else if (TextUtils.isEmpty(subjectET.getText().toString())) {
                subjectET.setError("Subject is required");
                subjectET.requestFocus();
            } else {
                Intent intent = new Intent(AddForumActivity.this, ContinueAddForumActivity.class);
                intent.putExtra("title", titleET.getText().toString());
                intent.putExtra("subject", subjectET.getText().toString());
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);
            }
        });



    }
}