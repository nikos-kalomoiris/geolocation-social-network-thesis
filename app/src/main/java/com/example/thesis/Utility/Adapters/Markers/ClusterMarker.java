package com.example.thesis.Utility.Adapters.Markers;

import android.net.Uri;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.Note;
import com.example.thesis.DatabaseModels.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class ClusterMarker implements ClusterItem {

    private String tag;

    private LatLng position;
    private String title;
    private String snippet;
    private Uri iconPicture;
    private String key;
    //private String getIconPictureString;

    private User user;
    private Note note;
    private Event event;

    public ClusterMarker(String tag, LatLng position, String title, String snippet, Uri iconPicture, User user) {
        this.tag = tag;
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.user = user;
    }

    public ClusterMarker(String tag, Note note, String key) {
        this.tag = tag;
        this.title = note.getNoteTitle();
        this.snippet = "By: " + note.getAuthorName();
        this.note = note;
        this.position = new LatLng(note.getGeoPoint().getLatitude(), note.getGeoPoint().getLongitude());
        this.key = key;
    }

    public ClusterMarker(String tag, Event event, String key) {
        this.tag = tag;
        this.title = event.getTitle();
        this.snippet = "By: " + event.getAuthorName();
        this.event = event;
        this.position = new LatLng(event.getGeoPoint().getLatitude(), event.getGeoPoint().getLongitude());
        this.key = key;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Uri getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(Uri iconPicture) {
        this.iconPicture = iconPicture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
