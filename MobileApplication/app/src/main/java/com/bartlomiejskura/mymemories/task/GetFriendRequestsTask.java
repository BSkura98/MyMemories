package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetFriendRequestsTask extends AsyncTask<Void, Void, User[]> {
    private Activity activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private boolean requestsSentByUser;

    public GetFriendRequestsTask(Activity activity, boolean requestsSentByUser){
        this.activity = activity;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        this.requestsSentByUser = requestsSentByUser;
    }

    @Override
    protected User[] doInBackground(Void... voids) {
        String url;
        if(requestsSentByUser){
            url = "https://mymemories-2.herokuapp.com/user/getFriendRequestsByUser?email="+sharedPreferences.getString("email", "");
        }else{
            url = "https://mymemories-2.herokuapp.com/user/getFriendRequests?email="+sharedPreferences.getString("email", "");
        }

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;
        User[] users;

        try{
            response = httpClient.newCall(request).execute();
            JSONArray array = new JSONArray(response.body().string());
            users = new User[array.length()];

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                users[i] = gson.fromJson(object.toString(), User.class);
            }
            return users;
        }catch (IOException | JSONException e){
            System.out.println("ERROR: " + e.getMessage());
        }

        return null;
    }
}
