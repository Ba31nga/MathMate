package com.example.mathmate;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.User;
import com.example.mathmate.Profile.ChangeProfilePictureActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddForumActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageButton backBTN;
    ImageView forumImage, uploadImage;
    Button continueBTN, takePhotoBTN, browseGalleryBTN;
    Uri uriImage;
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
        uriImage = Uri.EMPTY;

        forumImage = findViewById(R.id.forum_image);
        uploadImage = findViewById(R.id.upload_img);
        forumImage.setVisibility(View.GONE);

        backBTN = findViewById(R.id.go_back_btn);
        backBTN.setOnClickListener(v -> finish());

        takePhotoBTN = findViewById(R.id.take_photo_btn);
        browseGalleryBTN = findViewById(R.id.browse_gallery_btn);

        // Adding photo to the forum
        browseGalleryBTN.setOnClickListener(v -> openFileChooser());


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
                intent.putExtra("imageUri", uriImage.toString());
                startActivity(intent);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            Glide.with(AddForumActivity.this).load(uriImage).into(forumImage);
            forumImage.setVisibility(View.VISIBLE);
            uploadImage.setVisibility(View.GONE);
        }
    }

}