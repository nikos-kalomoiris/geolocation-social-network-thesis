package com.example.thesis.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.AddEventActivity;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.ParticipantsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AddParticipantsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ParticipantsRecyclerViewAdapter listAdapter;
    private ArrayList<User> selectedUsers;

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
        FriendListViewModel model = new ViewModelProvider(MapFragment.mapFragmentStore).get(FriendListViewModel.class);

        listAdapter = new ParticipantsRecyclerViewAdapter(getContext(), friendList);
        recyclerView.setAdapter(listAdapter);

        setFriendListUpdateObserver(model, friendList);
        return view;
    }

    private void setFriendListUpdateObserver(FriendListViewModel model, ArrayList<User> friendList) {
        model.getMarkers().observe(getViewLifecycleOwner(), list -> {

            friendList.clear();
            for(ClusterMarker marker: list) {

                friendList.add(marker.getUser());
            }

            listAdapter.updateParticipantsList(friendList);
        });
    }

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
}
