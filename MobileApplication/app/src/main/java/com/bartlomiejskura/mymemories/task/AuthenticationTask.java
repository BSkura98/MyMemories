package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import com.bartlomiejskura.mymemories.LoginActivity;
import com.bartlomiejskura.mymemories.model.AuthenticationRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticationTask extends AsyncTask<String, Void, Boolean> {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private WeakReference<Activity> activityReference;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private String error = "";

    public AuthenticationTask(Activity activity){
        this.activityReference = new WeakReference<>(activity);
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(activityReference.get() instanceof LoginActivity){
            ((LoginActivity)(activityReference.get())).setLoginProgressIndicatorVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String email = strings[0];
        String password = strings[1];

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
                System.out.println("ERROR in AuthenticationTask: jwt is null");
                if(activityReference.get() instanceof LoginActivity){
                    ((LoginActivity)(activityReference.get())).showSnackbar("Invalid email or password. Try again.");
                }
                return false;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", jwt.substring(7));
            editor.apply();
            return true;
        }catch (IOException e){
            error = e.getMessage();
            System.out.println("ERROR in AuthenticationTask: " + e.getMessage());
            if (e.getMessage() != null) {
                if(e.getMessage().equals("timeout")){
                    if(activityReference.get() instanceof LoginActivity){
                        ((LoginActivity)(activityReference.get())).showSnackbar("Connection timed out");
                    }
                }else if(e.getMessage().contains("Unable to resolve host")){
                    if(activityReference.get() instanceof LoginActivity){
                        ((LoginActivity)(activityReference.get())).showSnackbar("Problem with the Internet connection");
                    }
                }else{
                    if(activityReference.get() instanceof LoginActivity){
                        ((LoginActivity)(activityReference.get())).showSnackbar("A problem has occurred while signing in. Please try again.");
                    }
                }
            }

            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if(activityReference.get() instanceof LoginActivity){
            ((LoginActivity)(activityReference.get())).setLoginProgressIndicatorVisibility(View.GONE);
        }
    }

    public String getError(){
        return error;
    }
}