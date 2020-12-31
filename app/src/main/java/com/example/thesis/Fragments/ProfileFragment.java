package com.example.thesis.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.thesis.FriendsActivity;
import com.example.thesis.Interfaces.ClickInterface;
import com.example.thesis.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static ClickInterface clkInterface;

    private FirebaseUser user = null;
    private ViewPager2 mainPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPager = getActivity().findViewById(R.id.mainPager); //Get mainViewPager to disable swipe in mapFragment
        //Log.e("Error", mainPager.toString());
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final Button logoutButton = (Button) view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);
        final Button friendsButton = (Button) view.findViewById(R.id.friendsButton);
        friendsButton.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        Glide.with(this).load(user.getPhotoUrl()).into(profileImage);

        TextView userName = (TextView) view.findViewById(R.id.userNameText);
        userName.setText(user.getDisplayName());

        return view;
    }

    public void setLogoutButton() {
                AlertDialog dialog;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog = builder.create();
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                AuthUI.getInstance()
                                        .signOut(getView().getContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                clkInterface.buttonClick();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getView().getContext(),
                                                        "Failed to logout, try again!",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                final AlertDialog alert = builder.create();
                alert.show();
    }

    public void setFriendsButton() {
        Intent intent = new Intent(getActivity(), FriendsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logoutButton:
                setLogoutButton();
                break;
            case R.id.friendsButton:
                setFriendsButton();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainPager.setUserInputEnabled(true);
    }

    public static void setClickInterface(ClickInterface clkInter) {
        clkInterface = clkInter;
    }
}

