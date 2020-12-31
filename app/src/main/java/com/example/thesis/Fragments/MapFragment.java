package com.example.thesis.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.AddEventActivity;
import com.example.thesis.AddNoteActivity;
import com.example.thesis.Backend.DatabaseInformationController;
import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.Note;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.EventDetailsActivity;
import com.example.thesis.MainActivity;
import com.example.thesis.NoteDetailsActivity;
import com.example.thesis.Objects.PolylineData;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.Markers.ClusterManagerRenderer;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.example.thesis.ViewModels.FriendRequestsViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.thesis.DatabaseModels.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>,
        GoogleMap.OnPolylineClickListener {

    private final String DIRECTION_TAG = "Directions";

    private static final int UPDATE_INTERVAL = 3000;
    private static final int LAUNCH_ADD_NOTE_ACTIVITY = 1112;
    private static final int LAUNCH_ADD_EVENT_ACTIVITY = 1113;
    private static final int ACTION_ADD = 2;
    private static final int ACTION_DELETE = 1;
    //private static final int LAUNCH_NOTE_DETAILS_ACTIVITY = 1114;

    public static Location location;
    public static MapView mapView;
    public static ViewModelStoreOwner mapFragmentStore;
    //Marker Arrays
    private ArrayList<ClusterMarker> clusterMarkers = new ArrayList<>();
    private ArrayList<ClusterMarker> noteMarkers = new ArrayList<>();
    private ArrayList<ClusterMarker> eventMarkers = new ArrayList<>();

    private ArrayList<PolylineData> polylines = new ArrayList<>();
    private ArrayList<Marker> tripMarkers = new ArrayList<>();

    private ArrayList<User> friendRequests = new ArrayList<>();
    private UserLocation userLocation;
    private GoogleMap map;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private DatabaseInformationController dbController;

    private ClusterMarker selectedMarker = null;

    //Cluster Managers
    private ClusterManager clusterManager;

    private FirebaseUser user = null;

    //Custom cluster renderers
    private ClusterManagerRenderer clusterManagerRenderer;

    private FriendListViewModel friendListModel;
    private FriendRequestsViewModel friendRequestModel;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private ViewPager2 mainPager;
    private boolean fabIsOpen = false;
    private Note note;
    private Event event;
    private GeoApiContext geoApiContext = null;

    private MaterialButton fabAdd, fabNote, fabEvent, addNoteEventBtn, cancelNoteEventBtn, fabCancelDirections;
    private Animation fabOpen, fabClose, fabRotateClock, getFabRotateCounterClock;
    private SearchView findLocationSearchView;
    private ImageView target;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        dbController = new DatabaseInformationController();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //dbController = new DatabaseInformationController();
        Log.d("Debug", "onCreate - MapFragment");

        //Get mainViewPager to disable swipe in mapFragment
        mainPager = getActivity().findViewById(R.id.mainPager);

        //Implementing ViewModel Providers
        friendListModel = new ViewModelProvider(this).get(FriendListViewModel.class);
        friendRequestModel = new ViewModelProvider(this).get(FriendRequestsViewModel.class);

        mapFragmentStore = this;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize add note and event buttons and fab animation
        initializeFab(view);
        initializeMarkerAdd(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        if(geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    //Manipulating the map once available
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            MapsInitializer.initialize(getContext());
            map = googleMap;
            map.setMyLocationEnabled(true);

            Log.d("Debug", "FusedLocationProvider: " + mFusedLocationProviderClient + "| onMapReady - MapFragment");
            setMapUserLocation();
            setNotesListener();
            Log.d("Debug", "Location changed. onMapReady - MapFragment");
            Log.d("Debug", "Map is ready");
            //Implementing database listeners
            setFriendListListener();
            setRequestListener();
            setEventsListener();

            map.setOnPolylineClickListener(this);
           setClusterManagers();
        }
        catch (NullPointerException ex) {
            Log.e("Error", ex.getMessage());
        }

    }

    private void setClusterManagers() {
        if(clusterManager == null) {
            clusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), map);
        }

        clusterManagerRenderer = new ClusterManagerRenderer(getActivity(), map, clusterManager);
        clusterManager.setRenderer(clusterManagerRenderer);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
    }

    private void initializeMarkerAdd(View view) {
        findLocationSearchView = (SearchView) view.findViewById(R.id.searchLocation);
        target = (ImageView) view.findViewById(R.id.target);
        addNoteEventBtn = (MaterialButton) view.findViewById(R.id.addMarkerButton);
        cancelNoteEventBtn = (MaterialButton) view.findViewById(R.id.cancelMarkerButton);

        findLocationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = findLocationSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(!location.equals("")) {
                    Geocoder geocoder = new Geocoder(view.getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }
                    catch (IOException ex) {
                        Log.e("Error", ex.getMessage());
                    }

                    if(addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    }
                    else {
                        Toast.makeText(view.getContext(), "Location not found.", Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        addNoteEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(addNoteEventBtn.getText().toString().equals("Set Note")) {
                        if(note != null) {
                            LatLng mapCenter = map.getCameraPosition().target;
                            GeoPoint geoPoint = new GeoPoint(mapCenter.latitude, mapCenter.longitude);
                            note.setGeoPoint(geoPoint);
                            dbController.saveNote(note);
                        }
                    }
                    else if(addNoteEventBtn.getText().toString().equals("Set Event")) {
                        if(event != null) {
                            LatLng mapCenter = map.getCameraPosition().target;
                            GeoPoint geoPoint = new GeoPoint(mapCenter.latitude, mapCenter.longitude);
                            event.setGeoPoint(geoPoint);
                            dbController.saveEvent(event);
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Couldn't make marker", Toast.LENGTH_SHORT).show();
                    }

//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(mapCenter);
//                    map.addMarker(markerOptions);
                    cancelAddInterface();
                }
        });

        cancelNoteEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAddInterface();
            }
        });
    }

    private void initializeFab(View view) {
        fabAdd = (MaterialButton) view.findViewById(R.id.fabMap);
        fabNote = (MaterialButton) view.findViewById(R.id.fabAddNote);
        fabEvent = (MaterialButton) view.findViewById(R.id.fabAddEvent);
        fabCancelDirections = (MaterialButton) view.findViewById(R.id.fabCancelDirection);

        fabOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_close);
        fabRotateClock = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_rotate_clockwise);
        getFabRotateCounterClock = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_rotate_counterclockwise);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fabIsOpen) {
                    fabNote.startAnimation(fabClose);
                    fabEvent.startAnimation(fabClose);
                    fabAdd.startAnimation(fabRotateClock);

                    fabEvent.setClickable(false);
                    fabNote.setClickable(false);

                    fabIsOpen = false;
                }
                else {
                    fabNote.startAnimation(fabOpen);
                    fabEvent.startAnimation(fabOpen);
                    fabAdd.startAnimation(getFabRotateCounterClock);

                    fabEvent.setClickable(true);
                    fabNote.setClickable(true);

                    fabIsOpen = true;
                }
            }
        });

        fabNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, LAUNCH_ADD_NOTE_ACTIVITY);
            }
        });

        fabEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, LAUNCH_ADD_EVENT_ACTIVITY);
            }
        });

        fabCancelDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTripMarkers();

                if(polylines.size() > 0) {
                    for (PolylineData singlePolyline: polylines) {
                        singlePolyline.getPolyline().remove();
                    }
                    polylines.clear();
                    polylines = new ArrayList<>();
                    setSelectedMarkerVisibility(selectedMarker, true);
                }
                fabAdd.setVisibility(View.VISIBLE);
                fabCancelDirections.clearAnimation();
                fabCancelDirections.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == LAUNCH_ADD_NOTE_ACTIVITY){
                String noteText = data.getStringExtra("text");
                String noteDuration = data.getStringExtra("duration");
                String noteTitle = data.getStringExtra("title");
                note = new Note(noteTitle, noteText, noteDuration, user.getUid(),
                        user.getDisplayName(), null);
                setAddInterface("Set Note");
            }
            else if(requestCode == LAUNCH_ADD_EVENT_ACTIVITY) {
                String eventTitle = data.getStringExtra("title");
                String eventDesc = data.getStringExtra("desc");
                String eventDate = data.getStringExtra("date");
                ArrayList<String> participantsId = data.getStringArrayListExtra("participants");
                event = new Event(eventTitle, eventDesc, eventDate,user.getDisplayName(), user.getUid(), participantsId, null);
                setAddInterface("Set Event");
            }
        }
    }

    private void setAddInterface(String action) {
        findLocationSearchView.setVisibility(View.VISIBLE);
        target.setVisibility(View.VISIBLE);
        addNoteEventBtn.setVisibility(View.VISIBLE);
        cancelNoteEventBtn.setVisibility(View.VISIBLE);

        addNoteEventBtn.setText(action);
    }

    private void cancelAddInterface() {
        findLocationSearchView.setVisibility(View.GONE);
        target.setVisibility(View.GONE);
        addNoteEventBtn.setVisibility(View.GONE);
        cancelNoteEventBtn.setVisibility(View.GONE);
    }

    public void setMapUserLocation() {

        if(mFusedLocationProviderClient != null){
            //Get you to your current location on the map
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location usrLocation) {
                            location = usrLocation;
                            if(location != null) {
                                LatLng myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(myLocationLatLng)
                                        .zoom(16)
                                        .build();
                                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                saveCurrentUserLocation(location, MainActivity.getUserObj());
                            }
                            else {
                                Log.d("Debug", "Location NULL");
                            }
                        }
                    });

            //Does something when you click your location
            map.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    Toast.makeText(getContext(), "This is your location!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void saveCurrentUserLocation(Location location, User user) {
        GeoPoint tempUserGeopoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        userLocation = new UserLocation(tempUserGeopoint, null, user);
        DatabaseReference userLocationRef = FirebaseDatabase.getInstance().getReference().child(getActivity().getString(R.string.users_location_collection));
        userLocationRef.child(userLocation.getUser().getuId()).setValue(userLocation);
        //dbController.saveUserLocation(userLocation);
    }

    private void updateFriendsLocation() {
        try {
            for(int i = 0; i < clusterMarkers.size(); i++) {
                final DatabaseReference friendLocRef = FirebaseDatabase.getInstance()
                        .getReference()
                        .child(getString(R.string.users_location_collection))
                        .child(clusterMarkers.get(i).getUser().getuId());
                final int index = i;
                friendLocRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserLocation friendLocation = dataSnapshot.getValue(UserLocation.class);
                        Log.d("Friends", "Fetching Data: " + friendLocation.getUser().getuDisplayName());
                        if(!friendLocation.getUser().getuId().equals(FirebaseAuth.getInstance().getUid())) {
                            LatLng newPosition = new LatLng(friendLocation.getGeoPoint().getLatitude(),
                                    friendLocation.getGeoPoint().getLongitude());
                            clusterMarkers.get(index).setPosition(newPosition);
                            clusterManagerRenderer.updateClusterMarker(clusterMarkers.get(index));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", databaseError.getMessage());
                    }
                });
            }
        }
        catch (IllegalStateException ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void updateFriendsLocationRunnable() {
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                updateFriendsLocation();
                mHandler.postDelayed(mRunnable, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private void setFriendListListener() {
        clusterMarkers.clear();
        DatabaseReference friendList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_collection))
                .child(user.getUid())
                .child(getString(R.string.friends_list_collection));

        friendList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                DatabaseReference singleUserLocation = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_location_collection))
                        .child(dataSnapshot.getValue(String.class));

                singleUserLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {

                            UserLocation userLocation = dataSnapshot.getValue(UserLocation.class);
                            Uri iconPicture = Uri.parse(userLocation.getUser().getuIconUrl());
                            Log.d("friendList", "User: " + userLocation.getUser().getuDisplayName());
                            LatLng ltLnPoint = new LatLng(userLocation.getGeoPoint().getLatitude(),
                                    userLocation.getGeoPoint().getLongitude());

                            String title = userLocation.getUser().getuDisplayName();
                            String snippet = userLocation.getUser().getuEmail();

                            ClusterMarker userMarker = new ClusterMarker("User", ltLnPoint, title, snippet, iconPicture, userLocation.getUser());

                            clusterMarkers.add(userMarker);
                            clusterManager.addItem(userMarker);
                            //refreshNoteList(userLocation.getUser(), ACTION_ADD);
                            clusterManager.cluster();
                            friendListModel.setMarkers(clusterMarkers);

                        } catch (NullPointerException ex) {
                            Log.e("Error", ex.getMessage());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Debug", "Data Deleted");

                for(ClusterMarker userMarker: clusterMarkers) {
                    if(userMarker.getUser().getuId().equals(dataSnapshot.getValue(String.class))) {
                        refreshNoteList(userMarker.getUser(), ACTION_DELETE);
                        clusterMarkers.remove(userMarker);
                        clusterManager.removeItem(userMarker);
                        break;
                    }
                }
                friendListModel.setMarkers(clusterMarkers);
                clusterManager.cluster();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void setRequestListener() {
        DatabaseReference requestList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_collection))
                .child(user.getUid())
                .child(getString(R.string.requests_list_collection));
        Log.d("Request List", "helllo");
        requestList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DatabaseReference singleUser = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_collection))
                        .child(dataSnapshot.getValue(String.class));
                Log.d("Request List", dataSnapshot.getValue(String.class));

                singleUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friendRequests.add(dataSnapshot.getValue(User.class));
                        friendRequestModel.setRequests(friendRequests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference singleUserDel = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_collection))
                        .child(dataSnapshot.getValue(String.class));
                Log.d("Request List", "Deleting");

                singleUserDel.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d("Request List", "User to delete: " + dataSnapshot.getValue(User.class).getuDisplayName());
//                        User user = dataSnapshot.getValue(User.class);
                          Log.d("Request List", "In Data Change");
//                        Log.d("Request List", "Removed: " + friendRequests.remove(user));
                        for(User user: friendRequests) {
                            if(user.getuId().equals(dataSnapshot.getValue(User.class).getuId())) {
                                Log.d("Request List", "Removed: " + friendRequests.remove(user));
                                break;
                            }
                            //Log.d("Request List", "User: " + user.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: pending
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void setNotesListener() {
        DatabaseReference notesList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.notes_collection));

        notesList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Note currNote = dataSnapshot.getValue(Note.class);
                String key = dataSnapshot.getKey();

                Log.d("Added", "CurrNote: " + currNote.getAuthorName() + " " + currNote.getNoteTitle());

                DatabaseReference author = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_collection))
                        .child(user.getUid())
                        .child(getString(R.string.friends_list_collection))
                        .child(currNote.getAuthorId());

                author.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() || currNote.getAuthorId().equals(user.getUid())) {
                            Log.d("Added", "Note added: " + dataSnapshot.exists() + " " + dataSnapshot.getValue(String.class));
                            setNoteMarkOnMap(currNote, key);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", databaseError.getMessage());
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Note currNote = dataSnapshot.getValue(Note.class);
                String key = dataSnapshot.getKey();
                removeNoteMarkFromMap(key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNoteMarkOnMap(Note currNote, String key) {
        try {
            ClusterMarker noteMarker = new ClusterMarker("Note", currNote, key);

            Log.d("ClusterItemLister", "settingNotes");
            noteMarkers.add(noteMarker);
            clusterManager.addItem(noteMarker);
            clusterManager.cluster();

        }
        catch (NullPointerException ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void removeNoteMarkFromMap(String key) {
        try {

                for (ClusterMarker noteMarker: noteMarkers) {
                    if(noteMarker.getTag().equals("Note")) {
                        if(noteMarker.getKey().equals(key)) {
                            noteMarkers.remove(noteMarker);
                            clusterManager.removeItem(noteMarker);
                            break;
                        }
                    }
                }
                clusterManager.cluster();


        }
        catch (NullPointerException ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void refreshNoteList(User noteUser, int action) {
        DatabaseReference noteList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.notes_collection));

        noteList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    String key = noteSnapshot.getKey();

                    if(note.getAuthorId().equals(noteUser.getuId())) {
                        ClusterMarker marker = new ClusterMarker("Note", note, key);

                        if(action == ACTION_ADD) {
                            noteMarkers.add(marker);
                            clusterManager.addItem(marker);
                            //noteClusterManager.addItem(marker);
                        }
                        else if(action == ACTION_DELETE) {

                            for(int i = 0; i < noteMarkers.size(); i++) {
                                if(noteMarkers.get(i).getNote().getAuthorId().equals(noteUser.getuId())) {
                                    Log.d("Note Removed", "From user: " + noteMarkers.get(i).getNote().getAuthorName());
                                    Log.d("Note Removed", "Note title: " + noteMarkers.get(i).getNote().getNoteTitle());
                                    noteMarkers.remove(i);
                                    clusterManager.removeItem(marker);
                                    break;
                                }
                            }
                            //noteMarkers.remove(marker);
                            //noteClusterManager.clearItems();
                            //noteClusterManager.addItems(noteMarkers);

                        }

                    }
                }
                clusterManager.cluster();
                //noteClusterManager.cluster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void setEventsListener() {
        DatabaseReference eventsList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.events_collection));

        eventsList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Event currEvent = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();

                Log.d("Added", "CurrEvent: " + currEvent.getAuthorName() + " " + currEvent.getTitle());

                for(String participantId: currEvent.getParticipants()) {

                     if(currEvent.getAuthorId().equals(user.getUid())) {
                         setEventMarkOnMap(currEvent, key);
                         break;
                     }

                    if(participantId.equals(user.getUid())) {
                        setEventMarkOnMap(currEvent, key);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Event currEvent = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();
                removeEventMarkOnMap(key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setEventMarkOnMap(Event currEvent, String key) {
        try {
            ClusterMarker eventMarker = new ClusterMarker("Event", currEvent, key);

            Log.d("ClusterItemLister", "settingEvents");
            eventMarkers.add(eventMarker);
            clusterManager.addItem(eventMarker);
            clusterManager.cluster();

        }
        catch (NullPointerException ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void removeEventMarkOnMap(String key) {
        try {
            for(ClusterMarker eventMarker: eventMarkers) {
                if(eventMarker.getTag().equals("Event")) {
                    if(eventMarker.getKey().equals(key)) {
                        eventMarkers.remove(eventMarker);
                        clusterManager.removeItem(eventMarker);
                        break;
                    }
                }
            }
            clusterManager.cluster();
        }
        catch (NullPointerException ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void stopUpdateFriendsLocationRunnable() {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug", "onResume - MapFragment");
        updateFriendsLocationRunnable();
        mainPager.setUserInputEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdateFriendsLocationRunnable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "Destroyed - MapFragment");
        stopUpdateFriendsLocationRunnable();
    }

    private void addPathPolylines(DirectionsResult result) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                double duration = 999999999;
                for(DirectionsRoute route: result.routes) {
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    for(com.google.maps.model.LatLng latLng: decodedPath) {
                        newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                    }

                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorPolylineDefault));
                    polyline.setClickable(true);
                    polylines.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        setCameraToRoute(polyline.getPoints());
                    }

                    Log.d("Clicked", "Added polyline with id: " + polyline.getId());
                }
            }
        });
    }

    private void calculateDirections(ClusterMarker marker) {
        Log.d(DIRECTION_TAG, "calculateDirections: calculating directions.");

        if(polylines.size() > 0) {
            for (PolylineData singlePolyline: polylines) {
                singlePolyline.getPolyline().remove();
            }
            polylines.clear();
            polylines = new ArrayList<>();
            setSelectedMarkerVisibility(selectedMarker, true);
        }

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        directions.alternatives(true);
                        directions.origin(
                                new com.google.maps.model.LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                )
                        );

                        Log.d(DIRECTION_TAG, "calculateDirections: destination: " + destination.toString());
                        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                            @Override
                            public void onResult(DirectionsResult result) {
                                Log.d(DIRECTION_TAG, "calculateDirections: routes: " + result.routes[0].toString());
                                Log.d(DIRECTION_TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                                Log.d(DIRECTION_TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                                Log.d(DIRECTION_TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                                addPathPolylines(result);
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Log.e(DIRECTION_TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                            }
                        });
                    }
                });

    }

    private void createMarkerDialog(final ClusterMarker item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Choose action.")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Show info", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openDetails(item);
                    }
                })
                .setNeutralButton("Set Direction", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSelectedMarkerVisibility(item, false);
                        fabAdd.setVisibility(View.GONE);
                        fabEvent.setAnimation(fabClose);
                        fabNote.setAnimation(fabClose);
                        fabCancelDirections.setVisibility(View.VISIBLE);
                        fabCancelDirections.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                        fabCancelDirections.setAnimation(getFabRotateCounterClock);
                        calculateDirections(item);
                        selectedMarker = item;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void setSelectedMarkerVisibility(ClusterMarker item, Boolean isVisible) {
        ArrayList<ClusterMarker> listToSearch;

        if(item.getTag().equals("Event")) {
            listToSearch = eventMarkers;
        }
        else {
            listToSearch = noteMarkers;
        }

        int index = 0;
        for(ClusterMarker marker: listToSearch) {
            if(marker.getTag().equals("Event") || marker.getTag().equals("Note")) {
                if(marker.getKey().equals(item.getKey())) {
                    clusterManagerRenderer.setClusterMarkerVisibility(listToSearch.get(index), isVisible);
                    break;
                }
                index++;
            }
        }

        if(isVisible) {
            selectedMarker = null;
            removeTripMarkers();
        }
    }

    private void removeTripMarkers() {
        for(Marker marker: tripMarkers) {
            marker.remove();
        }
    }

    private void setCameraToRoute(List<LatLng> routeCoordinates) {
        if(map == null || routeCoordinates == null || routeCoordinates.isEmpty()) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng latLng: routeCoordinates) {
            builder.include(latLng);
        }
        LatLngBounds latLngBounds = builder.build();

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 170), 600, null);
    }

    private void openDetails(ClusterMarker item) {

        if(item.getTag().equals("Note")) {
            Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putSerializable("note", item.getNote());
            intent.putExtras(bundle);
            startActivity(intent);

        }
        else if(item.getTag().equals("Event")) {
            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", item.getEvent());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker item) {
        createMarkerDialog(item);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 1;
        Log.d("Clicked", "Clicked polyline with id: " + polyline.getId());
        //Log.d("Clicked", "Is clicking");
        for (PolylineData singlePolyline: polylines) {
            if(polyline.getId().equals(singlePolyline.getPolyline().getId())) {
                Log.d("Clicked", "Checking polyline with id: " + singlePolyline.getPolyline().getId());
                singlePolyline.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorPolylineHighlighted));
                singlePolyline.getPolyline().setZIndex(1);

                if(tripMarkers.size() > 0) {
                    removeTripMarkers();
                }

                LatLng markerPosition = new LatLng(singlePolyline.getLeg().endLocation.lat, singlePolyline.getLeg().endLocation.lng);
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .title("Path #" + index)
                        .snippet("Duration: " + singlePolyline.getLeg().duration)
                );
                tripMarkers.add(marker);

                marker.showInfoWindow();
            }
            else {
                singlePolyline.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorPolylineDefault));
                singlePolyline.getPolyline().setZIndex(0);
            }
            index++;
        }
    }
}

