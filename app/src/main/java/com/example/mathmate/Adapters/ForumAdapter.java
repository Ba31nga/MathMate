package com.example.mathmate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class ForumAdapter extends ArrayAdapter<Forum> {

    private LayoutInflater mInflater;
    private List<Forum> mForums;
    private int layoutResource;
    private Context mContext;


    public ForumAdapter(@NonNull Context context, int resource, @NonNull List<Forum> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mForums = objects;
    }

    private static class ViewHolder {
        TextView title, subject;
        ImageView profilePicture;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.title = convertView.findViewById(R.id.title_et);
            holder.subject = convertView.findViewById(R.id.subject_et);
            holder.profilePicture = convertView.findViewById(R.id.profile_picture);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(mForums.get(position).getTitle());
        holder.subject.setText(mForums.get(position).getSubject());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = reference.orderByValue().equalTo(mForums.get(position).getAuthorUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // putting the profile picture of the author
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(mContext).load(Uri.parse(user.getUri())).placeholder(R.drawable.default_pfp).into(holder.profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return convertView;
    }
}
