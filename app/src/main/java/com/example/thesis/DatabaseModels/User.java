package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable, Comparable<User> {

    private String uDisplayName;
    private String uEmail;
    private String uIconUrl;
    private String uId;
    private String uToken;

    public User() {

    }

    public User(String uDisplayName, String uEmail, String uIconUrl, String uId, String uToken) {
        this.uDisplayName = uDisplayName;
        this.uEmail = uEmail;
        this.uIconUrl = uIconUrl;
        this.uId = uId;
        this.uToken = uToken;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuDisplayName() {
        return uDisplayName;
    }

    public void setuDisplayName(String uDisplayName) {
        this.uDisplayName = uDisplayName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuIconUrl() {
        return uIconUrl;
    }

    public void setuIconUrl(String uIconUrl) {
        this.uIconUrl = uIconUrl;
    }

    public String getuToken() {
        return uToken;
    }

    public void setuToken(String uToken) {
        this.uToken = uToken;
    }

    @NonNull
    @Override
    public String toString() {
        return "DisplayName: " + getuDisplayName() + "/n" +
                "Email: " + getuEmail() + "/n" +
                "PhotoUrl: " + getuIconUrl() + "/n";
    }

    @Override
    public int compareTo(User o) {
        return (uId.compareTo(o.getuId()));
    }
}
