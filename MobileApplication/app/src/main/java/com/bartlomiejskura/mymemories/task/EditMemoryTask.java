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

public class EditMemoryTask extends AsyncTask<Void, Void, Boolean> {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Activity activity;
    private Memory memory;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    public EditMemoryTask(Activity activity, Memory memory){
        this.activity = activity;
        this.memory = memory;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String json = gson.toJson(memory);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/memory")
                .put(requestBody)
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            Memory memoryResponse = gson.fromJson(response.body().string(), Memory.class);
            if(memory==null||memoryResponse==null){
                return false;
            }
        } catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
            return false;
        }
        return true;
    }
}
