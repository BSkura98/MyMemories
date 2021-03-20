package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.model.AuthenticationRequest;
import com.bartlomiejskura.mymemories.model.AuthenticationResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticationTask extends AsyncTask<Void, Void, Boolean> {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Activity activity;
    private String email, password;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    public AuthenticationTask(Activity activity, String email, String password){
        this.activity = activity;
        this.email = email;
        this.password = password;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
        String json = gson.toJson(authenticationRequest);
        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/login")
                .post(requestBody)
                .build();
        Response response;

        try{
            response = httpClient.newCall(request).execute();
            String jwt = response.header("Authorization");
            if(jwt==null){
                activity.runOnUiThread(() -> Toast.makeText(activity.getBaseContext(), "Error: jwt is null", Toast.LENGTH_LONG).show());
                return false;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", jwt.substring(7));
            editor.apply();
            return true;
        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }
}