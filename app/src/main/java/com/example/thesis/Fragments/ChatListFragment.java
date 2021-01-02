package com.example.thesis.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.DatabaseModels.ChatRoom;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.ChatListRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<User> chatRoomUsers = new ArrayList<>();
    private ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    private FloatingActionButton createChatButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView = view.findViewById(R.id.chatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        chatRoomUsers.add(MainActivity.userObj);

        ChatRoom chatRoom = new ChatRoom("1111", "Chat Room 1", chatRoomUsers,"Hello there m8", FirebaseAuth.getInstance().getUid());
        chatRooms.add(chatRoom);

        ChatListRecyclerViewAdapter adapter = new ChatListRecyclerViewAdapter(getContext(), chatRooms);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
