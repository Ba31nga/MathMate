package com.example.mathmate.Profile;

import static com.example.mathmate.Utils.NotesUtil.successMessage;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.example.mathmate.Utils.NetworkChangeReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChangeProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageButton back;
    private ImageView new_pfp;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;
    private ProgressBar progressBar;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_profile_picture);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.go_back);
        back.setOnClickListener(v -> finish());

        Button choose_pfp = findViewById(R.id.change_pfp);
        Button save_pfp_btn = findViewById(R.id.save_btn);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        new_pfp = findViewById(R.id.new_pfp);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Display pics");

        Uri uri = firebaseUser.getPhotoUrl();

        // Set user's current DP in imageView (if uploaded already).
        // Regular URIs.
        Glide.with(ChangeProfilePictureActivity.this).load(uri).placeholder(R.drawable.default_pfp).into(new_pfp);

        choose_pfp.setOnClickListener(v -> openFileChooser());

        save_pfp_btn.setOnClickListener(v -> uploadImage());

    }

    private void uploadImage() {
        if (uriImage != null) {

            // Save the image with uid of the current logged user
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "." + getFileExtension(uriImage));
            progressBar.setVisibility(View.VISIBLE);

            // Upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                firebaseUser = authProfile.getCurrentUser();

                // Finally set the display image of the user after upload
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                firebaseUser.updateProfile(profileUpdates);

                // add it to the realtime database
                User update = new User(firebaseUser.getDisplayName(), uri.toString());
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                referenceProfile.child(firebaseUser.getUid()).setValue(update);

                // Return to user profile
                successMessage(ChangeProfilePictureActivity.this, "New profile picture added successfully");
                progressBar.setVisibility(View.GONE);
                finish();
            }));
        }
    }

    // Obtain file extension of the image
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
            Glide.with(ChangeProfilePictureActivity.this).load(uriImage).placeholder(R.drawable.default_pfp).into(new_pfp);
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