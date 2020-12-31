package com.example.thesis.Fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.DefaultTaskExecutor;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.Backend.DatabaseInformationController;
import com.example.thesis.DatabaseModels.Message;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.ChatRecyclerViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SingleChatFragment extends Fragment {

    private ArrayList<Message> messagesList = new ArrayList<>();
    private DatabaseInformationController dbController = new DatabaseInformationController();

    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter adapter;

    private EditText messageInput;
    private MaterialButton sendBtn;

    DatabaseReference chatRoomRef;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatRoomRef = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child("1111")
                .child(getString(R.string.messages_collection));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_chat, container, false);

        recyclerView = view.findViewById(R.id.chatMessagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setHasFixedSize(true);

        Log.d("SingleCh", "Created");

        messageInput = (EditText) view.findViewById(R.id.messageInput);

        sendBtn = (MaterialButton) view.findViewById(R.id.sendMessageBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = MainActivity.userObj;
                Message message = new Message(user, messageInput.getText().toString(), String.valueOf(getTimestamp()));
                sendMessage(message);
            }
        });

        setMessagesListener();
        return view;
    }

    private void setMessagesListener() {
        chatRoomRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messagesList.add(message);
                adapter = new ChatRecyclerViewAdapter(getContext(), messagesList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(Message message) {
        chatRoomRef.push().setValue(message);
    }

    private Long getTimestamp() {
        return System.currentTimeMillis()/1000;
    }
}
