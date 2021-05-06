package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;

public class AddFriendsActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private Toolbar toolbar;
    private TextView toolbarTextView, messageTextView;
    private ImageButton searchButton, backButton;
    private SearchView userSearchView;
    private CircularProgressIndicator addFriendsProgressIndicator;

    private UserListAdapter adapter;
    private Thread searchFriendsThread;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        findViews();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        toolbar = findViewById(R.id.toolbarSearchResults);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        userSearchView = findViewById(R.id.userSearchView);
        addFriendsProgressIndicator = findViewById(R.id.addFriendsProgressIndicator);
        messageTextView = findViewById(R.id.searchFriendsMessageTextView);
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Add friends");
        searchButton.setVisibility(View.GONE);

        //search view
        customizeSearchView(userSearchView);
        userSearchView.setIconifiedByDefault(false);

        //progress indicator
        addFriendsProgressIndicator.setVisibility(View.GONE);

        //TextView with message "No search results"
        messageTextView.setVisibility(View.GONE);
    }

    private void setListeners(){
        backButton.setOnClickListener(v -> onBackPressed());

        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(searchFriendsThread==null||!searchFriendsThread.isAlive()){
                    searchFriendsThread = new Thread(()->searchFriends(query));
                    searchFriendsThread.start();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @SuppressLint("SetTextI18n")
    private void searchFriends(String text){
        GetUsersWithoutFriendsTask task = new GetUsersWithoutFriendsTask(this,
                text);

        runOnUiThread(()-> {
            addFriendsProgressIndicator.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.GONE);
            usersRecyclerView.setAdapter(null);
        });
        try{
            User[] users = task.execute().get();
            if(users!=null){
                loadUsers(users);
            }else{
                runOnUiThread(()-> {
                    messageTextView.setVisibility(View.VISIBLE);
                    if(task.getError().contains("Unable to resolve host")){
                        messageTextView.setText("Problem with the Internet connection");
                    }else{
                        messageTextView.setText("A problem occurred");
                    }
                    addFriendsProgressIndicator.setVisibility(View.GONE);
                });
            }
        }catch (Exception e){
            runOnUiThread(()-> addFriendsProgressIndicator.setVisibility(View.GONE));
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadUsers(final User[] users){
        final Activity activity = this;
        runOnUiThread(() -> {
            if(users.length==0){
                messageTextView.setText("No search results");
                messageTextView.setVisibility(View.VISIBLE);
                addFriendsProgressIndicator.setVisibility(View.GONE);
            }else{
                adapter = new UserListAdapter(
                        getApplicationContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity,
                        false
                );
                addFriendsProgressIndicator.setVisibility(View.GONE);
                usersRecyclerView.setAdapter(adapter);
                usersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }

    private void customizeSearchView(SearchView searchView) {
        int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchBox = searchView.findViewById(searchTextId);
        searchBox.setBackgroundColor(Color.WHITE);
        searchBox.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
        int search_plateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View mSearchPlate = searchView.findViewById(search_plateId);
        mSearchPlate.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        int searchCloseImageId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView searchClose = searchView.findViewById(searchCloseImageId);// change color
        searchClose.setBackgroundColor(Color.WHITE);
    }
}
