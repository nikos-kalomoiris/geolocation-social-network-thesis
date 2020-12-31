package com.example.thesis.Objects;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class PolylineData {

    private Polyline polyline;
    private DirectionsLeg leg;

    public PolylineData(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setLeg(DirectionsLeg leg) {
        this.leg = leg;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
