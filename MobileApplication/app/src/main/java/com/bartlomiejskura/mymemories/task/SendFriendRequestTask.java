package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendFriendRequestTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private Long user1Id, user2Id;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public SendFriendRequestTask(Activity activity, Long user1Id, Long user2Id){
        this.activity = activity;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        RequestBody requestBody = RequestBody.create(JSON, "");

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/sendFriendRequest?user1Id="+user1Id+"&user2Id="+user2Id)
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
            System.out.println("ERROR:" + e.getMessage());
            return false;
        }
        return true;
    }
}
