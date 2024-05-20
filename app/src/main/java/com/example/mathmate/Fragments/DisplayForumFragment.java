package com.example.mathmate.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mathmate.R;


public class DisplayForumFragment extends Fragment {

    String forumId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display_forum, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        forumId = prefs.getString("forumid", "none");
        Toast.makeText(getContext(), "id : " + forumId, Toast.LENGTH_SHORT).show();



        return v;
    }
}