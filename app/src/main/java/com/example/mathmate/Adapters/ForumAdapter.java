package com.example.mathmate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    private Context context;
    private List<Forum> forums;

    public ForumAdapter(Context context, List<Forum> forums) {
        this.context = context;
        this.forums = forums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_forum_row, parent, false);
        return new ForumAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Forum forum = forums.get(position);

        holder.title.setText(forum.getTitle());
        holder.subject.setText(forum.getSubject());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = reference.orderByValue().equalTo(forum.getAuthorUid());
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
            // TODO : move the user to the forum activity
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subject;
        public ImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_et);
            subject = itemView.findViewById(R.id.subject_et);
            profilePicture = itemView.findViewById(R.id.profile_picture);
        }
    }


}
