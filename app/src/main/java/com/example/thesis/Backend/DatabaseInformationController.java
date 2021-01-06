package com.example.thesis.Backend;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.Note;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInformationController {

    final String ERROR_TAG = "Error";

    public static List<UserLocation> userLocationList = new ArrayList<>();
    private FirebaseDatabase database;

    public DatabaseInformationController() {
        database = FirebaseDatabase.getInstance();
    }

    public void saveUserInformation(final User user) {
        final DatabaseReference userRef = database.getReference().child("Users").child(user.getuId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    userRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(ERROR_TAG, databaseError.getMessage());
            }
        });

    }

    public void saveUserLocation(UserLocation userLocation) {
        DatabaseReference userLocationRef = database.getReference().child("User Location");
        userLocationRef.child(userLocation.getUser().getuId()).setValue(userLocation);
    }

    public void saveNote(Note note) {
        DatabaseReference noteRef = database.getReference().child("Notes");
        noteRef.push().setValue(note);
    }

    public void saveEvent(Event event) {
        DatabaseReference noteRef = database.getReference().child("Events");
        noteRef.push().setValue(event);
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }
}
