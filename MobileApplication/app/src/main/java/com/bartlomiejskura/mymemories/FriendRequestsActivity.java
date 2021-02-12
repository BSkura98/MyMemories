package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bartlomiejskura.mymemories.adapter.FriendRequestListAdapter;
import com.bartlomiejskura.mymemories.adapter.UserListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetFriendRequestsTask;
import com.bartlomiejskura.mymemories.task.GetUsersWithoutFriendsTask;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendRequestsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private FriendRequestListAdapter adapter;
    private RecyclerView requestsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        requestsRecyclerView = findViewById(R.id.friendRequests);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFriendRequests();
            }
        }).start();
    }

    private void getFriendRequests(){
        GetFriendRequestsTask task = new GetFriendRequestsTask(this);
        try{
            User[] users = task.execute().get();
            if(users!=null){
                showFriendRequests(users);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showFriendRequests(final User[] users){
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new FriendRequestListAdapter(
                        getApplicationContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity
                );
                requestsRecyclerView.setAdapter(adapter);
                requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }
}
