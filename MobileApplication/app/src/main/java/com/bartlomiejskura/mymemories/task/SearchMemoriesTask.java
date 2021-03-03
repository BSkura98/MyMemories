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

public class SearchMemoriesTask extends AsyncTask<Void, Void, Memory[]> {
    private Activity activity;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    private String keyword;
    private Boolean hasImage = null;
    private Boolean publicToFriends = null;
    private Boolean isSharedMemory = null;
    private String creationDateStart = null;
    private String creationDateEnd = null;
    private String dateStart = null;
    private String dateEnd = null;
    private String memoryPriorities = null;
    private String categories = null;

    public SearchMemoriesTask(Activity activity, String keyword){
        this.activity = activity;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        this.keyword = keyword.replace(" ", "_");
    }

    @Override
    protected Memory[] doInBackground(Void... voids) {
        Request request = new Request.Builder()
                .url(createUrl())
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

    private String createUrl(){
        String url = "https://mymemories-2.herokuapp.com/memory/search?userId="+sharedPreferences.getLong("userId", 0);
        if(keyword!=null){
            url += "&keyword="+ keyword;
        }
        if(creationDateStart!=null){
            url += "&creationDateStart="+ creationDateStart;
        }
        if(creationDateEnd!=null){
            url += "&creationDateEnd="+ creationDateEnd;
        }
        if(dateStart!=null){
            url += "&dateStart="+ dateStart;
        }
        if(dateEnd!=null){
            url += "&dateEnd="+ dateEnd;
        }
        if(hasImage!=null){
            url += "&hasImage=" + hasImage;
        }
        if(memoryPriorities!=null){
            url += "&memoryPriorities=" + memoryPriorities;
        }
        if(publicToFriends!=null){
            url += "&publicToFriends=" + publicToFriends;
        }
        if(isSharedMemory!=null){
            url += "&isSharedMemory=" + isSharedMemory;
        }
        if(categories!=null){
            url += "&categories=" + categories;
        }

        return url;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public void setPublicToFriends(Boolean publicToFriends) {
        this.publicToFriends = publicToFriends;
    }

    public void setSharedMemory(Boolean sharedMemory) {
        isSharedMemory = sharedMemory;
    }

    public void setCreationDateStart(String creationDateStart) {
        this.creationDateStart = creationDateStart;
    }

    public void setCreationDateEnd(String creationDateEnd) {
        this.creationDateEnd = creationDateEnd;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setMemoryPriorities(String memoryPriorities) {
        this.memoryPriorities = memoryPriorities;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
