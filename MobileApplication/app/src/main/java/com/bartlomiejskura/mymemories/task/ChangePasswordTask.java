package com.bartlomiejskura.mymemories.task;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private String oldPassword, newPassword;
    private String error = "";

    public ChangePasswordTask(String oldPassword, String newPassword, SharedPreferences sharedPreferences){
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        RequestBody requestBody = RequestBody.create(null, new byte[0]);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/user/changePassword?email="+sharedPreferences.getString("email", "")+"&oldPassword="+oldPassword
                        +"&newPassword="+newPassword)
                .put(requestBody)
                .addHeader("Authorization", "Bearer "+sharedPreferences.getString("token", null))
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            User userResponse = gson.fromJson(response.body().string(), User.class);
            if(userResponse.getEmail() == null){
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
