package com.example.mathmate;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mathmate.Models.Forum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ContinueAddForumActivity extends AppCompatActivity {


    private String title, subject, uriImage;
    private EditText descriptionET;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;




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

        ImageButton backBTN = findViewById(R.id.go_back_btn);
        backBTN.setOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        title = extras.getString("title");
        subject = extras.getString("subject");
        uriImage = extras.getString("imageUri");
        descriptionET = findViewById(R.id.description_et);

        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Forum images");


        Button submitBTN = findViewById(R.id.submit_btn);
        submitBTN.setOnClickListener(v -> {
            if (TextUtils.isEmpty(descriptionET.getText().toString())) {
                descriptionET.setError("Description is required");
                descriptionET.requestFocus();
            } else {
                uploadImage();
            }
        });


    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(uriImage);
        Forum forum = new Forum(title, subject, descriptionET.getText().toString(), firebaseUser.getUid());

        // Saves the image with id of the forum
        StorageReference fileReference = storageReference.child(forum.getId() + "." + getFileExtension(uri));

        // Upload image to storage
        fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {

            // adds it to the realtime database
            forum.setImageUri(uri1.toString());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Forums");
            reference.child(forum.getId()).setValue(forum);

            // Return to user profile
            Toast.makeText(ContinueAddForumActivity.this, "Forum added successfully", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            finish();
        }));

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}