package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesInCategoryTask;
import com.bartlomiejskura.mymemories.task.GetMemoriesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView memoryList;
    private TextView toolbarTextView;
    private Toolbar toolbar;
    private ImageButton backButton;

    private MemoryListAdapter adapter;
    private Long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        bindViews();
        categoryId = getIntent().getLongExtra("categoryId",0);

        initToolbar();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        new Thread(this::getAllMemories).start();
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText(getIntent().getStringExtra("category"));

        backButton.setOnClickListener(v -> super.onBackPressed());
    }

    private void bindViews(){
        memoryList = findViewById(R.id.memoryList);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backButton);
    }

    public void getAllMemories(){
        try{
            GetMemoriesInCategoryTask task = new GetMemoriesInCategoryTask(this, categoryId);
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            final CategoryActivity activity = this;
            runOnUiThread(() -> {
                adapter = new MemoryListAdapter(
                        getApplicationContext(),
                        memories,
                        activity
                );
                memoryList.setAdapter(adapter);
                memoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
