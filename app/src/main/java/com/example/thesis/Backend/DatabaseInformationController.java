package com.example.thesis.Backend;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.Note;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.Interfaces.OnDataFetchCallbackInterface;
import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseInformationController {

    final String ERROR_TAG = "Error";

    public static List<UserLocation> userLocationList = new ArrayList<>();
    private FirebaseDatabase database;
    private Resources resources = MainActivity.getRes();

    public DatabaseInformationController() {
        database = FirebaseDatabase.getInstance();
    }

    public void saveUserInformation(final User user) {
        final DatabaseReference userRef = database.getReference().child(resources.getString(R.string.users_collection)).child(user.getuId());
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
        DatabaseReference userLocationRef = database.getReference().child(resources.getString(R.string.users_location_collection));
        userLocationRef.child(userLocation.getUser().getuId()).setValue(userLocation);
    }

    public void saveNote(Note note) {
        DatabaseReference noteRef = database.getReference().child(resources.getString(R.string.notes_collection));
        noteRef.push().setValue(note);
    }

    public void saveEvent(Event event) {
        DatabaseReference noteRef = database.getReference().child(resources.getString(R.string.events_collection));
        noteRef.push().setValue(event);
    }

    /*public void getFriendList(User user, final OnDataFetchCallbackInterface dataFetch) {
        DatabaseReference userLocRef = database.getReference()
                .child(resources.getString(R.string.users_location_collection))
                .child(user.getuId())
                .child(resources.getString(R.string.friends_list_collection));

        userLocRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot locationSnapshot: dataSnapshot.getChildren()) {
                    UserLocation userLocation = locationSnapshot.getValue(UserLocation.class);
                    userLocationList.add(userLocation);
                }
                dataFetch.onDataFetch(userLocationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(ERROR_TAG, databaseError.getMessage());
            }
        });
    }*/

    /*public void getUsersList(String uId, final OnDataFetchCallbackInterface dataFetch) {
        userLocationList.clear();
        DatabaseReference friendListRef = database.getReference()
                                            .child(resources.getString(R.string.users_collection))
                                            .child(uId)
                                            .child(resources.getString(R.string.friends_list_collection));
        Log.d("DataOut", uId);
        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;
                for(DataSnapshot locationSnapshot: dataSnapshot.getChildren()) {
                    index++;
                    final long dtCount = dataSnapshot.getChildrenCount();
                    final long checkLast = index;
                    String id = locationSnapshot.getValue(String.class);
                    Log.d("DataOut", "Friend id: " + id);
                    DatabaseReference getFriendLocation = database.getReference().child(resources.getString(R.string.users_location_collection)).child(id);
                    Log.d("DataOut", getFriendLocation.toString());
                    getFriendLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Log.d("DataOut", "0: " + dataSnapshot.toString());
                                    UserLocation userLocation = dataSnapshot.getValue(UserLocation.class);
                                    Log.d("DataOut", "1: " + userLocation.toString());
                                    userLocationList.add(userLocation);
                                }
                                Log.d("DataOut", "Count: " + dtCount);
                                if(checkLast == dtCount) {
                                    dataFetch.onDataFetch(userLocationList);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(ERROR_TAG, databaseError.getMessage());
                        }
                    });

                }
                Log.d("DataOut", "Out");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(ERROR_TAG, databaseError.getMessage());
            }

        });

    }*/

    public FirebaseDatabase getDatabase() {
        return database;
    }
}
