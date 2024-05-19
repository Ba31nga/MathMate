package com.example.mathmate.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathmate.Adapters.ForumAdapter;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ForumsFragment extends Fragment {

    // widgets
    private EditText search_bar;
    private RecyclerView recyclerView;

    // vars
    private List<Forum> mForumList;
    private DatabaseReference database;
    private ForumAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forums, container, false);

        search_bar = v.findViewById(R.id.search_bar);
        database = FirebaseDatabase.getInstance().getReference("Forums");
        recyclerView = v.findViewById(R.id.recyclerView);


        if (TextUtils.isEmpty(search_bar.getText().toString())) {
            mForumList = new ArrayList<>();
            ReadAllUsers();
        }
        else {
            initTextListener("title");
        }



        return v;
    }

    private void initTextListener(String parameter) {
        mForumList = new ArrayList<>();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search_bar.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text, parameter);
            }
        });
    }

    private void searchForMatch(String keyword, String parameter) {
        mForumList.clear();

        if (TextUtils.isEmpty(keyword)) {
            ReadAllUsers();
        } else {
            Query query = database.orderByChild(parameter).startAt(keyword);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        mForumList.add(dataSnapshot.getValue(Forum.class));

                        // update the users list view
                        updateForumList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void ReadAllUsers() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    mForumList.add(forum);
                }
                updateForumList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateForumList() {
        adapter = new ForumAdapter(mForumList);
        recyclerView.setAdapter(adapter);
    }


}