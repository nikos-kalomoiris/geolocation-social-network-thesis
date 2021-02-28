package com.example.thesis.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.AddEventActivity;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.ParticipantsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
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

public class AddParticipantsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ParticipantsRecyclerViewAdapter listAdapter;
    private ArrayList<User> selectedUsers;

    private DatabaseReference friendListRef;
    private ChildEventListener friendsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        recyclerView = view.findViewById(R.id.participantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MaterialButton getPartDataBtn = view.findViewById(R.id.getParticipantsData);
        getPartDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParticipants();
            }
        });

        ArrayList<User> friendList =  new ArrayList<>();
        //FriendListViewModel model = new ViewModelProvider(MapFragment.mapFragmentStore).get(FriendListViewModel.class);

        listAdapter = new ParticipantsRecyclerViewAdapter(getContext(), friendList);
        recyclerView.setAdapter(listAdapter);

        createFriendsListener(friendList, listAdapter);
        setFriendListListener();
        //setFriendListUpdateObserver(model, friendList);
        return view;
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

                for (User user : friendList) {
                    if (user.getuId().equals(dataSnapshot.getValue(String.class))) {
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

//    private void setFriendListUpdateObserver(FriendListViewModel model, ArrayList<User> friendList) {
//        model.getMarkers().observe(getViewLifecycleOwner(), list -> {
//
//            friendList.clear();
//            for(ClusterMarker marker: list) {
//
//                friendList.add(marker.getUser());
//            }
//
//            listAdapter.updateParticipantsList(friendList);
//        });
//    }

    private void getParticipants () {
        if(listAdapter != null) {
            selectedUsers = listAdapter.getSelectedUsers();
            AddEventActivity eventActivity = new AddEventActivity();
            Bundle bundle = new Bundle();
            bundle.putSerializable("participantsList", selectedUsers);
            AddEventDetailsFragment eventDetailsFragment = (AddEventDetailsFragment) eventActivity.getEventFragmentManager().findFragmentByTag("EventDetails");
            eventDetailsFragment.setArguments(bundle);
            eventActivity.getEventFragmentManager().popBackStack();
            eventActivity.getEventFragmentManager().beginTransaction().replace(R.id.fragmentContainer, eventDetailsFragment).commit();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        friendListRef.removeEventListener(friendsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        friendListRef.removeEventListener(friendsListener);
    }
}
