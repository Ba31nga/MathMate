package com.example.mathmate.Utils;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_input, username_input, password_input, confirm_input;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterActivity";
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_input = findViewById(R.id.email_input);
        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        confirm_input = findViewById(R.id.confirm_input);
        progressBar = findViewById(R.id.progressBar2);

        progressBar.setVisibility(View.GONE);

        Button create_an_account_btn = findViewById(R.id.create_an_account_button);
        Button back_to_login_btn = findViewById(R.id.back_to_login_button);

        // The "Back to login" button was clicked
        back_to_login_btn.setOnClickListener(v -> finish());

        // The "Create an account" button was clicked
        create_an_account_btn.setOnClickListener(v -> {
            // Extract user's entered information
            String email = email_input.getText().toString();
            String username = username_input.getText().toString();
            String password = password_input.getText().toString();
            String confirm = confirm_input.getText().toString();

            // Validating user's entered information
            if (TextUtils.isEmpty(email)) {
                // The user didn't put an email address
                email_input.setError("Email address is required");
                email_input.requestFocus();
            } else if (TextUtils.isEmpty(username)) {
                // ... username
                password_input.setError("Username is required");
                password_input.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                // ... password
                password_input.setError("Password is required");
                password_input.requestFocus();
            } else if (TextUtils.isEmpty(confirm)) {
                // ...
                confirm_input.setError("Please confirm your password");
                confirm_input.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // The user didn't write a proper email address
                email_input.setError("Valid email is required");
                email_input.requestFocus();
            } else if (password.length() < 6) {
                // User's password is too short
                password_input.setError("Password should be in length of at least 6");
                password_input.requestFocus();
            } else if (!password.equals(confirm)) {
                // User's passwords do not match
                password_input.setError("Passwords do not match");
                password_input.requestFocus();
            } else {
                registerUser(email, username, password);
            }
        });
    }

    // a function that registers the user by the information given by him
    private void registerUser(String email, String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();


        // Checks if username is unique
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = dbRef.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username_input.requestFocus();
                    username_input.setError("Username is already taken");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // adds user to the firebase authenticator
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // user was added to the firebase authenticator
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        // update display name of user
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                        assert firebaseUser != null;
                        firebaseUser.updateProfile(profileChangeRequest);

                        // enter user data into the firebase realtime database
                        User userDetails = new User(username);

                        // adds user's credentials to the realtime database under "registered users"
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                        referenceProfile.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                // user details was added to the realtime database
                                MotionToast.Companion.darkToast(RegisterActivity.this,
                                        "Success!",
                                        "User registered successfully, please VERIFY your email",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(RegisterActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));

                                // sends verification email
                                firebaseUser.sendEmailVerification();

                                // returns to login activity
                                finish();

                            } else {
                                progressBar.setVisibility(View.GONE);
                                MotionToast.Companion.darkToast(RegisterActivity.this,
                                        "Failure!",
                                        "Something went wrong, please try again",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(RegisterActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                            }
                        });

                    } else {
                        // handling exceptions
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            password_input.setError("The password is too weak");
                            password_input.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            email_input.setError("Invalid email, please try again");
                            email_input.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            email_input.setError("Email is already in use");
                        } catch (Exception e) {
                            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MotionToast.Companion.darkToast(RegisterActivity.this,
                        "Failure!",
                        "Something went wrong, please try again",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(RegisterActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });

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