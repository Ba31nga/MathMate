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


    private RecyclerView recyclerView;
    private List<Notification> notifications;
    private FirebaseUser currentUser;
    private NotificationsAdapter notificationsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notifications = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        initiateNotifications();

        return v;
    }

    private void initiateNotifications() {
        notifications.clear();

        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");
        Query query = notificationsRef.child(currentUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    notifications.add(notification);
                }
                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateAdapter() {
        notificationsAdapter = new NotificationsAdapter(notifications, getContext());
        recyclerView.setAdapter(notificationsAdapter);
    }
}