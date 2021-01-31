package com.bartlomiejskura.mymemories.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Activity activity;
    private String email, password, firstName, lastName, birthday;
    private OkHttpClient httpClient = new OkHttpClient();
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    public CreateUserTask(Activity activity, String email, String password, String firstName, String lastName, String birthday){
        this.activity = activity;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        User user = new User(email, password, firstName, lastName, birthday);
        String json = gson.toJson(user);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://mymemories-2.herokuapp.com/user")
                .post(requestBody)
                .build();
        Response response;

        try {
            response = httpClient.newCall(request).execute();

            User userResponse = gson.fromJson(response.body().string(), User.class);
            if(user==null||userResponse.getEmail()==null){
                return false;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("userId", userResponse.getId());
            editor.putString("email", userResponse.getEmail());
            editor.putString("firstName", userResponse.getFirstName());
            editor.putString("lastName", userResponse.getLastName());
            editor.putString("birthday", userResponse.getBirthday());
            editor.apply();
        } catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
            return false;
        }
        return true;
    }
}
