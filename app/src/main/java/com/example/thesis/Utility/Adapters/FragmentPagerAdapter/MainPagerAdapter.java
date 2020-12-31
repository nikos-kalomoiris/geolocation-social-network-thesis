package com.example.thesis.Utility.Adapters.FragmentPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.thesis.Fragments.ChatFragment;
import com.example.thesis.Fragments.MapFragment;
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
                return new ProfileFragment();
            default:
                return new ChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

/*public class MainPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;
    public static MapFragment mapFragment;
    public static ProfileFragment profileFragment;
    public static ChatFragment chatFragment;

    public MainPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mapFragment = new MapFragment();
                return mapFragment;
            case 1:
                profileFragment = new ProfileFragment();
                return profileFragment;//ProfileFragment.newInstance(1, "Profile");
            case 2:
                chatFragment = new ChatFragment();
                return chatFragment;
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Map";
            case 1:
                return "Profile";
            case 2:
                return "Chat";
        }

        return null;
    }


}*/