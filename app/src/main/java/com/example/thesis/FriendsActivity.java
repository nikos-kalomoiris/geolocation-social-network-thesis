package com.example.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.Utility.Adapters.FragmentPagerAdapter.FriendsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager2 friendViewPager = findViewById(R.id.friendsPager);
        friendViewPager.setAdapter(new FriendsPagerAdapter(this));

        TabLayout friendsTab = findViewById(R.id.friendsTab);
        TabLayoutMediator tabLayoutMediator  =  new TabLayoutMediator(
                friendsTab, friendViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.friend_list_tab);
                        tab.setIcon(R.drawable.ic_friend_list);
                        break;
                    case 1:
                        tab.setText(R.string.requests_tab);
                        tab.setIcon(R.drawable.ic_friend_requests);
                        break;
                }
            }
        }
        );
        tabLayoutMediator.attach();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
