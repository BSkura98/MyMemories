package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrGetTagTask extends AsyncTask<Void, Void, Tag> {
    private final MediaType JSON = MediaType.get("application/json");
    private Activity activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    private String category;

    public CreateOrGetTagTask(Activity activity, String category) {
        this.activity = activity;
        this.category = category;

        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Tag doInBackground(Void... voids) {
        Tag tag = new Tag(category, new User(sharedPreferences.getLong("userId", 0)));
        String json = gson.toJson(tag);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/tag")
                .post(requestBody)
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            return gson.fromJson(response.body().string(), Tag.class);
        } catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
            return null;
        }
    }
}
