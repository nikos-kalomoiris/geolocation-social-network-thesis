package com.example.thesis;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thesis.Fragments.AddEventDetailsFragment;
import com.example.thesis.Fragments.AddParticipantsFragment;


public class AddEventActivity extends AppCompatActivity {

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        //Setting Toolbar
        LinearLayout toolBarTemplate = findViewById(R.id.mainToolbar);
        Toolbar toolbar = toolBarTemplate.findViewById(R.id.mainToolbarTemplate);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEventDetailsFragment eventDetailsFragment = new AddEventDetailsFragment();
        fragmentTransaction.add(R.id.fragmentContainer, eventDetailsFragment, "EventDetails");
        fragmentTransaction.commit();
    }

    public void changeFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddParticipantsFragment participantsFragment = new AddParticipantsFragment();
        fragmentTransaction.addToBackStack("Frag");
        fragmentTransaction.replace(R.id.fragmentContainer, participantsFragment);
        fragmentTransaction.commit();
    }

    public FragmentManager getEventFragmentManager() {
        return fragmentManager;
    }
}

