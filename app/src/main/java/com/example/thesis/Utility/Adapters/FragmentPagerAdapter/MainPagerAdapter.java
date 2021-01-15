package com.example.thesis.Utility.Adapters.FragmentPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.thesis.Fragments.ChatFragment;
import com.example.thesis.Fragments.ChatListFragment;
import com.example.thesis.Fragments.EventsFragment;
import com.example.thesis.Fragments.MapFragment;
import com.example.thesis.Fragments.NotesFragment;
import com.example.thesis.Fragments.ProfileFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new EventsFragment();
            case 2:
                return new NotesFragment();
            default:
                return new ChatListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}