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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathmate.Models.Forum;
import com.example.mathmate.Profile.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContinueAddForumActivity extends AppCompatActivity {


    String title, subject, imageUri;
    ImageButton backBTN;
    Button submitBTN;
    EditText descriptionET;

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
        imageUri = extras.getString("imageUri");
        descriptionET = findViewById(R.id.description_et);


        submitBTN = findViewById(R.id.submit_btn);
        submitBTN.setOnClickListener(v -> {
            if (TextUtils.isEmpty(descriptionET.getText().toString())) {
                descriptionET.setError("Description is required");
                descriptionET.requestFocus();
            } else {
                // adds the forum to the realtime database
                Forum forum = new Forum(title, subject, descriptionET.getText().toString(), imageUri, FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Forums");
                dbRef.child(forum.getId()).setValue(forum).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        Toast.makeText(this, "Forum added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ContinueAddForumActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //TODO : move the user to the forum activity


                    }
                    else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}