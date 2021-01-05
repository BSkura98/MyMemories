package com.bartlomiejskura.mymemories.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.AddMemoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecentEntriesFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private RecyclerView memoryList;
    private MemoryListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_entries, container, false);

        FloatingActionButton addMemoryButton = view.findViewById(R.id.addMemoryButton);
        memoryList = view.findViewById(R.id.memoryList);
        final Spinner prioritySpinner = view.findViewById(R.id.prioritySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.priorities_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(this);

        addMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddMemoryActivity.class));
            }
        });

        getAllMemories();

        return view;
    }

    public void getAllMemories(){
        try{
            GetMemoriesTask task = new GetMemoriesTask(getActivity());
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            adapter = new MemoryListAdapter(
                    getContext(),
                    memories,
                    getActivity()
            );
            memoryList.setAdapter(adapter);
            memoryList.setLayoutManager(new LinearLayoutManager(getContext()));
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
