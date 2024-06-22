package com.example.mathmate.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final List<User> users;
    private final Context context;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);
        return new UserAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = users.get(position);
        int rank = position + 1;

        holder.rank.setText("#" + rank);
        Glide.with(context).load(user.getUri()).placeholder(R.drawable.default_pfp).into(holder.profilePicture);
        assert currentUser != null;
        if (!user.getUsername().equals(currentUser.getDisplayName()))
            holder.username.setText(user.getUsername());
        else
            holder.username.setText("Your score");
        holder.score.setText(String.valueOf(user.getUserPoints()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, score, rank;
        public ImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            score = itemView.findViewById(R.id.score);
            rank = itemView.findViewById(R.id.rank);
            profilePicture = itemView.findViewById(R.id.profile_picture);
        }
    }
}
