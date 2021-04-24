package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        Toolbar toolbar = findViewById(R.id.toolbarSearchResults);
        TextView toolbarTextView = findViewById(R.id.toolbarTextView);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton backButton = findViewById(R.id.backButton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Friends");
        searchButton.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("fragmentToLoad", "friendsMemoriesFragment");
            startActivity(i);
        });

        Button addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AddFriendsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        Button friendRequestsButton = findViewById(R.id.friendRequestsButton);
        friendRequestsButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), FriendRequestsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        new Thread(this::getFriends).start();
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
            runOnUiThread(() -> {
                adapter = new UserListAdapter(
                        getApplicationContext(),
                        friends,
                        activity,
                        true
                );
                friendsRecyclerView.setAdapter(adapter);
                friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("fragmentToLoad", "friendsMemoriesFragment");
        startActivity(i);
    }
}
