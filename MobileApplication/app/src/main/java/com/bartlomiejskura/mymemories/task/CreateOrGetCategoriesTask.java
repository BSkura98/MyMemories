package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrGetCategoriesTask extends AsyncTask<Void, Void, Category[]> {
    private final MediaType JSON = MediaType.get("application/json");
    private Activity activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    private List<String> categoriesNames;

    public CreateOrGetCategoriesTask(Activity activity, List<String> categoriesNames) {
        this.activity = activity;
        this.categoriesNames = categoriesNames;

        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Category[] doInBackground(Void... voids) {
        List<Category> categories = new ArrayList<>();

        for(int i=0;i<categoriesNames.size();i++){
            categories.add(new Category(categoriesNames.get(i), new User(sharedPreferences.getLong("userId", 0))));
        }
        String json = gson.toJson(categories);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/category/addCategories")
                .post(requestBody)
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;
        Category[] responseArray;

        try {
            response = httpClient.newCall(request).execute();
            JSONArray array = new JSONArray(response.body().string());
            responseArray = new Category[array.length()];

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                responseArray[i] = gson.fromJson(object.toString(), Category.class);
            }
            return responseArray;
        } catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
            return null;
        }
    }
}
