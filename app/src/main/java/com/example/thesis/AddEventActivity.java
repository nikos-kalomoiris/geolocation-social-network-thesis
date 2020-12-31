package com.example.thesis;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thesis.Fragments.AddEventDetailsFragment;
import com.example.thesis.Fragments.AddParticipantsFragment;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

//    final Calendar calendar = Calendar.getInstance();
//
//    private final int MAX_CHARS = 144;
//    private final int TITLE_MAX_CHARS = 35;
//
//    private TextView charsLimitTitle, charsLimitDesc, participantsText;
//    private EditText titleInput, descInput, dateInput;
//    private MaterialButton addParticipants, addEvent;

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
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

