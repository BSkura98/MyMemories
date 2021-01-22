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
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetMemoriesForDateTask extends AsyncTask<Void, Void, Memory[]> {
    private Activity activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private Date date;

    public GetMemoriesForDateTask(Activity activity, Date date){
        this.activity = activity;
        this.date = date;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Memory[] doInBackground(Void... voids) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/memory/getAllForDate?userId="+sharedPreferences.getLong("userId", 0)+"&time="+sdf.format(date).replace(" ", "T"))
                .get()
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;
        Memory[] memories;

        try{
            response = httpClient.newCall(request).execute();
            JSONArray array = new JSONArray(response.body().string());
            memories = new Memory[array.length()];

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                memories[i] = gson.fromJson(object.toString(), Memory.class);
            }
            return memories;
        }catch (IOException | JSONException e){
            System.out.println("ERROR: " + e.getMessage());
        }

        return null;
    }
}
