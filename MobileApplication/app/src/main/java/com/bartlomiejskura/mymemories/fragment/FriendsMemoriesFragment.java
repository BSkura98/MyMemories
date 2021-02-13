package com.bartlomiejskura.mymemories.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.FriendsActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetFriendsMemoriesTask;
import com.bartlomiejskura.mymemories.task.GetMemoriesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FriendsMemoriesFragment extends Fragment {
    private RecyclerView friendsMemoriesRecyclerView;
    private MemoryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_memories, container, false);

        friendsMemoriesRecyclerView = view.findViewById(R.id.friendsMemoriesRecyclerView);

        Button showFriendsButton = view.findViewById(R.id.showFriendsButton);
        showFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FriendsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(i);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getMemories();
            }
        }).start();

        return view;
    }

    private void getMemories(){
        try{
            GetFriendsMemoriesTask task = new GetFriendsMemoriesTask(getActivity());
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new MemoryListAdapter(
                            getContext(),
                            memories,
                            getActivity()
                    );
                    friendsMemoriesRecyclerView.setAdapter(adapter);
                    friendsMemoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
