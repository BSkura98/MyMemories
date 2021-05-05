package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bartlomiejskura.mymemories.task.AuthenticationTask;
import com.bartlomiejskura.mymemories.task.CreateUserTask;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private Button birthdayButton, signUpButton;
    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, repeatPasswordEditText;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, passwordLayout, repeatPasswordLayout;
    private ConstraintLayout registerConstraintLayout;
    private CreateUserTask createUserTask;
    private AuthenticationTask authenticationTask;
    private CircularProgressIndicator signUpProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        birthdayButton = findViewById(R.id.birthdayButton);
        signUpButton = findViewById(R.id.signUpButton);
        registerConstraintLayout = findViewById(R.id.registerConstraintlayout);
        firstNameLayout = findViewById(R.id.textInputLayout);
        lastNameLayout = findViewById(R.id.textInputLayout2);
        emailLayout = findViewById(R.id.textInputLayout3);
        passwordLayout = findViewById(R.id.textInputLayout4);
        repeatPasswordLayout = findViewById(R.id.textInputLayout5);
        signUpProgressIndicator =findViewById(R.id.signUpProgressIndicator);
    }

    private void initValues() {
        createUserTask = new CreateUserTask(this);
        authenticationTask = new AuthenticationTask(this);
    }

    private void prepareViews(){
        signUpProgressIndicator.setVisibility(View.GONE);
    }

    private void setListeners() {
        birthdayButton.setOnClickListener(v -> selectDate());

        signUpButton.setOnClickListener(v -> {
            registerUser();
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("birthday", birthdayButton.getText().toString());
    }


    private void registerUser(){
        if(createUserTask.getStatus()== AsyncTask.Status.RUNNING||authenticationTask.getStatus()==AsyncTask.Status.RUNNING){
            return;
        }

        if(!verifyData()){
            return;
        }

        setSignUpInProgress(true);

        new Thread(() -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String birthday = birthdayButton.getText().toString();
            birthday = birthday.split("-")[2].concat("-").concat(birthday.split("-")[1]).concat("-").concat(birthday.split("-")[0]).concat("T00:00:00");

            createUserTask = new CreateUserTask(this);
            authenticationTask = new AuthenticationTask(this);
            try{
                Integer createUserResult = createUserTask.execute(email, password, firstName, lastName, birthday).get();
                switch (createUserResult){
                    case 200:
                        break;
                    case 409:
                        runOnUiThread(() -> emailLayout.setError("An account with this email already exists"));
                        setSignUpInProgress(false);
                        return;
                    case -3:
                        runOnUiThread(() -> Snackbar.make(registerConstraintLayout, "Connection timed out", Snackbar.LENGTH_LONG).show());
                        setSignUpInProgress(false);
                        return;
                    case -2:
                        runOnUiThread(() -> Snackbar.make(registerConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show());
                        setSignUpInProgress(false);
                        return;
                    case -1:
                    default:
                        runOnUiThread(() -> Snackbar.make(registerConstraintLayout, "A problem has occurred while signing up. Please try again.", Snackbar.LENGTH_LONG).show());
                        setSignUpInProgress(false);
                        return;
                }
            }catch (Exception e){
                System.out.println("ERROR in RegisterActivity:" + e.getMessage());
                runOnUiThread(() -> Snackbar.make(registerConstraintLayout, "A problem has occurred while signing up. Please try again.", Snackbar.LENGTH_LONG).show());
                setSignUpInProgress(false);
            }

            try{
                Boolean authenticationResult = authenticationTask.execute(email, password).get();
                if(!authenticationResult){
                    runOnUiThread(() -> Snackbar.make(registerConstraintLayout, "Account created but there was a problem with authentication. Please try to log in.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Log in", v -> onBackPressed()).show());
                    setSignUpInProgress(false);
                    return;
                }
                setSignUpInProgress(true);

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }catch (Exception e){
                System.out.println("ERROR in RegisterActivity:" + e.getMessage());
                runOnUiThread(() -> Snackbar.make(registerConstraintLayout, "Account created but there was a problem with authentication. Please try to log in.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Log in", v -> onBackPressed()).show());
                setSignUpInProgress(false);
            }
        }).start();
    }

    private boolean verifyData(){
        firstNameLayout.setError("");
        lastNameLayout.setError("");
        emailLayout.setError("");
        passwordLayout.setError("");
        repeatPasswordLayout.setError("");

        if(firstNameEditText.getText().toString().isEmpty()){
            firstNameLayout.setError("First name field cannot be empty");
            return false;
        }
        if(lastNameEditText.getText().toString().isEmpty()){
            lastNameLayout.setError("Last name field cannot be empty");
            return false;
        }
        if(emailEditText.getText().toString().isEmpty()){
            emailLayout.setError("Email field cannot be empty");
            return false;
        }
        String[] checkArray = emailEditText.getText().toString().split("@");
        if(checkArray.length!=2||checkArray[0].length()==0){
            emailLayout.setError("Please enter a valid email");
            return false;
        }else{
            String[] checkArray2  = checkArray[1].split("\\.", -1);
            if(checkArray2.length<2){
                emailLayout.setError("Please enter a valid email");
                return false;
            }else{
                for(String string:checkArray2){
                    if(string.length()==0){
                        emailLayout.setError("Please enter a valid email");
                        return false;
                    }
                }
            }
        }
        if(passwordEditText.getText().toString().isEmpty()){
            passwordLayout.setError("Password field cannot be empty");
            return false;
        }
        if(passwordEditText.getText().toString().length()<6){
            passwordLayout.setError("The password should be at least 6 characters long");
            return false;
        }
        if(repeatPasswordEditText.getText().toString().isEmpty()){
            repeatPasswordLayout.setError("Repeat password field cannot be empty");
            return false;
        }
        if(!passwordEditText.getText().toString().equals(repeatPasswordEditText.getText().toString())){
            repeatPasswordLayout.setError("Passwords given in fields \"Password\" and \"Repeat Password\" are not the same");
            return false;
        }
        if(birthdayButton.getText().equals("Select")){
            Snackbar.make(registerConstraintLayout, "Date of birth is required", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year1, month1, dayOfMonth) -> {

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, year1);
            calendar1.set(Calendar.MONTH, month1);
            calendar1.set(Calendar.DATE, dayOfMonth);
            calendar1.set(Calendar.HOUR, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            String dateText = DateFormat.format("dd-MM-yyyy", calendar1).toString();

            if(Calendar.getInstance().after(calendar1)){
                birthdayButton.setText(dateText);
            }else {
                Snackbar.make(registerConstraintLayout, "Date of birth cannot be in the future!", Snackbar.LENGTH_LONG).show();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setSignUpInProgress(boolean inProgress){
        runOnUiThread(()->{
            if(inProgress){
                signUpProgressIndicator.setVisibility(View.VISIBLE);
                signUpButton.setText("");
            }else{
                signUpProgressIndicator.setVisibility(View.GONE);
                signUpButton.setText("Sign Up");
            }
        });
    }
}
