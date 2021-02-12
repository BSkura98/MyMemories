package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

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
}
