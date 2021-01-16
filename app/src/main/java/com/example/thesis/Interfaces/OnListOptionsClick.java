package com.example.thesis.Interfaces;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.google.android.gms.maps.model.LatLng;

public interface OnListOptionsClick {
    void clickLocation(LatLng location);
    void clickDetails(ClusterMarker event);
}
