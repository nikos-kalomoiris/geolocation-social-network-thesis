package com.example.thesis.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.CreateChatRoomActivity;
import com.example.thesis.DatabaseModels.ChatRoom;
import com.example.thesis.DatabaseModels.Message;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.SingleChatActivity;
import com.example.thesis.Utility.Adapters.ChatListRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChatListFragment extends Fragment implements ChatListRecyclerViewAdapter.OnChatRoomClickListener {

    private final int LAUNCH_CREATE_CHAT_ACTIVITY = 1;

    private static ViewPager2 mainPager;

    private RecyclerView recyclerView;
    public static ChatListRecyclerViewAdapter adapter;
    private ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    private ExtendedFloatingActionButton createChatButton;

    boolean firstTime = true;

    private DatabaseReference chatRoomsRef;
    private Query lastMessageRef;
    private ChildEventListener chatRoomsListener, lastMessageListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPager = getActivity().findViewById(R.id.mainPager); //Get mainViewPager to disable swipe in mapFragment
        createChatRoomsListener();
        setChatRoomsListener();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        createChatButton = (ExtendedFloatingActionButton) view.findViewById(R.id.createChatFab);

        recyclerView = view.findViewById(R.id.chatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ChatListRecyclerViewAdapter(getContext(), chatRooms, this);
        recyclerView.setAdapter(adapter);

        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateChatRoomActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, LAUNCH_CREATE_CHAT_ACTIVITY);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LAUNCH_CREATE_CHAT_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK) {

                DatabaseReference chatRoomsRef = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.chat_rooms_collection));

                ArrayList<User> chatRoomUsers = (ArrayList<User>) data.getSerializableExtra("SelectedUsers");
                String chatRoomName = "";

                if(chatRoomUsers.size() > 1) {
                    for (User user: chatRoomUsers) {
                        chatRoomName += user.getuDisplayName() + ", ";
                    }
                }
                else {
                    chatRoomName = chatRoomUsers.get(0).getuDisplayName();
                }

                chatRoomUsers.add(MainActivity.userObj);
                final String chatName = chatRoomName;

                if(chatRoomUsers.size() <= 2) {
                    chatRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Boolean keyFound = false;
                            Collections.sort(chatRoomUsers);
                            String key = chatRoomUsers.get(0).getuId().substring(0, 5) + chatRoomUsers.get(1).getuId().substring(0, 5);
                            for (DataSnapshot chatRoomSnapshot: dataSnapshot.getChildren()) {
                                if(chatRoomSnapshot.getKey().equals(key)) {
                                    keyFound = true;
                                    break;
                                }
                            }

                            if(keyFound) {
                                int index = 0;
                                for (ChatRoom chatRoom: chatRooms) {
                                    if(chatRoom.getChatRoomId().equals(key)) {
                                        break;
                                    }
                                    index++;
                                }
                                onChatRoomClick(index);
                            }
                            else {
                                HashMap<String, Object> chatRoom = new HashMap<>();
                                chatRoom.put("users", chatRoomUsers);
                                chatRoom.put("chatRoomName", chatName);
                                chatRoom.put(getString(R.string.messages_collection), "");
                                chatRoom.put("type", "Classic");

                                chatRoomsRef.child(key).setValue(chatRoom);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    HashMap<String, Object> chatRoom = new HashMap<>();
                    chatRoom.put("users", chatRoomUsers);
                    chatRoom.put("chatRoomName", chatRoomName);
                    chatRoom.put(getString(R.string.messages_collection), "");
                    chatRoom.put("type", "Classic");

                    chatRoomsRef.push().setValue(chatRoom);
                }

            }
        }
    }

    public class userIdSorter implements Comparator<User> {

        @Override
        public int compare(User user1, User user2) {
            return user1.getuId().compareTo(user2.getuId());
        }
    }

    private void createChatRoomsListener() {
        chatRoomsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Boolean userInChat = false;

                ArrayList<User> chatRoomUsers = new ArrayList<>();
                for(DataSnapshot userSnapshot: dataSnapshot.child("users").getChildren()) {
                    chatRoomUsers.add(userSnapshot.getValue(User.class));
                    if(userSnapshot.getValue(User.class).getuId().equals(FirebaseAuth.getInstance().getUid())) {
                        userInChat = true;
                    }
                }

                if(userInChat) {

                    final String chatRoomId = dataSnapshot.getKey();
                    boolean chatRoomExists = false;
                    String chatRoomName = dataSnapshot.child("chatRoomName").getValue(String.class);
                    String chatRoomType = dataSnapshot.child("type").getValue(String.class);

                    for(ChatRoom chatRoom: chatRooms) {
                        if(chatRoomId.equals(chatRoom.getChatRoomId())) {
                            chatRoomExists = true;
                            break;
                        }
                    }

                    if(!chatRoomExists) {
                        if(chatRoomUsers.size() <= 2 ) {
                            for(User user: chatRoomUsers) {
                                if(!user.getuId().equals(FirebaseAuth.getInstance().getUid())) {
                                    chatRoomName = user.getuDisplayName();
                                }
                            }
                        }

                        ChatRoom chatRoom = new ChatRoom(chatRoomId, chatRoomName, chatRoomUsers, "", "", "", chatRoomType);
                        chatRooms.add(chatRoom);
                        adapter.updateChatRoomList(chatRoom);
                    }

                    lastMessageRef = dataSnapshot.child(getString(R.string.messages_collection))
                            .getRef()
                            .limitToLast(1);

                    lastMessageListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Message message = dataSnapshot.getValue(Message.class);

                            String lastMessage = message.getMessage();
                            String lastMessageSenderName = message.getSender().getuDisplayName();
                            String lastMessageSenderId = message.getSender().getuId();

                            adapter.updateLastMessage(lastMessage, chatRoomId, lastMessageSenderName, lastMessageSenderId);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            //Not Used
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            //Not Used
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            //Not Used
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Error", databaseError.getMessage());
                        }
                    };

                    lastMessageRef.addChildEventListener(lastMessageListener);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Not Used
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Not Used
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Not Used
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        };
    }

    private void setChatRoomsListener() {
        chatRoomsRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.chat_rooms_collection));
        chatRoomsRef.addChildEventListener(chatRoomsListener);
        firstTime = false;
    }

    @Override
    public void onChatRoomClick(int position) {
        Intent intent = new Intent(getContext(), SingleChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ChatRoom", chatRooms.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatRoomsRef.removeEventListener(chatRoomsListener);
        if(lastMessageListener != null) {
            lastMessageRef.removeEventListener(lastMessageListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        chatRoomsRef.removeEventListener(chatRoomsListener);
        if(lastMessageListener != null) {
            lastMessageRef.removeEventListener(lastMessageListener);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mainPager = getActivity().findViewById(R.id.mainPager);
        mainPager.setUserInputEnabled(true);
        Log.d("Debug", "ChatFragment - onResume");
        if(!firstTime) {
            chatRoomsRef.addChildEventListener(chatRoomsListener);
        }
    }
}
