package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SearchView;

import com.bartlomiejskura.mymemories.adapter.UserListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetUsersWithoutFriendsTask;

import java.util.ArrayList;
import java.util.Arrays;

public class AddFriendsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private UserListAdapter adapter;
    private RecyclerView usersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        usersRecyclerView = findViewById(R.id.usersRecyclerView);

        SearchView userSearchView = findViewById(R.id.userSearchView);
        userSearchView.setIconifiedByDefault(false);
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriends(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchFriends(String text){
        GetUsersWithoutFriendsTask task = new GetUsersWithoutFriendsTask(this,
                text);
        try{
            User[] users = task.execute().get();
            if(users!=null){
                loadUsers(users);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadUsers(final User[] users){
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new UserListAdapter(
                        getApplicationContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity
                );
                usersRecyclerView.setAdapter(adapter);
                usersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }
}
