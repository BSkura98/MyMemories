package com.bartlomiejskura.mymemories.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bartlomiejskura.mymemories.model.AuthenticationRequest;
import com.bartlomiejskura.mymemories.model.AuthenticationResponse;
import com.bartlomiejskura.mymemories.model.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserService {
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Gson gson;
    private OkHttpClient httpClient;
    private Context context;
    private SharedPreferences sharedPreferences;

    public UserService(Context context) {
        this.gson = new Gson();
        this.httpClient = new OkHttpClient();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    public void createNewUser(final String email, final String password, String firstName, String lastName, String birthday, final AppCompatActivity activity) {
        User user = new User(email, password, firstName, lastName, birthday);
        String json = gson.toJson(user);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/user")
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("ERROR:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    User user = gson.fromJson(response.body().string(), User.class);
                    if(user.getEmail()==null){
                        return;
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("userId", user.getId());
                    editor.putString("email", user.getEmail());
                    editor.putString("firstName", user.getFirstName());
                    editor.putString("lastName", user.getLastName());
                    editor.putString("birthday", user.getBirthday());
                    editor.apply();

                    authenticate(email, password, activity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void authenticate(String email, String password, final AppCompatActivity activity) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
        String json = gson.toJson(authenticationRequest);
        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/authenticate")
                .post(requestBody)
                .build();
        Response response;

        try{
            response = httpClient.newCall(request).execute();
            AuthenticationResponse authenticationResponse = gson.fromJson(response.body().string(), AuthenticationResponse.class);
            String jwt = authenticationResponse.getJwt();
            if(jwt==null){
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity.getBaseContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", authenticationResponse.getJwt());
            editor.apply();
        }catch (IOException e){
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
