package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetFriendsMemoriesTask;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FriendsMemoriesFragment extends Fragment {
    private RecyclerView friendsMemoriesRecyclerView;
    private CircularProgressIndicator friendsMemoriesProgressIndicator;
    private TextView messageTextView;

    private MemoryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_memories, container, false);

        findViews(view);
        prepareViews();

        return view;
    }

    private void findViews(View view){
        friendsMemoriesRecyclerView = view.findViewById(R.id.friendsMemoriesRecyclerView);
        friendsMemoriesProgressIndicator = view.findViewById(R.id.friendsMemoriesProgressIndicator);
        messageTextView = view.findViewById(R.id.noFriendsMemoriesTextView);
    }

    private void prepareViews(){
        //recycler view with friends public memories
        new Thread(this::getMemories).start();

        //TextView with text "No memories"
        messageTextView.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void getMemories(){
        try{
            if(getActivity()==null){
                throw new NullPointerException();
            }
            GetFriendsMemoriesTask task = new GetFriendsMemoriesTask(getActivity());
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                getActivity().runOnUiThread(()->{
                    messageTextView.setVisibility(View.VISIBLE);
                    if(task.getError().contains("Unable to resolve host")){
                        messageTextView.setText("Problem with the Internet connection");
                    }else if(task.getError().contains("timeout")){
                        messageTextView.setText("Connection timed out");
                    }else{
                        messageTextView.setText("A problem occurred");
                    }
                    friendsMemoriesProgressIndicator.setVisibility(View.GONE);
                });
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            getActivity().runOnUiThread(() -> {
                if(memories.isEmpty()){
                    messageTextView.setText("No memories");
                    messageTextView.setVisibility(View.VISIBLE);
                    friendsMemoriesProgressIndicator.setVisibility(View.GONE);
                }else{
                    adapter = new MemoryListAdapter(
                            getContext(),
                            memories,
                            getActivity()
                    );
                    friendsMemoriesProgressIndicator.setVisibility(View.GONE);
                    friendsMemoriesRecyclerView.setAdapter(adapter);
                    friendsMemoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            });
        }catch (Exception e){
            if(getActivity()!=null){
                getActivity().runOnUiThread(()->friendsMemoriesProgressIndicator.setVisibility(View.GONE));
            }
            e.printStackTrace();
        }
    }
}
