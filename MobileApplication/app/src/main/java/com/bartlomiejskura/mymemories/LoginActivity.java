package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.task.AuthenticationTask;
import com.bartlomiejskura.mymemories.task.GetUserInformationTask;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView signUpTextView = findViewById(R.id.signUpTextView);
        Button signInButton = findViewById(R.id.signInButton);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        final LoginActivity activity = this;
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = findViewById(R.id.emailEditText);
                EditText passwordEditText = findViewById(R.id.passwordEditText);

                try{
                    AuthenticationTask authenticationTask = new AuthenticationTask(activity, emailEditText.getText().toString(), passwordEditText.getText().toString());
                    Boolean authenticationResult = authenticationTask.execute().get();
                    if(!authenticationResult){
                        return;
                    }
                    GetUserInformationTask getUserInformationTask = new GetUserInformationTask(activity, emailEditText.getText().toString());
                    Boolean getUserInformationResult = getUserInformationTask.execute().get();
                    if(!getUserInformationResult){
                        return;
                    }
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }catch (Exception e){
                    System.out.println("ERROR:" + e.getMessage());
                }
            }
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
}
