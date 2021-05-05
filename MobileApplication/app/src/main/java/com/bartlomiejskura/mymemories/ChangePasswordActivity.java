package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.task.ChangePasswordTask;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.concurrent.ExecutionException;

public class ChangePasswordActivity extends AppCompatActivity {
    private Button confirmButton;
    private EditText currentPasswordEditText, newPassword1EditText, newPassword2EditText;
    private Toolbar toolbar;
    private TextView toolbarTextView;
    private ImageButton searchButton, backButton;
    private LinearProgressIndicator changePasswordProgressIndicator;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        confirmButton = findViewById(R.id.confirmButton);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPassword1EditText = findViewById(R.id.newPassword1EditText);
        newPassword2EditText = findViewById(R.id.newPassword2EditText);
        toolbar = findViewById(R.id.toolbarSearchResults);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        changePasswordProgressIndicator = findViewById(R.id.changePasswordProgressIndicator);
    }

    private void initValues(){
        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Change password");
        searchButton.setVisibility(View.GONE);

        //progress indicator
        changePasswordProgressIndicator.setVisibility(View.GONE);
    }

    private void setListeners(){
        confirmButton.setOnClickListener(v -> {
            new Thread(this::changePassword).start();
        });

        backButton.setOnClickListener(v -> {
            finish();
        });
    }


    private void changePassword(){
        if(!newPassword1EditText.getText().toString().equals(newPassword2EditText.getText().toString())){
            runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Passwords given in fields \"New password\" and \"Confirm new password\" are not the same!", Toast.LENGTH_LONG).show());
            return;
        }

        runOnUiThread(()->changePasswordProgressIndicator.setVisibility(View.VISIBLE));

        ChangePasswordTask changePasswordTask =
                new ChangePasswordTask(
                        currentPasswordEditText.getText().toString(),
                        newPassword1EditText.getText().toString(),
                        sharedPreferences);
        try {
            changePasswordTask.execute().get();
            finish();
        } catch (ExecutionException | InterruptedException e) {
            runOnUiThread(()->changePasswordProgressIndicator.setVisibility(View.GONE));
            e.printStackTrace();
        }
    }
}
