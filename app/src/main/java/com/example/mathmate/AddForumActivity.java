package com.example.mathmate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class AddForumActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;


    private ImageButton backBTN;
    private ImageView forumImage, uploadImage;
    private Button continueBTN, takePhotoBTN, browseGalleryBTN;
    private Uri uriImage;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private EditText titleET, subjectET;

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

        takePhotoBTN.setOnClickListener(v -> {
            checkCameraPermissionAndOpenCamera();
        });


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


    private void capturePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ActivityCompat.checkSelfPermission(AddForumActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddForumActivity.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            capturePicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePicture();
            } else {
                Toast.makeText(this, "Please allow camera permissions ", Toast.LENGTH_SHORT).show();
            }
        }
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            assert imageBitmap != null;

            // converts bitmap to uri
            uriImage = getImageUri(AddForumActivity.this, imageBitmap);

            Glide.with(AddForumActivity.this).load(uriImage).into(forumImage);
            forumImage.setVisibility(View.VISIBLE);
            uploadImage.setVisibility(View.GONE);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}