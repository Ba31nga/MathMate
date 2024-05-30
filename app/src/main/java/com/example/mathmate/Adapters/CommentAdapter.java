package com.example.mathmate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mathmate.Models.Comment;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.Like;
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



        // Displaying user data e.g username and profile picture
        displayUserData(holder, comment);


        // Handles the likes on the comment
        likesHandler(holder, comment);


        // handling like count
        holder.like_count.setText(String.valueOf(comment.getLikes()));

        // handling delete button
        deleteCommentHandler(holder, comment);
    }


    private void deleteCommentHandler(ViewHolder holder, Comment comment) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser.getUid().equals(comment.getAuthorId())) {
            // Checks if the current user is the writer of the comment
            holder.delete_btn.setVisibility(View.VISIBLE);
            holder.delete_btn.setOnClickListener(v -> {

                removeCommentFromDatabase(comment);
                removeAnswerPointsFromUser(comment);
                removeLikesFromDatabase(comment);
                removeNotificationsRelatedToTheComment(comment);
            });
        }
    }

    private void removeNotificationsRelatedToTheComment(Comment comment) {
        // deletes all notifications regarding to that comment
        DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("Forums");
        Query forumQuery = forumRef.orderByKey().equalTo(comment.getForumId());
        forumQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);

                    DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
                    Query notificationsQuery = notificationRef.child(forum.getAuthorUid()).orderByKey().equalTo(comment.getId());
                    notificationsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                // removes the notification related to that comment
                                dataSnapshot1.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void removeLikesFromDatabase(Comment comment) {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes");
        Query likesQuery = likesRef.orderByKey().equalTo(comment.getId());
        likesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            // removes the likes that are connected to the deleted comment
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removeCommentFromDatabase(Comment comment) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        Query commentsQuery = commentsRef.child(comment.getForumId()).orderByKey().equalTo(comment.getId());
        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            // Deletes the comment
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void removeAnswerPointsFromUser(Comment comment) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query userQuery = userReference.orderByKey().equalTo(comment.getAuthorId());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    user.RemoveAnswers();
                    user.removePoint(comment.getLikes());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }





    private void likesHandler(ViewHolder holder, Comment comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = reference.child(comment.getId()).orderByKey().equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if the current user liked the comment
                boolean isLiked = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // the current user already liked the comment

                    isLiked = true;
                    // the user already liked the comment
                    holder.like_btn.setImageResource(R.drawable.like_active);
                    holder.like_btn.setOnClickListener(v -> {
                        // deletes the like from the database
                        dataSnapshot.getRef().removeValue();
                        // removes 1 like from the comment like attribute
                        comment.removeLike();
                        // removes the points related to that like from the user
                        removeOnePointFromUser(comment);
                        // changes the like icon
                        holder.like_btn.setImageResource(R.drawable.like);
                        CommentAdapter.this.notifyDataSetChanged();
                    });
                }
                if (!isLiked) {
                    // the current user haven't liked the comment yet
                    // adds like to the comment
                    holder.like_btn.setOnClickListener(v -> {
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comment.getAuthorId())) {
                            // making sure the user can't like himself
                            Like like = new Like(currentUser.getUid());

                            // adds the like reference to the realtime database
                            FirebaseDatabase.getInstance().getReference("Likes").child(comment.getId()).child(currentUser.getUid()).setValue(like).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    // gives a point to the user created the command
                                    giveUserPoint(comment);
                                    // adds one like to the comment like attribute
                                    comment.addLike();
                                    CommentAdapter.this.notifyDataSetChanged();

                                    // TODO : add notification to the user
                                }
                            });
                        }
                        else {
                            Toast.makeText(context, "You can't like yourself -_-", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void giveUserPoint(Comment comment) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query userQuery = userRef.orderByKey().equalTo(comment.getAuthorId());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                for (DataSnapshot dataSnapshot : snapshot1.getChildren())
                    dataSnapshot.getValue(User.class).addPoint();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removeOnePointFromUser(Comment comment) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query userQuery = userRef.orderByKey().equalTo(comment.getAuthorId());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    dataSnapshot.getValue(User.class).removePoint();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }




    private void displayUserData(ViewHolder holder, Comment comment) {
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
