package com.example.thesis.DatabaseModels;

import java.io.Serializable;

public class GeoPoint implements Serializable {

    private double latitude;
    private double longitude;

    public GeoPoint() {

    }

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return "Latitude: " + getLatitude() + " " +
                "Longitude" + getLongitude();
    }
}
