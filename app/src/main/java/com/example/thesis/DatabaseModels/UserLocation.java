package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

//import com.google.firebase.firestore.GeoPoint;
import com.example.thesis.DatabaseModels.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {

    private GeoPoint geoPoint;
    private @ServerTimestamp Date timeStamp;
    private User user;

    public UserLocation() {

    }

    public UserLocation(GeoPoint geoPoint, Date timeStamp, User user) {
        this.geoPoint = geoPoint;
        this.timeStamp = timeStamp;
        this.user = user;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NonNull
    @Override
    public String toString() {
        return "Geopoint: " + getGeoPoint() + "/n" +
                "Timestamp: " + getTimeStamp() + "/n" +
                "User: " + getUser().toString();
    }
}
