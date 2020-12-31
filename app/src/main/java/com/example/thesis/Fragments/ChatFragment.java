package com.example.thesis.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.MainActivity;
import com.example.thesis.R;

public class ChatFragment extends Fragment {

    private FragmentManager fragmentManager;

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
        SingleChatFragment chatListFragment = new SingleChatFragment();
        fragmentManager = mainActivity.fragmentTransaction(chatListFragment, R.id.chatFragmentHolder,"ChatList");
    }

    public void changeFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddParticipantsFragment participantsFragment = new AddParticipantsFragment();
        fragmentTransaction.addToBackStack("Frag");
        fragmentTransaction.replace(R.id.fragmentContainer, participantsFragment);
        fragmentTransaction.commit();
    }
}
