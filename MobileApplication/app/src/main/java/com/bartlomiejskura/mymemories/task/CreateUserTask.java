package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateUserTask extends AsyncTask<String, Void, Boolean> {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private WeakReference<Activity> activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    public CreateUserTask(Activity activity){
        this.activity = new WeakReference<>(activity);
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        User user = new User(strings[0], strings[1], strings[2], strings[3], strings[4]);
        String json = gson.toJson(user);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/user")
                .post(requestBody)
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            User userResponse = gson.fromJson(response.body().string(), User.class);
            if(userResponse.getEmail() == null){
                return false;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("userId", userResponse.getId());
            editor.putString("email", userResponse.getEmail());
            editor.putString("firstName", userResponse.getFirstName());
            editor.putString("lastName", userResponse.getLastName());
            editor.putString("birthday", userResponse.getBirthday());
            editor.apply();
        } catch (Exception e) {
            System.out.println("ERROR in CreateUserTask: " + e.getMessage());
            return false;
        }
        return true;
    }
}
