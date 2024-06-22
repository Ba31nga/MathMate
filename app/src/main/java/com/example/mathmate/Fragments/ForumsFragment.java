package com.example.mathmate.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathmate.Adapters.ForumAdapter;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ForumsFragment extends Fragment {

    // UI elements
    private EditText search_bar;
    private RecyclerView recyclerView;

    // Variables to hold forum data and reference to Firebase Database
    private List<Forum> forums;
    private DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forums, container, false);

        // Initialize UI elements
        search_bar = v.findViewById(R.id.search_bar);

        // Get reference to "Forums" node in Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Forums");

        // Initialize RecyclerView and set its layout manager
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the forums list
        forums = new ArrayList<>();

        // Read and display forums
        readForums();

        // Add a text change listener to the search bar for searching forums
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Search forums as user types in the search bar
                searchForums(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return v;
    }

    // Method to search forums based on the query
    private void searchForums(String s) {
        // Attach a listener to read the data at our forums reference
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing forums list
                forums.clear();
                // Iterate through each child node (each forum)
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Convert each child node to a Forum object
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    assert forum != null;
                    // Add the forum to the list if it matches the search query
                    if (forum.getTitle().toLowerCase().startsWith(s) || forum.getSubject().toLowerCase().startsWith(s))
                        forums.add(forum);
                }
                // Update the adapter with the new forums list
                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    // Method to read all forums from the database
    private void readForums() {
        // Attach a listener to read the data at our forums reference
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If the search bar is empty, display all forums
                if (search_bar.getText().toString().isEmpty()) {
                    // Clear the existing forums list
                    forums.clear();
                    // Iterate through each child node (each forum)
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Convert each child node to a Forum object
                        Forum forum = dataSnapshot.getValue(Forum.class);
                        // Add the forum to the list
                        forums.add(forum);
                    }
                    // Update the adapter with the new forums list
                    updateAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    // Method to update the RecyclerView's adapter
    private void updateAdapter() {
        // Initialize the adapter with the forums list and context
        ForumAdapter adapter = new ForumAdapter(forums, getContext());
        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
    }
}
