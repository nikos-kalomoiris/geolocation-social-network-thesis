package com.example.thesis.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thesis.DatabaseModels.User;

import java.util.ArrayList;

public class FriendRequestsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<User>> requestsList =  new MutableLiveData<>();

    public LiveData<ArrayList<User>> getRequests() {
        return requestsList;
    }

    public void setRequests(ArrayList<User> requestsList) {
        this.requestsList.setValue(requestsList);
    }
}
