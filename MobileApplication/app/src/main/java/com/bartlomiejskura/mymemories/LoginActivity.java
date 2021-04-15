package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bartlomiejskura.mymemories.task.AuthenticationTask;
import com.bartlomiejskura.mymemories.task.GetUserInformationTask;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private Button signUpButton, signInButton;
    private EditText emailEditText, passwordEditText;
    private LinearProgressIndicator loginProgressIndicator;

    private AuthenticationTask authenticationTask;
    private GetUserInformationTask getUserInformationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        signUpButton = findViewById(R.id.signUpButton);
        signInButton = findViewById(R.id.signInButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginProgressIndicator = findViewById(R.id.loginProgressIndicator);
    }

    private void initValues(){
        authenticationTask = new AuthenticationTask(this);
        getUserInformationTask = new GetUserInformationTask(this);
    }

    private void prepareViews(){
        loginProgressIndicator.setVisibility(View.GONE);
    }

    private void setListeners(){
        signUpButton.setOnClickListener(v -> {
            if(authenticationTask.getStatus()==AsyncTask.Status.RUNNING||authenticationTask.getStatus()==AsyncTask.Status.RUNNING){
                return;
            }
            startActivity(new Intent(this, RegisterActivity.class));
        });

        signInButton.setOnClickListener(v -> new Thread(() -> {
            try{
                if(authenticationTask.getStatus()==AsyncTask.Status.RUNNING||authenticationTask.getStatus()==AsyncTask.Status.RUNNING){
                    return;
                }
                Boolean authenticationResult = authenticateUser();
                if(authenticationResult){
                    Boolean getUserInformationResult = getUserInformation();
                    if(getUserInformationResult){
                        startActivity(new Intent(this, MainActivity.class));
                    }
                }
            }catch (Exception e){
                System.out.println("ERROR:" + e.getMessage());
            }
        }).start());
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



    public void setLoginProgressIndicatorVisibility(int visibility){
        runOnUiThread(() -> loginProgressIndicator.setVisibility(visibility));
    }

    private Boolean authenticateUser() throws ExecutionException, InterruptedException {
        authenticationTask = new AuthenticationTask(this);
        return authenticationTask.execute(emailEditText.getText().toString(), passwordEditText.getText().toString()).get();
    }

    private Boolean getUserInformation() throws ExecutionException, InterruptedException {
        getUserInformationTask = new GetUserInformationTask(this);
        return getUserInformationTask.execute(emailEditText.getText().toString()).get();
    }
}
