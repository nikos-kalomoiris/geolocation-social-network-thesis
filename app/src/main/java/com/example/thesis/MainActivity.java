package com.example.thesis;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.thesis.Backend.DatabaseInformationController;
import com.example.thesis.ForegroundServices.UserLocationUpdateService;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.Fragments.ChatListFragment;
import com.example.thesis.Fragments.MapFragment;
import com.example.thesis.Utility.Adapters.FragmentPagerAdapter.MainPagerAdapter;
import com.example.thesis.Fragments.ProfileFragment;
import com.example.thesis.Interfaces.ClickInterface;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.thesis.Utility.Constants.ERROR_DIALOG_REQUEST;
import static com.example.thesis.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.thesis.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends FragmentActivity implements ClickInterface {


    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "Debug";

    public static AuthUI currInstance;
    private FirebaseUser user = null;
    private boolean locationPermissionGranted = false;
    private MapFragment mapFragment;
    public static User userObj;
    private DatabaseInformationController dbController;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static MainActivity instance;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setPagerAdapter();
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(this, "Successful Login!\n" + user.getEmail(), Toast.LENGTH_SHORT).show();

        //Setting the click interface for logout button
        ProfileFragment.setClickInterface(this);

        dbController = new DatabaseInformationController();
        saveUserInfo();
        currInstance = AuthUI.getInstance();
        startLocationService();
    }

    private void setPagerAdapter() {
        final ViewPager2 mainViewPager = findViewById(R.id.mainPager);
        mainViewPager.setAdapter(new MainPagerAdapter(this));

        TabLayout mainTab = findViewById(R.id.mainTab);
        TabLayoutMediator tabLayoutMediator  =  new TabLayoutMediator(
                mainTab, mainViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.map_tab);
                        tab.setIcon(R.drawable.ic_map);
                        break;
                    case 1:
                        tab.setText(R.string.profile_tab);
                        tab.setIcon(R.drawable.ic_profile);
                        break;
                    case 2:
                        tab.setText(R.string.chat_tab);
                        tab.setIcon(R.drawable.ic_chat);
                        break;
                }
            }
        }
        );
        tabLayoutMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed");
        if(checkMapServices() && locationPermissionGranted){
            Log.d(TAG, "Location changed. onResume - MainActivity");
            //mapFragment.setMapUserLocation();
        }
        else{
            getLocationPermission();
        }
    }

    //Setting the buttonClick method for the ClickInterface
    @Override
    public void buttonClick() {
        callLoginActivity();
    }

    private boolean checkMapServices(){
        if(isServicesOK()) {
            if(isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildLocationPermissionAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Application requires gps access, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildLocationPermissionAlert();
            return false;
        }
        return true;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, UserLocationUpdateService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                MainActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.thesis.BackgroundServices.UserLocationUpdateService".equals(service.service.getClassName())) {
                Log.d("LocationService", "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d("LocationService", "isLocationServiceRunning: location service is not running.");
        return false;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void saveCurrentUser() {
        userObj = new User(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString(), user.getUid());
        dbController.saveUserInformation(userObj);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void saveUserInfo() {
        Log.i("Test-2", user.getDisplayName());
        saveCurrentUser();
        ImageView profileImage = findViewById(R.id.profileImage);
        if(profileImage != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(profileImage);
            if(mFusedLocationProviderClient != null) {
                //Get you to your current location on the map
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location usrLocation) {
                                location = usrLocation;
                                mapFragment.saveCurrentUserLocation(location, userObj);
                            }
                        });
            }
        }
        //Getting the location of the user in onMapReady and saving it after the user logged in
        //saveCurrentUserLocation(MapFragment.getLocation(), getUserObj());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called. MainActivity");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationPermissionGranted){
                    Log.d(TAG, "Permission Granted");
                }
                else{
                    getLocationPermission();
                }
            }
        }

        if(requestCode == RC_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                ImageView profileImage = findViewById(R.id.profileImage);
                if(profileImage != null) {
                    Glide.with(this).load(user.getPhotoUrl()).into(profileImage);
                }
                saveCurrentUser();
                Log.d(TAG, "Saved User Location");

            }
            else{
                if(response != null)
                    Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                callLoginActivity();
            }
        }
    }

    private void callLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public FragmentManager fragmentTransaction(Fragment fragment, int fragmentHolder, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragmentHolder, fragment, tag);
        fragmentTransaction.commit();
        return fragmentManager;
    }

    //get the activity resources for universal use
//    public static Resources getRes() {
//        return instance.getResources();
//    }
    //
    public static Context getContext() {
        return getContext();
    }
    //get the current user object
    public static User getUserObj() {
        return userObj;
    }
}
