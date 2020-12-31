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
import com.example.thesis.DatabaseModels.Message;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    private final int SENT_MESSAGE = 1;
    private final int RECEIVED_MESSAGE = 2;

    private Context context;
    private ArrayList<Message> messageList;

    public  ChatRecyclerViewAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {

        if(messageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENT_MESSAGE;
        }
        else {
            return RECEIVED_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(viewType == SENT_MESSAGE) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.message_container_right, parent, false);
            return new SentMessageHolder(view);
        }
        else if(viewType == RECEIVED_MESSAGE) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.message_container_left, parent, false);
            return new receivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView name, email;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageList);
            name = itemView.findViewById(R.id.nameTextList);
            email = itemView.findViewById(R.id.emailTextList);
        }
    }

    public class receivedMessageHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView name, email;

        public receivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageList);
            name = itemView.findViewById(R.id.nameTextList);
            email = itemView.findViewById(R.id.emailTextList);
        }
    }
}
