package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesInCategoryTask;

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

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        memoryList = findViewById(R.id.memoryList);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backButton);
    }

    private void initValues(){
        categoryId = getIntent().getLongExtra("categoryId",0);
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText(getIntent().getStringExtra("category"));

        //priority spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //recycler view with memories
        new Thread(this::getMemories).start();
    }

    private void setListeners(){
        backButton.setOnClickListener(v -> super.onBackPressed());
    }


    public void getMemories(){
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
