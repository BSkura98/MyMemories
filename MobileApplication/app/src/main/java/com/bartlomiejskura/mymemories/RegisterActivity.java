package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.services.UserService;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private TextView birthdayTextView;
    private int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        final EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        final EditText repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        birthdayTextView = findViewById(R.id.birthdayTextView);
        Button signUpButton = findViewById(R.id.signUpButton);

        birthdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString(), repeatPasswordEditText.getText().toString(), birthdayTextView.getText().toString());
            }
        });
    }

    private void registerUser(String firstName, String lastName, String email, String password, String repeatPassword, String birthday){
        if(!isDataValid(firstName, lastName, email, password, repeatPassword, birthday)){
            return;
        }
        UserService userService = new UserService(getApplicationContext());

        userService.createNewUser(email, password, firstName, lastName, birthday, this);
        //userService.authenticate(email, password, this);
    }

    private boolean isDataValid(String firstName, String lastName, String email, String password, String repeatPassword, String birthday){
        if(firstName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Field \"First Name\" cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(lastName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Field \"Last Name\" cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Field \"Email\" cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Field \"Password\" cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(repeatPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Field \"Repeat Password\" cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(birthday.equals("Select")){
            Toast.makeText(getApplicationContext(), "You need to select a date of birth!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(repeatPassword)){
            Toast.makeText(getApplicationContext(), "Passwords given in fields \"Password\" and \"Repeat Password\" are not the same!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.DATE, dayOfMonth);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                String dateText = DateFormat.format("dd-MM-yyyy", calendar).toString();

                if(Calendar.getInstance().after(calendar)){
                    birthdayTextView.setText(dateText);
                    year = year1;
                    month = month1;
                    day = dayOfMonth;
                }else {
                    Toast.makeText(getApplicationContext(), "Date of birth cannot be in the future!", Toast.LENGTH_SHORT).show();
                }
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }
}
