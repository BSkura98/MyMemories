package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserInformationTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private String email;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    public GetUserInformationTask(Activity activity, String email){
        this.activity = activity;
        this.email = email;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/user/getByEmail?email="+email)
                .get()
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;
        User user;

        try{
            response = httpClient.newCall(request).execute();
            user = gson.fromJson(response.body().string(), User.class);
        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
        if(user==null||user.getEmail()==null){
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("birthday", user.getBirthday());
        editor.putString("avatarUrl", user.getAvatarUrl());
        editor.apply();
        return  true;
    }
}