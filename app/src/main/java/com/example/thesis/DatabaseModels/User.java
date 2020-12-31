package com.example.thesis.DatabaseModels;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String uDisplayName;
    private String uEmail;
    private String uIconUrl;
    private String uId;
    //private ArrayList<UserLocation> uFriendRequests;
    //private ArrayList<UserLocation> uFriendList;

    public User() {

    }

    public User(String uDisplayName, String uEmail, String uIconUrl, String uId) {
        this.uDisplayName = uDisplayName;
        this.uEmail = uEmail;
        this.uIconUrl = uIconUrl;
        this.uId = uId;
    }

    /*public User(String uDisplayName, String uEmail, String uIconUrl, String uId, ArrayList<UserLocation> uFriendList ,ArrayList<UserLocation> uFriendRequests) {
        this.uDisplayName = uDisplayName;
        this.uEmail = uEmail;
        this.uIconUrl = uIconUrl;
        this.uId = uId;
        this.uFriendList.addAll(uFriendList);
        this.uFriendRequests.addAll(uFriendRequests);
    }*/

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

    public void setuFriendList(List<String> friendList) {

    }

    /*public void setuFriendRequests(List<UserLocation> friendRequest) {
        this.uFriendRequests.addAll(friendRequest);
    }

    public List<UserLocation> getFriendList() {
        return uFriendList;
    }

    public List<UserLocation> getFriendRequest() {
        return uFriendRequests;
    }*/
    @NonNull
    @Override
    public String toString() {
        return "DisplayName: " + getuDisplayName() + "/n" +
                "Email: " + getuEmail() + "/n" +
                "PhotoUrl: " + getuIconUrl() + "/n";
    }
}
