package com.example.mathmate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.Comment;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.Like;
import com.example.mathmate.Models.User;
import com.example.mathmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<Comment> comments;
    private final Context context;

    public CommentAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        return new CommentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        holder.comment_content.setText(comment.getMessage());



        // Displaying user data e.g username and profile picture
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = reference.orderByKey().equalTo(comment.getAuthorId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    Uri uriImage = Uri.parse(user.getUri());
                    Glide.with(context).load(uriImage).placeholder(R.drawable.default_pfp).into(holder.profile_picture);
                    holder.username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        // Handles the likes on the comment
        reference = FirebaseDatabase.getInstance().getReference("Likes").child(comment.getId());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        query = reference.orderByKey().equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if the current user liked the comment
                boolean isLiked = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // the current user already liked the comment

                    isLiked = true;
                    holder.like_btn.setImageResource(R.drawable.like_active);
                    holder.like_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // deletes the like
                            dataSnapshot.getRef().removeValue();
                        }
                    });
                }

                if (!isLiked) {
                    // the current user didn't liked the comment yet
                    holder.like_btn.setOnClickListener(new View.OnClickListener() {
                        // adds like to the comment
                        @Override
                        public void onClick(View v) {
                            Like like = new Like(currentUser.getUid());

                            // adds the like reference to the realtime database
                            FirebaseDatabase.getInstance().getReference("Likes").child(comment.getId()).child(currentUser.getUid()).setValue(like).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // TODO : add notification to the user
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // handling like count
        reference = FirebaseDatabase.getInstance().getReference("Likes").child(comment.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    count++;
                holder.like_count.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, comment_content, like_count;
        public ImageView profile_picture;
        public ImageButton like_btn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            comment_content = itemView.findViewById(R.id.comment);
            profile_picture = itemView.findViewById(R.id.profile_picture);
            like_btn = itemView.findViewById(R.id.like_btn);
            like_count = itemView.findViewById(R.id.like_counter);
        }
    }
}