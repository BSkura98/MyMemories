package com.bartlomiejskura.mymemories.fragment;

import android.app.Activity;
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
import com.bartlomiejskura.mymemories.adapter.FriendRequestListAdapter;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.GetFriendRequestsTask;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendRequestsFragment extends Fragment {
    private RecyclerView requestsByOthers, requestsByUser;
    private TextView requestsByOthersTextView, requestsByUserTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        bindViews(view);

        requestsByUserTextView.setVisibility(View.GONE);
        requestsByOthersTextView.setVisibility(View.GONE);

        new Thread(this::getFriendRequestsByOthers).start();
        new Thread(this::getFriendRequestsByUser).start();

        return view;
    }

    private void bindViews(View view) {
        requestsByUser = view.findViewById(R.id.friendRequests2);
        requestsByOthers = view.findViewById(R.id.friendRequests);
        requestsByUserTextView = view.findViewById(R.id.textView17);
        requestsByOthersTextView = view.findViewById(R.id.textView16);
    }

    private void getFriendRequestsByOthers(){
        GetFriendRequestsTask task = new GetFriendRequestsTask(getActivity(), false);
        try{
            User[] users = task.execute().get();
            if(users!=null&&users.length!=0){
                showFriendRequestsByOthers(users);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getFriendRequestsByUser(){
        GetFriendRequestsTask task = new GetFriendRequestsTask(getActivity(), true);
        try{
            User[] users = task.execute().get();
            if(users!=null){
                showFriendRequestsByUser(users);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showFriendRequestsByOthers(final User[] users){
        final Activity activity = getActivity();
        activity.runOnUiThread(() -> {
            requestsByOthersTextView.setVisibility(View.VISIBLE);
            FriendRequestListAdapter adapter = new FriendRequestListAdapter(
                    getContext(),
                    new ArrayList<>(Arrays.asList(users)),
                    activity,
                    true
            );
            requestsByOthers.setAdapter(adapter);
            requestsByOthers.setLayoutManager(new LinearLayoutManager(getContext()));
            requestsByOthers.setNestedScrollingEnabled(false);
        });
    }

    private void showFriendRequestsByUser(final User[] users){
        final Activity activity = getActivity();
        activity.runOnUiThread(() -> {
            requestsByUserTextView.setVisibility(View.VISIBLE);
            FriendRequestListAdapter adapter = new FriendRequestListAdapter(
                    getContext(),
                    new ArrayList<>(Arrays.asList(users)),
                    activity,
                    false
            );
            requestsByUser.setAdapter(adapter);
            requestsByUser.setLayoutManager(new LinearLayoutManager(getContext()));
            requestsByUser.setNestedScrollingEnabled(false);
        });
    }
}
