package com.example.mathmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathmate.CommentsActivity;
import com.example.mathmate.Models.Forum;
import com.example.mathmate.Models.Notification;
import com.example.mathmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final List<Notification> notifications;
    private final Context context;

    public NotificationsAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = notifications.get(position);
        holder.message.setText(notification.getText());

        // Get the title of the forum :
        DatabaseReference forumRef = FirebaseDatabase.getInstance().getReference("Forums");
        Query forumQuery = forumRef.orderByKey().equalTo(notification.getForumId());
        forumQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    holder.title.setText((dataSnapshot.getValue(Forum.class)).getTitle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("forumid", notification.getForumId());
            context.startActivity(intent);

            removeNotificationFromDatabase(notification);
        });
    }

    private void removeNotificationFromDatabase(Notification notification) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
        Query notificationQuery = notificationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByKey().equalTo(notification.getId());
        notificationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);

        }
    }
}
