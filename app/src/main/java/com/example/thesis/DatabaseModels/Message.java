package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class Message {

    private String senderId;
    private String message;
    private Timestamp timestamp;

    public Message(String senderId, String message, Timestamp timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
