package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private String title, description, date, authorName, authorId;
    private ArrayList<String> participants = new ArrayList<>();
    private GeoPoint geoPoint;

    public Event() {

    }

    public Event(String title, String description, String date, String authorName, String authorId, ArrayList<String> participants, GeoPoint geoPoint) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.authorName = authorName;
        this.authorId = authorId;
        this.participants.addAll(participants);
        this.geoPoint = geoPoint;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
