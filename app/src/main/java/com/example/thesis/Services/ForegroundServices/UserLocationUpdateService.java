package com.example.thesis.Services.ForegroundServices;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.thesis.Backend.DatabaseInformationController;
import com.example.thesis.DatabaseModels.User;
import com.example.thesis.DatabaseModels.UserLocation;
import com.example.thesis.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.thesis.DatabaseModels.GeoPoint;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserLocationUpdateService extends Service {

    private static final String TAG = "LocationService";
    private DatabaseInformationController dbController;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4000;
    private final static long FASTEST_INTERVAL = 2000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbController = new DatabaseInformationController();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "channel_1";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Update Location Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_text))
                    .setSmallIcon(R.drawable.ic_location)
                    .setPriority(NotificationCompat.PRIORITY_MAX).build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location Done");
            stopSelf();
            return;
        }
        Log.d(TAG, mFusedLocationClient.toString());
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Location location = locationResult.getLastLocation();
                        Log.d(TAG, "Location Sending");
                        if (location != null) {
                            FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                            UserLocation userLocation = null;
                            if(currUser != null) {
                                User user = new User(currUser.getDisplayName(), currUser.getEmail(), currUser.getPhotoUrl().toString(), currUser.getUid());
                                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                userLocation = new UserLocation(geoPoint, null, user);
                            }
                            saveUserLocation(userLocation);
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void saveUserLocation(final UserLocation userLocation){

        try{
            Log.d(TAG, userLocation.toString());
//            DatabaseReference userLocationRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users_location_collection));
//            userLocationRef.child(userLocation.getUser().getuId()).setValue(userLocation);
            dbController.saveUserLocation(userLocation);
        }catch (NullPointerException e){
            Log.d(TAG, "Service Stopped");
            stopSelf(); //Stop when user is logged out
        }

    }
}

