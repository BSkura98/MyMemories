package com.bartlomiejskura.mymemories.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;

import java.util.ArrayList;
import java.util.List;

public class MemoryUtil {
    public static String getTaggedFriends(Memory memory, Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        if(memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            if(memory.getMemoryFriends()==null||memory.getMemoryFriends().isEmpty()){
                return "";
            }else{
                StringBuilder friendsText= new StringBuilder("with ");
                for(User user:memory.getMemoryFriends()){
                    friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                }
                friendsText.setLength(friendsText.length()-2);
                return friendsText.toString();
            }
        }else{
            if(memory.getIsPublicToFriends()){//jeśli wspomnienie jest oznaczone jako publiczne
                User memoryOwner= memory.getMemoryOwner();
                StringBuilder friendsText= new StringBuilder(memoryOwner.getFirstName()+" "+ memoryOwner.getLastName());
                if(memory.getMemoryFriends().size()>0){//jeśli wspomnienie jest wspólne, lecz znajomy nie oznaczył konkretnego użytkownika w nim
                    friendsText.append(" with ");
                    for(User user:memory.getMemoryFriends()){
                        friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                    }
                    friendsText.setLength(friendsText.length()-2);
                }
                return friendsText.toString();
            }else{//jeśli jest to wspólne wspomnienie
                User memoryOwner= memory.getMemoryOwner();
                StringBuilder friendsText= new StringBuilder(memoryOwner.getFirstName()+" "+ memoryOwner.getLastName() + " with you");

                List<User> memoryFriends = new ArrayList<>(memory.getMemoryFriends());
                for(User user:memoryFriends){
                    if(user.getId().equals(sharedPreferences.getLong("userId", 0))){
                        memoryFriends.remove(user);
                        break;
                    }
                }

                if(memoryFriends.size()>0){
                    friendsText.append(" and ");
                    for(User user:memoryFriends){
                        friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                    }
                    friendsText.setLength(friendsText.length()-2);
                }
                return friendsText.toString();
            }
        }
    }
}
