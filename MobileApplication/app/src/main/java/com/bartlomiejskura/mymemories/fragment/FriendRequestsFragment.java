package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
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
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendRequestsFragment extends Fragment {
    private RecyclerView requestsByOthers, requestsByUser;
    private TextView requestsByOthersTextView, requestsByUserTextView, messageTextView;
    private CircularProgressIndicator friendRequestsProgressIndicator;

    private boolean noFriendRequests;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        findViews(view);
        initValues();
        prepareViews();

        return view;
    }

    private void findViews(View view) {
        requestsByUser = view.findViewById(R.id.friendRequests2);
        requestsByOthers = view.findViewById(R.id.friendRequests);
        requestsByUserTextView = view.findViewById(R.id.textView17);
        requestsByOthersTextView = view.findViewById(R.id.textView16);
        friendRequestsProgressIndicator = view.findViewById(R.id.friendRequestsProgressIndicator);
        messageTextView = view.findViewById(R.id.noFriendRequestsTextView);
    }

    private void initValues(){
        noFriendRequests = false;
    }

    private void prepareViews(){
        //friend requests headers (unnecessary until friend requests are loaded)
        requestsByUserTextView.setVisibility(View.GONE);
        requestsByOthersTextView.setVisibility(View.GONE);

        //recycler views with friend requests
        new Thread(this::getFriendRequestsByOthers).start();
        new Thread(this::getFriendRequestsByUser).start();

        //TextView with text "No friend requests"
        messageTextView.setVisibility(View.GONE);
    }


    @SuppressLint("SetTextI18n")
    private void getFriendRequestsByOthers(){
        try{
            if(getActivity()==null){
                throw new NullPointerException();
            }
            GetFriendRequestsTask task = new GetFriendRequestsTask(getActivity(), false);
            User[] users = task.execute().get();
            if(users!=null&&users.length!=0){
                showFriendRequestsByOthers(users);
            }else{
                if(task.getError().isEmpty()){
                    setNoFriendRequests();
                }else{
                    getActivity().runOnUiThread(()->{
                        if(task.getError().contains("Unable to resolve host")){
                            messageTextView.setText("Problem with the Internet connection");
                        }else if(task.getError().contains("timeout")){
                            messageTextView.setText("Connection timed out");
                        }else{
                            messageTextView.setText("A problem occurred");
                        }
                        messageTextView.setVisibility(View.VISIBLE);
                        friendRequestsProgressIndicator.setVisibility(View.GONE);
                        requestsByOthers.setVisibility(View.GONE);
                        requestsByUser.setVisibility(View.GONE);
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getFriendRequestsByUser(){
        try{
            if(getActivity()==null){
                throw new NullPointerException();
            }
            GetFriendRequestsTask task = new GetFriendRequestsTask(getActivity(), true);
            User[] users = task.execute().get();
            if(users!=null&&users.length!=0){
                showFriendRequestsByUser(users);
            }else{
                if(task.getError().isEmpty()){
                    setNoFriendRequests();
                }else{
                    getActivity().runOnUiThread(()->{
                        if(task.getError().contains("Unable to resolve host")){
                            messageTextView.setText("Problem with the Internet connection");
                        }else if(task.getError().contains("timeout")){
                            messageTextView.setText("Connection timed out");
                        }else{
                            messageTextView.setText("A problem occurred");
                        }
                        friendRequestsProgressIndicator.setVisibility(View.GONE);
                        requestsByOthers.setVisibility(View.GONE);
                        requestsByUser.setVisibility(View.GONE);
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showFriendRequestsByOthers(final User[] users){
        final Activity activity = getActivity();
        if(activity!=null){
            activity.runOnUiThread(() -> {
                requestsByOthersTextView.setVisibility(View.VISIBLE);
                FriendRequestListAdapter adapter = new FriendRequestListAdapter(
                        getContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity,
                        true,
                        requestsByOthersTextView
                );
                friendRequestsProgressIndicator.setVisibility(View.GONE);
                requestsByOthers.setAdapter(adapter);
                requestsByOthers.setLayoutManager(new LinearLayoutManager(getContext()));
                requestsByOthers.setNestedScrollingEnabled(false);
            });
        }
    }

    private void showFriendRequestsByUser(final User[] users){
        final Activity activity = getActivity();
        if(activity!=null){
            activity.runOnUiThread(() -> {
                requestsByUserTextView.setVisibility(View.VISIBLE);
                FriendRequestListAdapter adapter = new FriendRequestListAdapter(
                        getContext(),
                        new ArrayList<>(Arrays.asList(users)),
                        activity,
                        false,
                        requestsByUserTextView
                );
                friendRequestsProgressIndicator.setVisibility(View.GONE);
                requestsByUser.setAdapter(adapter);
                requestsByUser.setLayoutManager(new LinearLayoutManager(getContext()));
                requestsByUser.setNestedScrollingEnabled(false);
            });
        }
    }

    @SuppressLint("SetTextI18n")
    synchronized private void setNoFriendRequests(){
        if(noFriendRequests){
            if(getActivity()!=null){
                getActivity().runOnUiThread(()->{
                    messageTextView.setText("No friend requests");
                    messageTextView.setVisibility(View.VISIBLE);
                    friendRequestsProgressIndicator.setVisibility(View.GONE);
                });
            }
        }else{
            noFriendRequests = true;
        }
    }
}
