package com.example.thesis.Interfaces;

import com.example.thesis.DatabaseModels.UserLocation;

import java.util.List;

public interface OnDataFetchCallbackInterface {

    void onDataFetch(List<UserLocation> userLocations);
}
