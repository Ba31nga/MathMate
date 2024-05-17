package com.example.mathmate.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mathmate.Models.User;
import com.example.mathmate.Profile.HomeActivity;
import com.example.mathmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email_input, password_input;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_btn = findViewById(R.id.login_button);
        Button register_btn = findViewById(R.id.register_button);

        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);

        authProfile = FirebaseAuth.getInstance();

        register_btn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        login_btn.setOnClickListener(v -> {
            String email = email_input.getText().toString();
            String password = password_input.getText().toString();

            // Validating user's entered information
            if (TextUtils.isEmpty(email)) {
                // The user didn't put an email address
                email_input.setError("Email address is required");
                email_input.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // The user didn't write a proper email address
                email_input.setError("Valid email is required");
                email_input.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                // ... password
                password_input.setError("Password is required");
                password_input.requestFocus();
            } else {
                // login the user in
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {

        // log the user in
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // user's credentials are correct

                // get instance of current user
                FirebaseUser firebaseUser = authProfile.getCurrentUser();

                // check if email is verified
                assert firebaseUser != null;
                if (firebaseUser.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                } else {
                    firebaseUser.sendEmailVerification();
                    authProfile.signOut();
                    // alerting the user
                    showAlertDialog();
                }
            } else {
                // handle exceptions
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    email_input.setError("Email does not exist or is not valid");
                    email_input.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    email_input.setError("Invalid credentials, please try again");
                    email_input.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // Checks if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null && authProfile.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void showAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("You cannot continue further without verifying your email");

        // opens email app if user clicks the continue button
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // to email app in new window and not within the app itself
            startActivity(intent);
        });

        // create the alert dialog
        AlertDialog alertDialog = builder.create();

        // show the AlertDialog
        alertDialog.show();

    }
}