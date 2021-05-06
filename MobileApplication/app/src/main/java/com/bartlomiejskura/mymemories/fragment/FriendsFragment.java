package com.bartlomiejskura.mymemories.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bartlomiejskura.mymemories.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FriendsFragment extends Fragment {
    private BottomNavigationView navigationView;

    private FriendsMemoriesFragment friendsMemoriesFragment;
    private YourFriendsFragment yourFriendsFragment;
    private FriendRequestsFragment friendRequestsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        findViews(view);
        initNavigationView();

        return view;
    }

    private void findViews(View view){
        navigationView = view.findViewById(R.id.bottomNavigation);
    }

    private void initNavigationView(){
        if(getActivity()==null){
            return;
        }

        friendsMemoriesFragment = new FriendsMemoriesFragment();
        yourFriendsFragment = new YourFriendsFragment();
        friendRequestsFragment = new FriendRequestsFragment();

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.friendsFragmentContainer, friendsMemoriesFragment).commit();

        navigationView.setSelectedItemId(R.id.friendsMemories);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.friendsMemories:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.friendsFragmentContainer, friendsMemoriesFragment).commit();
                    return true;
                case R.id.yourFriends:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.friendsFragmentContainer, yourFriendsFragment).commit();
                    return true;
                case R.id.friendRequests:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.friendsFragmentContainer, friendRequestsFragment).commit();
                    return true;
            }
            return false;
        });
    }
}
