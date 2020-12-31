package com.example.thesis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.thesis.Backend.DatabaseInformationController;
import com.example.thesis.DatabaseModels.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.button.MaterialButton;


import java.util.Arrays;

import static com.example.thesis.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.thesis.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class LoginActivity extends Activity {
    public static AuthUI currInstance;

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "Debug";
    private boolean locationPermissionGranted = false;

    public static User userObj;

    protected static LoginActivity instance;

    private MaterialButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_login);
        currInstance = AuthUI.getInstance();
        getLocationPermission();

        login = findViewById(R.id.googleLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationPermissionGranted) {
                    signInOptions();
                }
                else {
                    Toast.makeText(getContext(), "Please give location permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void signInOptions() {

        startActivityForResult(
                currInstance
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                //new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);

    }

    public void getLocationPermission() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    signInOptions(); //Calling sign-in options after checking if location permission granted
                }
                else {
                    getLocationPermission();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called. LoginActivity");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (!locationPermissionGranted) {
                    getLocationPermission();
                }
                else {

                }
            }
        }

        if (requestCode == RC_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                signInOptions();
            }
        }
    }


    //get the activity resources for universal use
    public static Resources getRes() {
        return instance.getResources();
    }

    //
    public static Context getContext() {
        return getContext();
    }

    //get the current user object
    public static User getUserObj() {
        return userObj;
    }
}