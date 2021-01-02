package com.bartlomiejskura.mymemories.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            Collections.sort(memories, new Comparator<Memory>() {
                @Override
                public int compare(Memory memory1, Memory memory2) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddd hh:mm:ss");
                    try {
                        Date strDate1 = sdf.parse(memory1.getDate().replace("T"," "));
                        Date strDate2 = sdf.parse(memory2.getDate().replace("T"," "));
                        return strDate1.before(strDate2)?1:-1;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
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
