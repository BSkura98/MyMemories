package com.bartlomiejskura.mymemories.utils;

import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static void logout(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.remove("email");
        editor.remove("firstName");
        editor.remove("lastName");
        editor.remove("birthday");
        editor.remove("token");
        editor.remove("avatarUrl");
        editor.remove("friends");
        editor.remove("friendRequestsIds");
        editor.apply();
    }
}
