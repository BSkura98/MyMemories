package com.bartlomiejskura.mymemories.fragment;

import android.app.Activity;
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

import com.bartlomiejskura.mymemories.AddFriendsActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.UserListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetFriendsTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YourFriendsFragment extends Fragment {
    private RecyclerView friendsRecyclerView;
    private Button addFriendButton;

    private UserListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_friends, container, false);

        findViews(view);
        setListeners();
        prepareViews();

        return view;
    }

    private void findViews(View view){
        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        addFriendButton = view.findViewById(R.id.addFriendButton);
    }

    private void setListeners(){
        addFriendButton.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddFriendsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void prepareViews(){
        //recycler view with friends
        new Thread(this::getFriends).start();
    }


    private void getFriends(){
        try{
            GetFriendsTask task = new GetFriendsTask(getActivity());
            User[] userArray = task.execute().get();
            if(userArray ==null){
                return;
            }
            final List<User> friends = new ArrayList<>(Arrays.asList(userArray));
            final Activity activity = getActivity();
            activity.runOnUiThread(() -> {
                adapter = new UserListAdapter(
                        getContext(),
                        friends,
                        activity,
                        true
                );
                friendsRecyclerView.setAdapter(adapter);
                friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
