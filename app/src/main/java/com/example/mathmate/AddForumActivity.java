package com.example.mathmate;

import static com.example.mathmate.Utils.NotesUtil.errorMessage;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
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
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mathmate.Utils.NetworkChangeReceiver;
import com.example.mathmate.Utils.RegisterActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class AddForumActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image from gallery
    private static final int CAMERA_PERMISSION_CODE = 1; // Request code for camera permission

    private ImageView forumImage, uploadImage; // ImageView for displaying the selected or captured image
    private Uri uriImage; // URI of the selected or captured image
    private EditText titleET, subjectET; // EditTexts for the forum title and subject

    private ActivityResultLauncher<Uri> takePictureLauncher; // Launcher for taking a picture
    private NetworkChangeReceiver networkChangeReceiver;

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

        // Initialize EditTexts for title and subject
        titleET = findViewById(R.id.title_et);
        subjectET = findViewById(R.id.subject_et);

        // Initialize ImageViews for forum image and upload image button
        forumImage = findViewById(R.id.forum_image);
        uploadImage = findViewById(R.id.upload_img);
        forumImage.setVisibility(View.GONE); // Hide forum image by default

        uriImage = createUri(); // Create a URI for the image file
        registerPictureLauncher(); // Register the launcher for taking a picture

        // Initialize back button and set its click listener to finish the activity
        ImageButton backBTN = findViewById(R.id.go_back_btn);
        backBTN.setOnClickListener(v -> finish());

        // Initialize buttons for taking a photo and browsing gallery
        Button takePhotoBTN = findViewById(R.id.take_photo_btn);
        Button browseGalleryBTN = findViewById(R.id.browse_gallery_btn);

        // Set click listener for browsing gallery to open the file chooser
        browseGalleryBTN.setOnClickListener(v -> openFileChooser());

        // Set click listener for taking a photo to check camera permission and open the camera
        takePhotoBTN.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        // Initialize continue button and set its click listener to pass information to the next activity
        Button continueBTN = findViewById(R.id.continue_btn);
        continueBTN.setOnClickListener(v -> {
            // Validate title and subject inputs
            if (TextUtils.isEmpty(titleET.getText().toString())) {
                titleET.setError("Title is required");
                titleET.requestFocus();
            } else if (TextUtils.isEmpty(subjectET.getText().toString())) {
                subjectET.setError("Subject is required");
                subjectET.requestFocus();
            } else {
                // Pass data to the next activity
                Intent intent = new Intent(AddForumActivity.this, ContinueAddForumActivity.class);
                intent.putExtra("title", titleET.getText().toString());
                intent.putExtra("subject", subjectET.getText().toString());
                intent.putExtra("imageUri", uriImage.toString());
                startActivity(intent);
            }
        });
    }

    // Create a URI for the image file using FileProvider
    private Uri createUri() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.mathmate.fileProvider",
                imageFile
        );
    }

    // Register the launcher for taking a picture
    private void registerPictureLauncher() {
        // Register an ActivityResultLauncher for taking a picture and handling the result
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        // Check if the picture was taken successfully
                        if (result) {
                            // Set the forum image to visible and hide the upload image button
                            forumImage.setVisibility(View.VISIBLE);
                            uploadImage.setVisibility(View.GONE);
                            // Use Glide to load the captured image into the ImageView
                            Glide.with(AddForumActivity.this).load(uriImage).into(forumImage);
                        } else {
                            // Display an error message if the picture was not taken successfully
                            errorMessage(AddForumActivity.this, "Failed to capture image");
                        }
                    }
                }
        );
    }

    // Check camera permission and open the camera if permission is granted
    private void checkCameraPermissionAndOpenCamera() {
        if (ActivityCompat.checkSelfPermission(AddForumActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission if not granted
            ActivityCompat.requestPermissions(AddForumActivity.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            // Launch the camera if permission is granted
            takePictureLauncher.launch(uriImage);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Launch the camera if permission is granted
                takePictureLauncher.launch(uriImage);
            } else {
                // Show a failure message if permission is not granted
                MotionToast.Companion.darkToast(AddForumActivity.this,
                        "Failure!",
                        "Please allow camera permissions",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(AddForumActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        }
    }

    // Open the file chooser to pick an image from the gallery
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            uriImage = data.getData();
            // Use Glide to load the selected image into the ImageView
            Glide.with(AddForumActivity.this).load(uriImage).into(forumImage);
            // Set the forum image to visible and hide the upload image button
            forumImage.setVisibility(View.VISIBLE);
            uploadImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(networkChangeReceiver);
    }
}
