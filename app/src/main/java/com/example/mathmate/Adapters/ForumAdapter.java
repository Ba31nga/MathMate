package com.example.mathmate.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mathmate.Fragments.DisplayForumFragment;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private final List<Forum> forums;
    private final Context context;

    public ForumAdapter(List<Forum> forums, Context context) {
        this.forums = forums;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_forum_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Forum forum = forums.get(position);
        holder.title.setText(forum.getTitle());
        holder.subject.setText(forum.getSubject());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

        Query query = reference.orderByKey().equalTo(forum.getAuthorUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    Uri uriImage = Uri.parse(user.getUri());
                    Glide.with(context).load(uriImage).placeholder(R.drawable.default_pfp).into(holder.profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        holder.itemView.setOnClickListener(v -> {
            // when the forum is clicked, it transfers the user to a forum fragment
            SharedPreferences.Editor editor = context.getSharedPreferences("PREPS", context.MODE_PRIVATE).edit();
            editor.putString("forumid", forum.getId());
            editor.apply();

            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new DisplayForumFragment()).commit();
        });
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subject;
        public ImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.forum_title);
            subject = itemView.findViewById(R.id.forum_subject);
            profilePicture = itemView.findViewById(R.id.profile_picture);
        }
    }


}
