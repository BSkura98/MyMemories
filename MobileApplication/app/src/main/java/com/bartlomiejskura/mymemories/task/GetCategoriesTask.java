package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.Category;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetCategoriesTask extends AsyncTask<Void, Void, Category[]> {
    private Activity activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private String error = "";

    public GetCategoriesTask(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Category[] doInBackground(Void... voids) {
        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/category/getAll?email="+sharedPreferences.getString("email", ""))
                .get()
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;
        Category[] categories;

        try{
            response = httpClient.newCall(request).execute();
            JSONArray array = new JSONArray(response.body().string());
            categories = new Category[array.length()];

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                categories[i] = gson.fromJson(object.toString(), Category.class);
            }
            return categories;
        }catch (IOException | JSONException e){
            error = e.getMessage();
            System.out.println("ERROR: " + e.getMessage());
        }

        return null;
    }

    public String getError(){
        return error;
    }
}
