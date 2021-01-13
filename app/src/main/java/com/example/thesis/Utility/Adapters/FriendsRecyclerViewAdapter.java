package com.example.thesis.Utility.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> userList = new ArrayList<>();

    public  FriendsRecyclerViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList.addAll(userList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_list_component, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(userList.get(position).getuIconUrl())
                //.apply(new RequestOptions().override(200, 200))
                .into(holder.profileImage);
        holder.name.setText(userList.get(position).getuDisplayName());
        holder.email.setText(userList.get(position).getuEmail());
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public void updateFriendList(ArrayList<User> friendList) {
        userList.clear();
        userList.addAll(friendList);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView name, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageList);
            name = itemView.findViewById(R.id.nameTextList);
            email = itemView.findViewById(R.id.emailTextList);
        }
    }
}
