package com.example.thesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.thesis.DatabaseModels.User;
import com.example.thesis.Fragments.FriendRequestsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFriendActivity extends AppCompatActivity {

    private String friendEmail;
    private EditText emailInput;
    private Button addFriendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        //Setting Toolbar
        LinearLayout toolBarTemplate = findViewById(R.id.mainToolbar);
        Toolbar toolbar = toolBarTemplate.findViewById(R.id.mainToolbarTemplate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailInput = (EditText) findViewById(R.id.addFriendInput);
        addFriendButton = (Button) findViewById(R.id.addFriendButton);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendEmail = emailInput.getText().toString();
                if(validateEmailAddress(friendEmail)) {
                    findFriendToAdd(friendEmail);
                }
            }
        });
    }

    private boolean validateEmailAddress(String email) {
        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        else {
            makeToast("Invalid email address");
            return false;
        }
    }

    private void findFriendToAdd(String email) {
        DatabaseReference usersList = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_collection));

        usersList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(user.getuEmail().equals(email)) {
                        try {
                            userFound = true;
                            usersList.child(user.getuId())
                                    .child(getString(R.string.requests_list_collection))
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(FirebaseAuth.getInstance().getUid());
                        }
                        catch (NullPointerException ex) {
                            Log.d("Error", ex.getMessage());
                        }

                    }
                }
                if(userFound) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "Request sent successfully!");
                    setResult(AddFriendActivity.RESULT_OK, returnIntent);
                    finish();
                    //makeToast("Request sent successfully!");
                }
                else {
                    makeToast("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
