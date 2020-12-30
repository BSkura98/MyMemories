package com.bartlomiejskura.mymemories;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.CreateMemoryTask;
import com.bartlomiejskura.mymemories.task.EditMemoryTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditMemoryActivity extends AppCompatActivity {
    private TextView dateTextView, timeTextView;
    private int day, month, year, hour, minute;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);

        final String title = getIntent().getStringExtra("title");
        final String description = getIntent().getStringExtra("description");
        final String date = getIntent().getStringExtra("date");

        final EditText titleEditText = findViewById(R.id.titleEditText);
        final EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        final Button editMemoryButton = findViewById(R.id.editMemoryButton);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        dateTextView.setText(date);

        dateTextView.setText(date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4));
        timeTextView.setText(date.substring(11, 16));

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        editMemoryButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                editMemory(titleEditText.getText().toString(), descriptionEditText.getText().toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editMemory(String title, String description){
        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Title field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        //LocalDateTime date = LocalDateTime.of(year, month+1, day, hour, minute);
        //DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        Long memoryOwnerId = sharedPreferences.getLong("userId", 0);
        Memory memory = new Memory(getIntent().getLongExtra("memoryId", 0), title, description, sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"), sdf.format(calendar.getTime()).replace(" ", "T"), new User(memoryOwnerId));
        final EditMemoryActivity activity = this;

        try{
            EditMemoryTask editMemoryTask = new EditMemoryTask(activity, memory);
            Boolean editMemoryResult = editMemoryTask.execute().get();
            if(!editMemoryResult){
                return;
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("fragmentToLoad", "recentEntriesFragment");
            startActivity(i);
        }catch (Exception e){
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year1);
                calendar1.set(Calendar.MONTH, month1);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("dd-MM-yyyy", calendar1).toString();

                dateTextView.setText(dateText);
                year = year1;
                month = month1;
                day = date;
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour1, int minute1) {
                //Log.i(TAG, "onTimeSet: " + hour + minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, hour1);
                calendar1.set(Calendar.MINUTE, minute1);
                String dateText = DateFormat.format("HH:mm", calendar1).toString();
                timeTextView.setText(dateText);
                hour = hour1;
                minute = minute1;
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }
}
