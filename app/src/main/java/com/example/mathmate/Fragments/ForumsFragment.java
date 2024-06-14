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
import androidx.recyclerview.widget.DividerItemDecoration;
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

    // widgets
    private EditText search_bar;
    private RecyclerView recyclerView;

    // vars
    private List<Forum> forums;
    private DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forums, container, false);

        search_bar = v.findViewById(R.id.search_bar);

        database = FirebaseDatabase.getInstance().getReference("Forums");

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        forums = new ArrayList<>();

        readForums();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchForums(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        return v;
    }

    private void searchForums(String s) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                forums.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    assert forum != null;
                    if (forum.getTitle().toLowerCase().startsWith(s) || forum.getSubject().toLowerCase().startsWith(s))
                        forums.add(forum);
                }
                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readForums() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_bar.getText().toString().isEmpty()) {
                    forums.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Forum forum = dataSnapshot.getValue(Forum.class);
                        forums.add(forum);
                    }
                    updateAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }





    private void updateAdapter() {
        ForumAdapter adapter = new ForumAdapter(forums, getContext());
        recyclerView.setAdapter(adapter);
    }






}