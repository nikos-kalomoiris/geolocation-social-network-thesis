package com.example.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thesis.Backend.DatabaseInformationController;
import com.example.thesis.DatabaseModels.ChatRoom;
import com.example.thesis.DatabaseModels.Message;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.Utility.Adapters.ChatRecyclerViewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SingleChatActivity extends FragmentActivity {

    private ArrayList<Message> messagesList = new ArrayList<>();
    private DatabaseInformationController dbController = new DatabaseInformationController();

    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter adapter;
    private static Boolean hasEnded = false;

    private EditText messageInput;
    private MaterialButton sendBtn;
    private TextView singleChatName;
    private ImageView singleChatImage;

    DatabaseReference chatRoomRef;
    Query chatRoomSingleListener;
    Query fetchFirstMessages;

    ChatRoom chatRoom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        Intent intent = getIntent();
        chatRoom = (ChatRoom) intent.getSerializableExtra("ChatRoom");

        initDbRefs();
        initViewWidgets();
        getFirstMessages();
    }

    private void initDbRefs() {
        chatRoomRef = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child(chatRoom.getChatRoomId())
                .child(getString(R.string.messages_collection));

        fetchFirstMessages = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child(chatRoom.getChatRoomId())
                .child(getString(R.string.messages_collection))
                .orderByChild("timestamp")
                .limitToLast(20);

        chatRoomSingleListener = dbController.getDatabase().getReference()
                .child(getString(R.string.chat_rooms_collection))
                .child(chatRoom.getChatRoomId())
                .child(getString(R.string.messages_collection))
                .limitToLast(1);
    }

    private void initViewWidgets() {
        singleChatName = (TextView) findViewById(R.id.chatName);
        singleChatImage = (ImageView) findViewById(R.id.chatImage);

        singleChatName.setText(chatRoom.getChatRoomName());

        recyclerView = findViewById(R.id.chatMessagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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

        messageInput = (EditText) findViewById(R.id.messageInput);

        sendBtn = (MaterialButton) findViewById(R.id.sendMessageBtn);
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
                adapter = new ChatRecyclerViewAdapter(getApplication(), messagesList);
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
                adapter = new ChatRecyclerViewAdapter(getApplication(), messagesList);
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
                .child(chatRoom.getChatRoomId())
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
