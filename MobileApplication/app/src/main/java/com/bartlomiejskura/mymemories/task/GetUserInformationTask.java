package com.bartlomiejskura.mymemories.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import com.bartlomiejskura.mymemories.LoginActivity;
import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserInformationTask extends AsyncTask<String, Void, Boolean> {
    private WeakReference<LoginActivity> activityReference;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private String error = "";

    public GetUserInformationTask(LoginActivity activityReference){
        this.activityReference = new WeakReference<>(activityReference);
        sharedPreferences = activityReference.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String email = strings[0];

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/user?email="+email)
                .get()
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;
        User user;

        try{
            response = httpClient.newCall(request).execute();
            user = gson.fromJson(response.body().string(), User.class);
        }catch (IOException e){
            error = e.getMessage();
            System.out.println("ERROR in GetUserInformationTask: " + e.getMessage());
            activityReference.get().showSnackbar("A problem occurred while getting user information. Please try again.");
            return false;
        }
        if(user==null||user.getEmail()==null){
            System.out.println("ERROR in GetUserInformationTask: user or user email is null");
            activityReference.get().showSnackbar("A problem occurred while getting user information. Please try again.");
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("birthday", user.getBirthday());
        editor.putString("avatarUrl", user.getAvatarUrl());
        editor.putString("friends", gson.toJson(user.getFriends()));
        List<Long> friendRequestsIds = new ArrayList<>();
        for(User friendRequest:user.getFriendRequests()){
            friendRequestsIds.add(friendRequest.getId());
        }
        editor.putString("friendRequestsIds", gson.toJson(friendRequestsIds));
        editor.apply();
        return  true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if(!aBoolean){
            activityReference.get().setLoginProgressIndicatorVisibility(View.GONE);
        }
    }

    public String getError(){
        return error;
    }
}