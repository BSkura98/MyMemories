package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bartlomiejskura.mymemories.task.AuthenticationTask;
import com.bartlomiejskura.mymemories.task.GetUserInformationTask;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private Button signUpButton, signInButton;
    private EditText emailEditText, passwordEditText;
    private TextInputLayout emailLayout, passwordLayout;
    private LinearProgressIndicator loginProgressIndicator;
    private ConstraintLayout loginConstraintLayout;

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
        loginConstraintLayout = findViewById(R.id.loginConstraintlayout);
        emailLayout = findViewById(R.id.textInputLayout);
        passwordLayout = findViewById(R.id.textInputLayout2);
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
            if(authenticationTask.getStatus()==AsyncTask.Status.RUNNING||getUserInformationTask.getStatus()==AsyncTask.Status.RUNNING){
                return;
            }
            startActivity(new Intent(this, RegisterActivity.class));
        });

        signInButton.setOnClickListener(v -> new Thread(() -> {
            try{
                if(authenticationTask.getStatus()==AsyncTask.Status.RUNNING||getUserInformationTask.getStatus()==AsyncTask.Status.RUNNING){
                    return;
                }

                if(!verifyData()){
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
                System.out.println("ERROR in LoginActivity:" + e.getMessage());
                showSnackbar("A problem has occurred while signing in. Please try again.");
            }
        }).start());

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setError("");
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setError("");
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
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

    public void showSnackbar(String message){
        Snackbar.make(loginConstraintLayout, message, Snackbar.LENGTH_LONG).show();
    }


    private boolean verifyData(){
        boolean result = true;

        runOnUiThread(() -> {
            emailLayout.setError("");
            passwordLayout.setError("");
        });

        if(emailEditText.getText().toString().isEmpty()){
            runOnUiThread(() -> emailLayout.setError("Email field cannot be empty"));
            result = false;
        }else{
            String[] checkArray = emailEditText.getText().toString().split("@");
            if(checkArray.length!=2||checkArray[0].length()==0){
                runOnUiThread(() -> emailLayout.setError("Please enter a valid email"));
                result = false;
            }else{
                String[] checkArray2  = checkArray[1].split("\\.", -1);
                if(checkArray2.length<2){
                    runOnUiThread(() -> emailLayout.setError("Please enter a valid email"));
                    result = false;
                }else{
                    for(String string:checkArray2){
                        if(string.length()==0){
                            runOnUiThread(() -> emailLayout.setError("Please enter a valid email"));
                            result = false;
                            break;
                        }
                    }
                }
            }
        }


        if(passwordEditText.getText().toString().isEmpty()){
            runOnUiThread(() -> passwordLayout.setError("Password field cannot be empty"));
            result = false;
        }

        return result;
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
