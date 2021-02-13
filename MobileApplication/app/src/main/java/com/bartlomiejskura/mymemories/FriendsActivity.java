package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bartlomiejskura.mymemories.adapter.UserListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetFriendsTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    private RecyclerView friendsRecyclerView;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);

        Button addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddFriendsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        Button friendRequestsButton = findViewById(R.id.friendRequestsButton);
        friendRequestsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FriendRequestsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFriends();
            }
        }).start();
    }

    private void getFriends(){
        try{
            GetFriendsTask task = new GetFriendsTask(this);
            User[] userArray = task.execute().get();
            if(userArray ==null){
                return;
            }
            final List<User> friends = new ArrayList<>(Arrays.asList(userArray));
            final Activity activity = this;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new UserListAdapter(
                            getApplicationContext(),
                            friends,
                            activity,
                            true
                    );
                    friendsRecyclerView.setAdapter(adapter);
                    friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
