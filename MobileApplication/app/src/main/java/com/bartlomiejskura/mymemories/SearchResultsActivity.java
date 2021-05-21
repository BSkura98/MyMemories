package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.SearchMemoriesTask;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView memoryList;
    private Toolbar toolbar;
    private ImageButton searchButton, backButton;
    private TextView keywordTextView, messageTextView;
    private CircularProgressIndicator searchResultsProgressIndicator;

    private MemoryListAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        findViews();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        memoryList = findViewById(R.id.memoryList);
        toolbar = findViewById(R.id.toolbarSearchResults);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        keywordTextView = findViewById(R.id.keywordTextView);
        searchResultsProgressIndicator = findViewById(R.id.searchResultsProgressIndicator);
        messageTextView = findViewById(R.id.noSearchResultsTextView);
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //keyword text view
        if(getIntent().getStringExtra("keyword").isEmpty()){
            keywordTextView.setVisibility(View.GONE);
        }else{
            keywordTextView.setText("Results for: "+getIntent().getStringExtra("keyword"));
        }

        //recycler view with memories
        new Thread(this::getMemories).start();

        //TextView with text "No search results"
        messageTextView.setVisibility(View.GONE);
    }

    private void setListeners(){
        searchButton.setOnClickListener(v -> finish());

        backButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @SuppressLint("SetTextI18n")
    public void getMemories(){
        try{
            SearchMemoriesTask task = new SearchMemoriesTask(this, getIntent().getStringExtra("keyword"));

            String memoryPriorities = getIntent().getStringExtra("memoryPriorities");
            String categories = getIntent().getStringExtra("categories");

            task.setCreationDateStart(getIntent().getStringExtra("creationDateStart"));
            task.setCreationDateEnd(getIntent().getStringExtra("creationDateEnd"));
            task.setDateStart(getIntent().getStringExtra("dateStart"));
            task.setDateEnd(getIntent().getStringExtra("dateEnd"));
            task.setMemoryPriorities(memoryPriorities!=null&&memoryPriorities.isEmpty()?null:memoryPriorities);
            task.setPublicToFriends(getIntent().getStringExtra("publicToFriends")==null?null:(getIntent().getStringExtra("publicToFriends").equals("true")));
            task.setSharedMemory(getIntent().getStringExtra("sharedMemories")==null?null:(getIntent().getStringExtra("sharedMemories").equals("true")));
            task.setHasImage(getIntent().getStringExtra("withImage")==null?null:(getIntent().getStringExtra("withImage").equals("true")));
            task.setCategories(categories!=null&&categories.isEmpty()?null:categories);
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                runOnUiThread(()->{
                    messageTextView.setVisibility(View.VISIBLE);
                    if(task.getError().contains("Unable to resolve host")){
                        messageTextView.setText("Problem with the Internet connection");
                    }else if(task.getError().contains("timeout")){
                        messageTextView.setText("Connection timed out");
                    }else{
                        messageTextView.setText("A problem occurred");
                    }
                    searchResultsProgressIndicator.setVisibility(View.GONE);
                });
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            final SearchResultsActivity activity = this;
            runOnUiThread(() -> {
                if(memories.isEmpty()){
                    messageTextView.setText("No search results");
                    messageTextView.setVisibility(View.VISIBLE);
                    searchResultsProgressIndicator.setVisibility(View.GONE);
                }else{
                    adapter = new MemoryListAdapter(
                            getApplicationContext(),
                            memories,
                            activity
                    );
                    searchResultsProgressIndicator.setVisibility(View.GONE);
                    memoryList.setAdapter(adapter);
                    memoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });
        }catch (Exception e){
            runOnUiThread(()->searchResultsProgressIndicator.setVisibility(View.GONE));
            e.printStackTrace();
        }
    }
}
