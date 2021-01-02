package com.example.thesis.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SingleChatFragment extends Fragment {

    private ArrayList<Message> messagesList = new ArrayList<>();
    private DatabaseInformationController dbController = new DatabaseInformationController();

    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter adapter;
    private static Boolean hasEnded = false;

    private EditText messageInput;
    private MaterialButton sendBtn;

    DatabaseReference chatRoomRef;
    Query chatRoomSingleListener;
    Query fetchFirstMessages;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatRoomRef = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child("1111")
                .child(getString(R.string.messages_collection));

        fetchFirstMessages = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child("1111")
                .child(getString(R.string.messages_collection))
                .orderByChild("timestamp")
                .limitToLast(20);

        chatRoomSingleListener = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child("1111")
                .child(getString(R.string.messages_collection))
                .limitToLast(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_chat, container, false);

        recyclerView = view.findViewById(R.id.chatMessagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1) && newState==RecyclerView.SCROLL_STATE_DRAGGING) {
                    fetchMessages();
                }
            }
        });

        Log.d("SingleCh", "Created");

        messageInput = (EditText) view.findViewById(R.id.messageInput);

        sendBtn = (MaterialButton) view.findViewById(R.id.sendMessageBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(messageInput.getText())) {
                    User user = MainActivity.userObj;
                    Message message = new Message(user, messageInput.getText().toString(), String.valueOf(getTimestamp()));
                    messageInput.setText("");
                    sendMessage(message);
                }
            }
        });

        getFirstMessages();
        return view;
    }

    private void getFirstMessages() {
        fetchFirstMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;
                for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    messagesList.add(message);
                    Log.d("MessageList", messagesList.get(index).getMessage());
                    index++;
                }
                adapter = new ChatRecyclerViewAdapter(getContext(), messagesList);
                recyclerView.setAdapter(adapter);
                if(messagesList.size() > 0) {
                    messagesList.remove(messagesList.size() - 1);
                }

                setMessagesListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setMessagesListener() {
        chatRoomSingleListener.addChildEventListener(new ChildEventListener() {
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

    private void fetchMessages() {

        Query fetchMessages = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child("1111")
                .child(getString(R.string.messages_collection))
                .orderByChild("timestamp")
                .endAt(messagesList.get(0).getTimestamp())
                .limitToLast(20);

        if(!hasEnded) {
            fetchMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ArrayList<Message> messageToAdd = new ArrayList<>();
                    for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        messageToAdd.add(0, message);
                    }
                    Collections.reverse(messageToAdd);
                    messageToAdd.remove(messageToAdd.size() - 1);
                    messagesList.addAll(0, messageToAdd);
                    adapter.updateAdapter(messagesList);

                    if(dataSnapshot.getChildrenCount() <= 19) {
                        hasEnded = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Error", databaseError.getMessage());
                }
            });
        }



    }

    private void sendMessage(Message message) {
        chatRoomRef.push().setValue(message);
    }

    private Long getTimestamp() {
        return System.currentTimeMillis()/1000;
    }
}
