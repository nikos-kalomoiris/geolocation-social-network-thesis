package com.example.thesis.Fragments;

import android.net.Uri;
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

import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.FriendsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference friendListRef;
    private ChildEventListener friendsListener;

    public FriendListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<User> friendList =  new ArrayList<>();
        Log.d("Debug", "FriendsList - onCreateView");

        FriendsRecyclerViewAdapter listAdapter = new FriendsRecyclerViewAdapter(getContext(), friendList);
        recyclerView.setAdapter(listAdapter);

        createFriendsListener(friendList, listAdapter);
        setFriendListListener();

        return view;
    }

    private void setFriendListListener() {
        friendListRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_collection))
                .child(FirebaseAuth.getInstance().getUid())
                .child(getString(R.string.friends_list_collection));

        friendListRef.addChildEventListener(friendsListener);
    }

    private void createFriendsListener(ArrayList<User> friendList, FriendsRecyclerViewAdapter listAdapter) {
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
                            listAdapter.updateFriendList(friendList);

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
                //Not used
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
                listAdapter.updateFriendList(friendList);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Not used
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
        Log.d("Debug", "FriendList - onDestroy");
        friendListRef.removeEventListener(friendsListener);
    }
}
