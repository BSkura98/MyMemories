package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesInCategoryTask;
import com.bartlomiejskura.mymemories.task.GetMemoriesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView memoryList;
    private MemoryListAdapter adapter;
    private Spinner prioritySpinner;
    private Long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        memoryList = findViewById(R.id.memoryList);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        categoryId = getIntent().getLongExtra("categoryId",0);

        TextView toolbarTextView = findViewById(R.id.toolbarTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText(getIntent().getStringExtra("category"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        getAllMemories();
    }

    public void getAllMemories(){
        try{
            GetMemoriesInCategoryTask task = new GetMemoriesInCategoryTask(this, categoryId);
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            adapter = new MemoryListAdapter(
                    this,
                    memories,
                    this
            );
            memoryList.setAdapter(adapter);
            memoryList.setLayoutManager(new LinearLayoutManager(this));

            prioritySpinner.setOnItemSelectedListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        if(selected.equals("High")){
            adapter.setMemoryPriority(90);
        }else if(selected.equals("Medium")){
            adapter.setMemoryPriority(50);
        }else if(selected.equals("Low")){
            adapter.setMemoryPriority(10);
        }else if(selected.equals("All")){
            adapter.setMemoryPriority(0);
        }
        memoryList.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
