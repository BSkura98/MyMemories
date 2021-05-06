package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoveFriendRequestTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private String user1Email;
    private String user2Email;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private String error = "";

    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public RemoveFriendRequestTask(Activity activity, String user1Email, String user2Email){
        this.activity = activity;
        this.user1Email = user1Email;
        this.user2Email = user2Email;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        RequestBody requestBody = RequestBody.create(JSON, "");

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/user/removeFriendRequest?user1Email="+ user1Email +"&user2Email="+ user2Email)
                .put(requestBody)
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            User userResponse = gson.fromJson(response.body().string(), User.class);
            if(userResponse==null){
                return false;
            }
        } catch (Exception e) {
            error = e.getMessage();
            System.out.println("ERROR:" + e.getMessage());
            return false;
        }
        return true;
    }

    public String getError(){
        return error;
    }
}
