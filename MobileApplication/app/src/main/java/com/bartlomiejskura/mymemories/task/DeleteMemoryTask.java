package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.Memory;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteMemoryTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private Long memoryId;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();

    public DeleteMemoryTask(Activity activity, Long memoryId) {
        this.activity = activity;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        this.memoryId = memoryId;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/memory?memoryId="+memoryId)
                .delete()
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;

        try{
            httpClient.newCall(request).execute();
            return true;
        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
        }
        return false;
    }
}
