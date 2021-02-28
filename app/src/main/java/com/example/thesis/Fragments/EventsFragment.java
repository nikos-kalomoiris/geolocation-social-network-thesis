package com.example.thesis.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.ChatListRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.EventsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.FriendsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

    private static ViewPager2 mainPager;
    private RecyclerView recyclerView;
    private EventsRecyclerViewAdapter adapter;
    private ArrayList<ClusterMarker> eventsList = new ArrayList<>();
    private DatabaseReference eventsRef;
    private ChildEventListener eventsListener;
    private FirebaseUser user;
    boolean firstTime = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPager = getActivity().findViewById(R.id.mainPager); //Get mainViewPager to disable swipe in mapFragment
        user = FirebaseAuth.getInstance().getCurrentUser();
        createEventsListener();
        firstTime = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = view.findViewById(R.id.eventsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new EventsRecyclerViewAdapter(getContext(), eventsList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug", "ProfileFragment - onResume");
        mainPager = getActivity().findViewById(R.id.mainPager);
        mainPager.setUserInputEnabled(true);
        if(!firstTime) {
            setEventsListener();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        eventsList.clear();
        if(eventsListener != null) {
            eventsRef.removeEventListener(eventsListener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventsList.clear();
//        if(eventsListener != null) {
//            eventsRef.removeEventListener(eventsListener);
//        }
    }

    private void setEventsListener() {
        eventsList.clear();
        eventsRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.events_collection));

        eventsRef.addChildEventListener(eventsListener);
    }

    private void createEventsListener() {
        eventsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Event currEvent = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();


                for(String participantId: currEvent.getParticipants()) {

                    if(currEvent.getAuthor().getuId().equals(user.getUid())) {
                        ClusterMarker event = new ClusterMarker("Event", currEvent, key);
                        eventsList.add(event);
                        break;
                    }

                    if(participantId.equals(user.getUid())) {
                        ClusterMarker event = new ClusterMarker("Event", currEvent, key);
                        eventsList.add(event);
                    }
                }
                adapter.updateEventsList(eventsList);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Event currEvent = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
