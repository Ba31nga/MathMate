package com.example.mathmate.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mathmate.R;


public class ForumsFragment extends Fragment {


    private EditText search_bar;
    private ImageButton search_btn;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forums, container, false);

        search_bar = v.findViewById(R.id.search_bar);
        search_btn = v.findViewById(R.id.search_btn);
        recyclerView = v.findViewById(R.id.recycler_view);

        search_btn.setOnClickListener(v1 -> {
            String searchTerm = search_bar.getText().toString();
            if (TextUtils.isEmpty(searchTerm)) {
                search_bar.setError("Invalid input");
                search_bar.requestFocus();
            }

            setupSearchRecyclerView(searchTerm);
        });



        return v;
    }

    private void setupSearchRecyclerView(String searchTerm) {

    }
}