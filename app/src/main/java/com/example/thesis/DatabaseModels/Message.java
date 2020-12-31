package com.example.thesis.DatabaseModels;

import android.net.Uri;

import androidx.annotation.NonNull;

public class Message {

    private User sender;
    private String message;
    private String timestamp;

    public Message() {

    }

    public Message(User sender, String message, String timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
