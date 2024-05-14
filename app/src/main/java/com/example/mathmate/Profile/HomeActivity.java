package com.example.mathmate.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mathmate.AddForumActivity;
import com.example.mathmate.Fragments.ForumsFragment;
import com.example.mathmate.Fragments.ProfileFragment;
import com.example.mathmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    // m4thmat3@gmail.com

    BottomNavigationView bottom_nav_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        replaceFragment(new ProfileFragment());

        bottom_nav_bar = findViewById(R.id.bottomNavigationView);
        bottom_nav_bar.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (menuItem.getItemId() == R.id.add_forum) {
                Intent intent = new Intent(HomeActivity.this, AddForumActivity.class);
                startActivity(intent);
            } else if (menuItem.getItemId() == R.id.forums) {
                replaceFragment(new ForumsFragment());
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


}