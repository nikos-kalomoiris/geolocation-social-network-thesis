package com.example.thesis.Utility.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thesis.DatabaseModels.Message;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    private final int SENT_MESSAGE = 1;
    private final int RECEIVED_MESSAGE = 2;

    private Context context;
    private ArrayList<Message> messageList = new ArrayList<>();

    public  ChatRecyclerViewAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.messageList.addAll(messageList);
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.size() > 0) {
            if(messageList.get(position).getSender().getuId().equals(FirebaseAuth.getInstance().getUid())) {
                return SENT_MESSAGE;
            }
            else {
                return RECEIVED_MESSAGE;
            }
        }
        return 0;
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
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == SENT_MESSAGE) {
            ((SentMessageHolder) holder).message.setText(messageList.get(position).getMessage());
            ((SentMessageHolder) holder).timestamp.setText(messageList.get(position).getTimestamp());
        }
        else if(holder.getItemViewType() == RECEIVED_MESSAGE) {
            ((ReceivedMessageHolder) holder).message.setText(messageList.get(position).getMessage());
            ((ReceivedMessageHolder) holder).timestamp.setText(messageList.get(position).getTimestamp());
            ((ReceivedMessageHolder) holder).name.setText(messageList.get(position).getSender().getuDisplayName());
            Glide.with(context)
                    .load(messageList.get(position).getSender().getuIconUrl())
                    //.apply(new RequestOptions().override(200, 200))
                    .into(((ReceivedMessageHolder) holder).senderImage);

        }
    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }

    public void updateAdapter(List<Message> newList) {
        messageList.clear();
        messageList.addAll(newList);
        this.notifyDataSetChanged();
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder{

        TextView timestamp, message;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.messageBody);
            timestamp = itemView.findViewById(R.id.messageTimestamp);
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder{

        ImageView senderImage;
        TextView name, message, timestamp;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            senderImage = itemView.findViewById(R.id.senderImage);
            name = itemView.findViewById(R.id.senderName);
            message = itemView.findViewById(R.id.senderMessageBody);
            timestamp = itemView.findViewById(R.id.senderTimestamp);

        }
    }
}
