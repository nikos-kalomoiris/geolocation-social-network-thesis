package com.example.thesis.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.example.thesis.SingleChatActivity;

public class ChatFragment extends Fragment {

    private static FragmentManager fragmentManager;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initFragment();
        return view;
    }

    private void initFragment() {
        MainActivity mainActivity = MainActivity.instance;
        //SingleChatActivity chatListFragment = new SingleChatActivity();
        ChatListFragment chatListFragment = new ChatListFragment();
        fragmentManager = mainActivity.fragmentTransaction(chatListFragment, R.id.chatFragmentHolder,"ChatList");
    }

    public void changeFragment(Bundle bundle) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SingleChatActivity singleChatFragment = new SingleChatActivity();
        //singleChatFragment.setArguments(bundle);
        fragmentTransaction.addToBackStack("Frag");
        //fragmentTransaction.replace(R.id.chatFragmentHolder, singleChatFragment);
        fragmentTransaction.commit();
    }
}
