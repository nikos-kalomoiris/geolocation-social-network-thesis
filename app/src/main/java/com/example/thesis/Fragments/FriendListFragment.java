package com.example.thesis.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thesis.DatabaseModels.User;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.FriendsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.ViewModels.FriendListViewModel;

import java.util.ArrayList;


public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;

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
        FriendListViewModel model = new ViewModelProvider(MapFragment.mapFragmentStore).get(FriendListViewModel.class);

        setFriendListUpdateObserver(model, friendList);

        return view;
    }

    private void setFriendListUpdateObserver(FriendListViewModel model, ArrayList<User> friendList) {
        model.getMarkers().observe(getViewLifecycleOwner(), list -> {

            friendList.clear();
            for(ClusterMarker marker: list) {

                friendList.add(marker.getUser());
            }
            FriendsRecyclerViewAdapter listAdapter = new FriendsRecyclerViewAdapter(getContext(), friendList);
            recyclerView.setAdapter(listAdapter);
        });
    }
}
