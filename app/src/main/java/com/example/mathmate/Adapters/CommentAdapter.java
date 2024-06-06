package com.example.mathmate.Adapters;

import android.annotation.SuppressLint;
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
import com.example.mathmate.Models.User;
import com.example.mathmate.R;
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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        holder.delete_btn.setVisibility(View.GONE);
        holder.comment_content.setText(comment.getMessage());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Displaying user data e.g username and profile picture
        displayUserData(holder, comment);

        // checking if the current user already liked to comment or not
        isLiked(comment, holder);

        // showing how much likes does the comment have
        showLikeCount(comment, holder);


        assert currentUser != null;
        if (currentUser.getUid().equals(comment.getAuthorId())) {
            // if the current logged user is the one who wrote the comment, allow him to delete the comment
            holder.delete_btn.setVisibility(View.VISIBLE);
            holder.delete_btn.setOnClickListener(v -> deleteComment(comment));
        } else {
            // if the current logged user is not the one who wrote the comment, allow him to like the comment

            holder.like_btn.setOnClickListener(v -> {
                if (holder.like_btn.getTag().equals("like")) {
                    // the current user didn't like the comment already
                    FirebaseDatabase.getInstance().getReference("Likes").child(comment.getId()).child(currentUser.getUid())
                            .setValue(true);
                    comment.addLike();

                    // gives the author of the comment one point
                    giveXPoints(comment, 1);
                }
                else {
                    // the current user already liked the comment
                    FirebaseDatabase.getInstance().getReference("Likes").child(comment.getId()).child(currentUser.getUid())
                            .removeValue();
                    comment.removeLike();

                    // removes from author of the comment one point
                    giveXPoints(comment, -1);
                }
            });
        }
    }

    private void deleteComment(Comment comment) {
        // removes the comment, likes and notifications from the realtime database
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        likeRef.child(comment.getId()).removeValue();
        commentsRef.child(comment.getForumId()).child(comment.getId()).removeValue();

        // removes from the author all the points he gained from that comment
        removeCredentialsFromUser(comment);

        // removes related notifications
        removeNotifications(comment);
    }

    private void removeNotifications(Comment comment) {
        DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("Forums");
        Query query = forumRef.child(comment.getForumId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // gets the forum of the comment
                Forum forum = snapshot.getValue(Forum.class);
                // removes the notifications related to the comment
                DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");
                notificationsRef.child(forum.getAuthorUid()).child(comment.getId()).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void displayUserData(ViewHolder holder, Comment comment) {
        // displays the user data on the comment view
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = reference.child(comment.getAuthorId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Uri uriImage = Uri.parse(user.getUri());
                Glide.with(context).load(uriImage).placeholder(R.drawable.default_pfp).into(holder.profile_picture);
                holder.username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void isLiked(Comment comment, ViewHolder holder) {
        // displaying the like button according to if the current user already liked the comment or not

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        likeRef.child(comment.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assert currentUser != null;
                if (snapshot.child(currentUser.getUid()).exists()) {
                    holder.like_btn.setImageResource(R.drawable.like_active);
                    holder.like_btn.setTag("liked");
                }
                else {
                    holder.like_btn.setImageResource(R.drawable.like);
                    holder.like_btn.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showLikeCount(Comment comment, ViewHolder holder) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        likeRef.child(comment.getId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.like_count.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void giveXPoints(Comment comment, int x) {
        // gives one point to the author of the comment
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = usersRef.child(comment.getAuthorId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User writer_of_comment = snapshot.getValue(User.class);
                assert writer_of_comment != null;
                writer_of_comment.addPoint(x);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removeCredentialsFromUser(Comment comment) {
        // removes from user 1 questions answered and all the points the comment has given him
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = usersRef.child(comment.getAuthorId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User writer_of_comment = snapshot.getValue(User.class);
                assert writer_of_comment != null;
                writer_of_comment.removePoint(comment.getLikes());
                writer_of_comment.removeAnswers();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, comment_content, like_count;
        public ImageView profile_picture;
        public ImageButton like_btn, delete_btn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            comment_content = itemView.findViewById(R.id.comment);
            profile_picture = itemView.findViewById(R.id.profile_picture);
            like_btn = itemView.findViewById(R.id.like_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            like_count = itemView.findViewById(R.id.like_counter);
        }
    }
}
