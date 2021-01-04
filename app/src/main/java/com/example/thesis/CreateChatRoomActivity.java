package com.example.thesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thesis.DatabaseModels.User;
import com.example.thesis.Fragments.ChatListFragment;
import com.example.thesis.Fragments.MapFragment;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.Utility.Adapters.ParticipantsRecyclerViewAdapter;
import com.example.thesis.ViewModels.FriendListViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CreateChatRoomActivity extends AppCompatActivity {

    private MaterialButton createChatBtn;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        ArrayList<User> friendList = (ArrayList<User>) intent.getSerializableExtra("FriendList");

        initView(friendList);
    }

    private void initView(ArrayList<User> friendList) {
        createChatBtn = findViewById(R.id.createChatBtn);
        recyclerView = findViewById(R.id.chatParticipants);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ParticipantsRecyclerViewAdapter adapter = new ParticipantsRecyclerViewAdapter(this, friendList);
        recyclerView.setAdapter(adapter);

        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectedUsers", adapter.getSelectedUsers());
                returnIntent.putExtras(bundle);

                if(adapter.getSelectedUsers().size() > 0) {
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                else {
                    Toast.makeText(getApplication(), "Please select users to chat", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
