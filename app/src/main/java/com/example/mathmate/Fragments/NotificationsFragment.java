package com.example.mathmate.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mathmate.Adapters.NotificationsAdapter;
import com.example.mathmate.Models.Notification;
import com.example.mathmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    // RecyclerView to display notifications
    private RecyclerView recyclerView;
    // List to hold Notification objects
    private List<Notification> notifications;
    // Current authenticated Firebase user
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Initialize RecyclerView and set its layout manager
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the notifications list and get the current user
        notifications = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch and display notifications
        initiateNotifications();

        return v;
    }

    // Method to fetch and display notifications
    private void initiateNotifications() {
        // Clear the existing notifications list
        notifications.clear();

        // Get reference to Notifications node in Firebase Database
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");
        // Query to fetch notifications for the current user
        Query query = notificationsRef.child(currentUser.getUid());

        // Attach a listener to read the data at our notifications reference
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterate through each child node (each notification)
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Convert each child node to a Notification object
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    // Add the notification to the list
                    notifications.add(notification);
                }
                // Update the adapter with the new notifications list
                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    // Method to update the RecyclerView's adapter
    private void updateAdapter() {
        // Initialize the adapter with the notifications list and context
        // Adapter for RecyclerView to bind notifications data
        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications, getContext());
        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(notificationsAdapter);
    }
}
