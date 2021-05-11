package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.Category;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditCategoryTask extends AsyncTask<Void, Void, Boolean> {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Category category;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private String error = "";

    public EditCategoryTask(Activity activity, Category category){
        this.category = category;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String json = gson.toJson(category);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/category")
                .put(requestBody)
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            if(response.code()==409){
                error="409";
                return false;
            }

            Category categoryResponse = gson.fromJson(response.body().string(), Category.class);
            if(category == null||categoryResponse==null){
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
