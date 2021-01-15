package com.example.thesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.Fragments.ChatListFragment;
import com.example.thesis.Fragments.MapFragment;
import com.example.thesis.Utility.Adapters.FriendsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.Utility.Adapters.ParticipantsRecyclerViewAdapter;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateChatRoomActivity extends AppCompatActivity {

    private MaterialButton createChatBtn;
    private RecyclerView recyclerView;

    private DatabaseReference friendListRef;
    private ChildEventListener friendsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);
        //Setting Toolbar
        LinearLayout toolBarTemplate = findViewById(R.id.mainToolbar);
        Toolbar toolbar = toolBarTemplate.findViewById(R.id.mainToolbarTemplate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<User> friendList = new ArrayList<>();

        initView(friendList);
    }

    private void initView(ArrayList<User> friendList) {
        createChatBtn = findViewById(R.id.createChatBtn);
        recyclerView = findViewById(R.id.chatParticipants);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ParticipantsRecyclerViewAdapter adapter = new ParticipantsRecyclerViewAdapter(this, friendList);
        recyclerView.setAdapter(adapter);

        createFriendsListener(friendList, adapter);
        setFriendListListener();

        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectedUsers", adapter.getSelectedUsers());
                returnIntent.putExtras(bundle);

                if(adapter.getSelectedUsers().size() > 0) {
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                else {
                    Toast.makeText(getApplication(), "Please select users to chat", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setFriendListListener() {
        friendListRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.friends_list_collection));

        friendListRef.addChildEventListener(friendsListener);
    }

    private void createFriendsListener(ArrayList<User> friendList, ParticipantsRecyclerViewAdapter listAdapter) {
        friendsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                DatabaseReference singleUserLocation = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_location_collection))
                        .child(dataSnapshot.getValue(String.class));

                singleUserLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {
                            UserLocation userLocation = dataSnapshot.getValue(UserLocation.class);
                            friendList.add(userLocation.getUser());
                            listAdapter.updateParticipantsList(friendList);

                        } catch (NullPointerException ex) {
                            Log.e("Error", ex.getMessage());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Debug", "Data Deleted");

                for(User user: friendList) {
                    if(user.getuId().equals(dataSnapshot.getValue(String.class))) {
                        friendList.remove(user);
                        break;
                    }
                }
                listAdapter.updateParticipantsList(friendList);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        };
    }
}
