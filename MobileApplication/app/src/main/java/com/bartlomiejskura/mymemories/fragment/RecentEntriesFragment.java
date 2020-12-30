package com.bartlomiejskura.mymemories.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.AddMemoryActivity;
import com.bartlomiejskura.mymemories.LoginActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecentEntriesFragment extends Fragment {
    private RecyclerView memoryList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_entries, container, false);

        FloatingActionButton addMemoryButton = view.findViewById(R.id.addMemoryButton);
        memoryList = view.findViewById(R.id.memoryList);

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
            Memory[] memories = task.execute().get();
            if(memories ==null){
                return;
            }
            MemoryListAdapter adapter = new MemoryListAdapter(
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
}
