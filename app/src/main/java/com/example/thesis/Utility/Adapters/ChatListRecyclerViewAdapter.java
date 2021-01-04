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
import com.example.thesis.DatabaseModels.ChatRoom;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
    private OnChatRoomClickListener mOnChatRoomClickListener;

    public  ChatListRecyclerViewAdapter(Context context, List<ChatRoom> chatRoomList, OnChatRoomClickListener mOnChatRoomClickListener) {
        this.context = context;
        this.chatRoomList.addAll(chatRoomList);
        this.mOnChatRoomClickListener = mOnChatRoomClickListener;
    }

    @NonNull
    @Override
    public ChatListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_list_component, parent, false);
        return new ChatListRecyclerViewAdapter.ViewHolder(view, mOnChatRoomClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListRecyclerViewAdapter.ViewHolder holder, int position) {

        if(chatRoomList.get(position).getChatRoomUsers().size() > 2) {
            Glide.with(context)
                    .load(R.drawable.ic_friend_list)
                    .into(holder.chatRoomImage);
        }
        else {
            for(User user: chatRoomList.get(position).getChatRoomUsers()) {
                if(!user.getuId().equals(FirebaseAuth.getInstance().getUid())) {
                    Glide.with(context)
                            .load(user.getuIconUrl())
                            .into(holder.chatRoomImage);
                }
            }
        }
        holder.chatRoomName.setText(chatRoomList.get(position).getChatRoomName());
        if(chatRoomList.get(position).getLastUserMessageId().equals(FirebaseAuth.getInstance().getUid())) {
            holder.lastMessage.setText("Me: " + chatRoomList.get(position).getLastMessage());
        }
        else {
            holder.lastMessage.setText(chatRoomList.get(position).getLastMessageUserName() +
                    ": " + chatRoomList.get(position).getLastMessage());
        }
    }

    public void updateChatRoomList(ChatRoom newChatRoom) {
        chatRoomList.add(newChatRoom);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.chatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnChatRoomClickListener chatRoomClickListener;

        ImageView chatRoomImage;
        TextView chatRoomName, lastMessage;

        public ViewHolder(@NonNull View itemView, OnChatRoomClickListener chatRoomClickListener) {
            super(itemView);

            chatRoomImage = itemView.findViewById(R.id.chatRoomImage);
            chatRoomName = itemView.findViewById(R.id.chatRoomName);
            lastMessage = itemView.findViewById(R.id.chatRoomLastMessage);
            this.chatRoomClickListener = chatRoomClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            chatRoomClickListener.onChatRoomClick(getAdapterPosition());
        }
    }

    public void updateLastMessage(String lastMessage, String chatRoomId, String lastMessageSenderName, String lastMessageSenderId) {
        int index = 0;
        for(ChatRoom chatRoom: chatRoomList) {
            if(chatRoom.getChatRoomId().equals(chatRoomId)) {
                chatRoomList.get(index).setLastMessage(lastMessage);
                chatRoomList.get(index).setLastUserMessageId(lastMessageSenderId);
                chatRoomList.get(index).setLastMessageUserName(lastMessageSenderName);
            }
            index++;
        }
        this.notifyDataSetChanged();
    }

    public interface OnChatRoomClickListener {
        void onChatRoomClick(int position);
    }
}
