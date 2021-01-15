package com.example.thesis.Fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.R;

public class NotesFragment extends Fragment {
    private ViewPager2 mainPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPager = getActivity().findViewById(R.id.mainPager); //Get mainViewPager to disable swipe in mapFragment
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug", "ProfileFragment - onResume");
        mainPager.setUserInputEnabled(true);
    }
}


