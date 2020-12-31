package com.example.thesis.Fragments;

import android.content.Intent;
import android.os.Bundle;

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

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {

    private static final int LAUNCH_ADD_FRIEND_ACTIVITY = 1111;

    private RecyclerView recyclerView;
    private MaterialButton addFriendFab;

    public FriendRequestsFragment() {
        // Required empty public constructor
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
        FriendRequestsViewModel model = new ViewModelProvider(MapFragment.mapFragmentStore).get(FriendRequestsViewModel.class);

        setFriendRequestUpdateObserver(model, friendRequestList);

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

    private void setFriendRequestUpdateObserver(FriendRequestsViewModel model, ArrayList<User> friendRequestList) {
        model.getRequests().observe(getViewLifecycleOwner(), list -> {
            Log.d("Request List", "Fired From RequestFragment");
            friendRequestList.clear();
            for(User user: list) {
                friendRequestList.add(user);
            }
            FriendRequestRecyclerViewAdapter listAdapter = new FriendRequestRecyclerViewAdapter(getContext(), friendRequestList);
            recyclerView.setAdapter(listAdapter);
        });
    }
}


