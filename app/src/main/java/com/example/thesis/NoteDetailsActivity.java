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

import com.example.thesis.DatabaseModels.GeoPoint;
import com.example.thesis.DatabaseModels.Note;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NoteDetailsActivity extends AppCompatActivity {

    private MaterialTextView noteTitle, noteText, noteAuthor, noteLocation, noteDuration;
    private MaterialButton doneBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        //Setting Toolbar
        LinearLayout toolBarTemplate = findViewById(R.id.mainToolbar);
        Toolbar toolbar = toolBarTemplate.findViewById(R.id.mainToolbarTemplate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Note note = (Note) intent.getSerializableExtra("note");

        initWidgets(note);
    }

    private void initWidgets(Note note) {
        noteTitle = (MaterialTextView) findViewById(R.id.noteDetailsTItle);
        noteText = (MaterialTextView) findViewById(R.id.noteDescText);
        noteAuthor = (MaterialTextView) findViewById(R.id.noteAuthorText);
        noteLocation = (MaterialTextView) findViewById(R.id.noteLocationText);
        noteDuration = (MaterialTextView) findViewById(R.id.noteDurationText);
        doneBtn = (MaterialButton) findViewById(R.id.detailsDoneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String address = convertToAddress(note.getGeoPoint());

        noteTitle.setText(note.getNoteTitle());
        noteText.setText(note.getNoteText());
        noteAuthor.setText("Author: " + note.getAuthor().getuDisplayName());
        if(address != null) {
            noteLocation.setText("Address: " + address);
        }
        else {
            noteLocation.setText("Address: Not Found");
        }

        switch (note.getDuration()) {
            case "min": noteDuration.setText("Duration: 30 min"); break;
            case "med": noteDuration.setText("Duration: 1 hour"); break;
            case "max": noteDuration.setText("Duration: 3 hours"); break;
        }

    }

    private String convertToAddress(GeoPoint coordinates) {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(coordinates.getLatitude(), coordinates.getLongitude(), 1);

            String address = addresses.get(0).getAddressLine(0);

            return address;
        }
        catch (IOException ex) {
            Log.e("Error", ex.getMessage());
        }
        return null;
    }
}
