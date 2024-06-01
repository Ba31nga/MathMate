package com.example.mathmate.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mathmate.AddForumActivity;
import com.example.mathmate.Fragments.ForumsFragment;
import com.example.mathmate.Fragments.NotificationsFragment;
import com.example.mathmate.Fragments.ProfileFragment;
import com.example.mathmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        checkForNotifications();

        bottom_nav_bar.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (menuItem.getItemId() == R.id.add_forum) {
                Intent intent = new Intent(HomeActivity.this, AddForumActivity.class);
                startActivity(intent);
            } else if (menuItem.getItemId() == R.id.forums) {
                replaceFragment(new ForumsFragment());
            }
            else if (menuItem.getItemId() == R.id.notifications) {
                replaceFragment(new NotificationsFragment());
            }
            return true;
        });
    }

    private void checkForNotifications() {
        // checks if the user has notifications
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");
        Query notificationsQuery = notificationsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        notificationsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean hasNotifications = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // has notifications
                    hasNotifications = true;
                    break;
                }

                if (hasNotifications) {
                    bottom_nav_bar.getMenu().getItem(3).setIcon(R.drawable.notification_active);
                } else {
                    bottom_nav_bar.getMenu().getItem(3).setIcon(R.drawable.notifications);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


}