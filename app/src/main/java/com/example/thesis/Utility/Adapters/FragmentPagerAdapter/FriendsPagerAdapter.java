package com.example.thesis.Utility.Adapters.FragmentPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.thesis.Fragments.FriendListFragment;
import com.example.thesis.Fragments.FriendRequestsFragment;

public class FriendsPagerAdapter extends FragmentStateAdapter {

    public FriendsPagerAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FriendListFragment();
            default:
                return new FriendRequestsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
