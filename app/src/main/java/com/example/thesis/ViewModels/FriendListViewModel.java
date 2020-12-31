package com.example.thesis.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;

import java.util.ArrayList;

public class FriendListViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ClusterMarker>> markersList =  new MutableLiveData<>();

    public LiveData<ArrayList<ClusterMarker>> getMarkers() {
        return markersList;
    }

    public void setMarkers(ArrayList<ClusterMarker> markersList) {
        this.markersList.setValue(markersList);
    }
}


