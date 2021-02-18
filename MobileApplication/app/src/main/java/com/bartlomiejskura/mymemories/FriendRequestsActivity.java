package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.FriendRequestListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetFriendRequestsTask;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendRequestsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private FriendRequestListAdapter adapter;
    private RecyclerView requestsByOthers, requestsByUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        requestsByOthers = findViewById(R.id.friendRequests);
        requestsByUser = findViewById(R.id.friendRequests2);

        Toolbar toolbar = findViewById(R.id.toolbarSearchResults);
        TextView toolbarTextView = findViewById(R.id.toolbarTextView);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton backButton = findViewById(R.id.backButton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Friend requests");
        searchButton.setVisibility(View.GONE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FriendsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFriendRequestsByOthers();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFriendRequestsByUser();
            }
        }).start();
    }

    private void getFriendRequestsByOthers(){
        GetFriendRequestsTask task = new GetFriendRequestsTask(this, false);
        try{
            User[] users = task.execute().get();
            if(users!=null){
                showFriendRequestsByOthers(users);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getFriendRequestsByUser(){
        GetFriendRequestsTask task = new GetFriendRequestsTask(this, true);
        try{
            User[] users = task.execute().get();
            if(users!=null){
                showFriendRequestsByUser(users);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showFriendRequestsByOthers(final User[] users){
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new FriendRequestListAdapter(
                        getApplicationContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity,
                        true
                );
                requestsByOthers.setAdapter(adapter);
                requestsByOthers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }

    private void showFriendRequestsByUser(final User[] users){
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new FriendRequestListAdapter(
                        getApplicationContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity,
                        false
                );
                requestsByUser.setAdapter(adapter);
                requestsByUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), FriendsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
