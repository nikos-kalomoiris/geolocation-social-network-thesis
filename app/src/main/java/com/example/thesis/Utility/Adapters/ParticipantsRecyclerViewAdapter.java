package com.example.thesis.Utility.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.R;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<User> selectedUsers = new ArrayList<>();

    public  ParticipantsRecyclerViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList.addAll(userList);
    }

    @NonNull
    @Override
    public ParticipantsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.participants_list_component, parent, false);
        return new ParticipantsRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ParticipantsRecyclerViewAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(userList.get(position).getuIconUrl())
                //.apply(new RequestOptions().override(200, 200))
                .into(holder.profileImage);
        holder.name.setText(userList.get(position).getuDisplayName());
        holder.email.setText(userList.get(position).getuEmail());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()) {
                    selectedUsers.add(userList.get(position));
                }
                else {
                    selectedUsers.remove(userList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView name, email;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageParticipant);
            name = itemView.findViewById(R.id.nameTextParticipant);
            email = itemView.findViewById(R.id.emailTextParticipant);
            checkBox = itemView.findViewById(R.id.checkParticipant);
        }
    }

    public ArrayList<User> getSelectedUsers() {
        return selectedUsers;
    }
}
