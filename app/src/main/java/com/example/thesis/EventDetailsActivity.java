package com.example.thesis;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.GeoPoint;
import com.example.thesis.DatabaseModels.Note;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private MaterialTextView eventTitle, eventDesc, eventAuthor, eventLocation, eventDate, eventParticipants;
    private MaterialButton doneBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        //Setting Toolbar
        LinearLayout toolBarTemplate = findViewById(R.id.mainToolbar);
        Toolbar toolbar = toolBarTemplate.findViewById(R.id.mainToolbarTemplate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra("event");

        initWidgets(event);
    }

    private void initWidgets(Event event) {
        eventTitle = (MaterialTextView) findViewById(R.id.eventDetailsTitle);
        eventDesc = (MaterialTextView) findViewById(R.id.eventDescText);
        eventAuthor = (MaterialTextView) findViewById(R.id.eventOrganizerText);
        eventLocation = (MaterialTextView) findViewById(R.id.eventLocationText);
        eventDate = (MaterialTextView) findViewById(R.id.eventDateText);
        eventParticipants = (MaterialTextView) findViewById(R.id.eventParticipantsText);
        //noteDuration = (MaterialTextView) findViewById(R.id.eventParticipantsText);
        doneBtn = (MaterialButton) findViewById(R.id.detailsDoneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String address = convertToAddress(event.getGeoPoint());

        eventTitle.setText(event.getTitle());
        eventDesc.setText(event.getDescription());
        eventAuthor.setText("Organizer: " + event.getAuthor().getuDisplayName());
        if(address != null) {
            eventLocation.setText("Address: " + address);
        }
        else {
            eventLocation.setText("Address: Not Found");
        }
        eventDate.setText("Event Date: " + event.getDate());

        String participants = "";
        int index = 0;
        for(String user: event.getParticipants()) {
            if(index == event.getParticipants().size() - 1) {
                participants += user;
            }
            else {
                participants += user + ", ";
            }
        }

        //eventParticipants.setText(participants);
        //noteDuration.setText("Duration: " + note.getDuration());
    }

    private String convertToAddress(GeoPoint coordinates) {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(coordinates.getLatitude(), coordinates.getLongitude(), 1);

            String address = addresses.get(0).getAddressLine(0);
//            String city = addresses.get(0).getLocality();
//            String postalCode = addresses.get(0).getPostalCode();

            return address;
        }
        catch (IOException ex) {
            Log.e("Error", ex.getMessage());
        }
        return null;
    }
}
