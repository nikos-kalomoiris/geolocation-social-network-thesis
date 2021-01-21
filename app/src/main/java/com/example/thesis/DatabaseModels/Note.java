package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Note implements Serializable{

    private String noteTitle ,noteText, duration;
    private GeoPoint geoPoint;
    private User author;

    public Note() {

    }

    public Note(String noteTitle, String noteText, String duration, User author, GeoPoint geoPoint) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.duration = duration;
        this.author = author;
        this.geoPoint = geoPoint;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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
