package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.UserListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetUsersWithoutFriendsTask;

import java.util.ArrayList;
import java.util.Arrays;

public class AddFriendsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private UserListAdapter adapter;
    private RecyclerView usersRecyclerView;
    private Activity activity = this;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        usersRecyclerView = findViewById(R.id.usersRecyclerView);

        Toolbar toolbar = findViewById(R.id.toolbarSearchResults);
        TextView toolbarTextView = findViewById(R.id.toolbarTextView);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton backButton = findViewById(R.id.backButton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Add friends");
        searchButton.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> activity.onBackPressed());

        SearchView userSearchView = findViewById(R.id.userSearchView);
        customizeSearchView(userSearchView);
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
                        activity,
                        false
                );
                usersRecyclerView.setAdapter(adapter);
                usersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void customizeSearchView(SearchView searchView) {
        int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchBox = ((EditText) searchView.findViewById(searchTextId));
        searchBox.setBackgroundColor(Color.WHITE);
        searchBox.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
        int search_plateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View mSearchPlate = ((View) searchView.findViewById(search_plateId));
        mSearchPlate.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        int searchCloseImageId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView searchClose = ((ImageView) searchView.findViewById(searchCloseImageId));// change color
        searchClose.setBackgroundColor(Color.WHITE);
    }
}
