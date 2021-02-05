package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesInCategoryTask;
import com.bartlomiejskura.mymemories.task.SearchMemoriesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView memoryList;
    private MemoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        memoryList = findViewById(R.id.memoryList);

        Toolbar toolbar = findViewById(R.id.toolbarSearchResults);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        TextView keywordTextView = findViewById(R.id.keywordTextView);
        keywordTextView.setText(getIntent().getStringExtra("query"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllMemories();
            }
        }).start();
    }

    public void getAllMemories(){
        try{
            SearchMemoriesTask task = new SearchMemoriesTask(this, getIntent().getStringExtra("query"));
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            final SearchResultsActivity activity = this;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new MemoryListAdapter(
                            getApplicationContext(),
                            memories,
                            activity
                    );
                    memoryList.setAdapter(adapter);
                    memoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
