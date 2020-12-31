package com.example.thesis.Utility.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestRecyclerViewAdapter extends RecyclerView.Adapter<FriendRequestRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> userRequestList = new ArrayList<>();
    //private Resources resources = MainActivity.getRes();
    //Database refs
    private Context resources;

    public FriendRequestRecyclerViewAdapter(Context context, List<User> userRequestList) {
        this.context = context;
        this.userRequestList.addAll(userRequestList);
    }

    @NonNull
    @Override
    public FriendRequestRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_request_component, parent, false);
        return new FriendRequestRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FriendRequestRecyclerViewAdapter.ViewHolder holder, int position) {
        resources = holder.itemView.getContext();
        Glide.with(context)
                .load(userRequestList.get(position).getuIconUrl())
                //.apply(new RequestOptions().override(200, 200))
                .into(holder.profileImage);
        holder.name.setText(userRequestList.get(position).getuDisplayName());
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(userRequestList.get(position));
                removeAt(position);
            }
        });

        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineFriend(userRequestList.get(position));
                removeAt(position);
            }
        });
    }

    private void addFriend(User user) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference request = database.child(resources.getString(R.string.users_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .child(resources.getString(R.string.requests_list_collection))
                .child(user.getuId());

        database.child(resources.getString(R.string.users_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .child(resources.getString(R.string.friends_list_collection))
                .child(user.getuId())
                .setValue(user.getuId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Request", "Friend Added");
                    }
                });

        database.child(resources.getString(R.string.users_collection))
                .child(user.getuId())
                .child(resources.getString(R.string.friends_list_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(FirebaseAuth.getInstance().getUid());

        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void declineFriend(User user) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference request = database.child(resources.getString(R.string.users_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .child(resources.getString(R.string.requests_list_collection))
                .child(user.getuId());

        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void removeAt(int position) {
        userRequestList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userRequestList.size());
    }

    @Override
    public int getItemCount() {
        return this.userRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView name;
        Button acceptButton, declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageParticipant);
            name = itemView.findViewById(R.id.nameTextParticipant);
            acceptButton = itemView.findViewById(R.id.acceptRequest);
            declineButton = itemView.findViewById(R.id.declineRequest);
        }
    }
}
