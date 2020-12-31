package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Note implements Serializable{

    private String noteTitle ,noteText, duration, authorId, authorName;
    private GeoPoint geoPoint;

    public Note() {

    }

    public Note(String noteTitle, String noteText, String duration,String authorId, String authorName, GeoPoint geoPoint) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.duration = duration;
        this.authorId = authorId;
        this.authorName = authorName;
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

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
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
