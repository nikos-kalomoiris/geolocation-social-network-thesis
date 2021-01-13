package com.example.thesis.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.thesis.AddFriendActivity;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.FriendRequestRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.FriendsRecyclerViewAdapter;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.example.thesis.ViewModels.FriendRequestsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {

    private static final int LAUNCH_ADD_FRIEND_ACTIVITY = 1111;

    private RecyclerView recyclerView;
    private MaterialButton addFriendFab;

    ChildEventListener friendRequestListener;
    DatabaseReference requestList;

    public FriendRequestsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        recyclerView = view.findViewById(R.id.friendRequestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addFriendFab = (MaterialButton) view.findViewById(R.id.addFriendFab);
        addFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, LAUNCH_ADD_FRIEND_ACTIVITY);
            }
        });

        ArrayList<User> friendRequestList =  new ArrayList<>();

        FriendRequestRecyclerViewAdapter listAdapter = new FriendRequestRecyclerViewAdapter(getContext(), friendRequestList);
        recyclerView.setAdapter(listAdapter);

        createFriendsRequestListener(listAdapter, friendRequestList);
        setFriendRequestListener();


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LAUNCH_ADD_FRIEND_ACTIVITY) {
            String result = data.getStringExtra("result");
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private void setFriendRequestListener() {
        requestList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.requests_list_collection));

        requestList.addChildEventListener(friendRequestListener);
    }

    private void createFriendsRequestListener(FriendRequestRecyclerViewAdapter adapter, ArrayList<User> friendRequestList) {
        friendRequestListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DatabaseReference singleUser = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_collection))
                        .child(dataSnapshot.getValue(String.class));

                singleUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friendRequestList.add(dataSnapshot.getValue(User.class));
                        adapter.updateRequestsList(friendRequestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference singleUserDel = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_collection))
                        .child(dataSnapshot.getValue(String.class));

                singleUserDel.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Request List", "In Data Change");
//                        Log.d("Request List", "Removed: " + friendRequests.remove(user));
                        for(User user: friendRequestList) {
                            if(user.getuId().equals(dataSnapshot.getValue(User.class).getuId())) {
                                Log.d("Request List", "Removed: " + friendRequestList.remove(user));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestList.removeEventListener(friendRequestListener);
    }
}


