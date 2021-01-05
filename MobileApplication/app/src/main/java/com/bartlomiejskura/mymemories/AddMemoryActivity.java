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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.CreateMemoryTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddMemoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView dateTextView, timeTextView;
    private int day, month, year, hour, minute;
    private SharedPreferences sharedPreferences;
    private int memoryPriority=90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        final EditText titleEditText = findViewById(R.id.titleEditText);
        final EditText description = findViewById(R.id.descriptionEditText);
        final Button addMemoryButton = findViewById(R.id.addMemoryButton);
        final Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(this);

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

        addMemoryButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                addMemory(titleEditText.getText().toString(), description.getText().toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addMemory(String title, String description){
        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Title field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        Long memoryOwnerId = sharedPreferences.getLong("userId", 0);
        Memory memory = new Memory(title, description, sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"), sdf.format(calendar.getTime()).replace(" ", "T"), new User(memoryOwnerId), memoryPriority);
        final AddMemoryActivity activity = this;

        try{
            CreateMemoryTask createMemoryTask = new CreateMemoryTask(activity, memory);
            Boolean createMemoryResult = createMemoryTask.execute().get();
            if(!createMemoryResult){
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        if(selected.equals("High")){
            memoryPriority=90;
        }else if(selected.equals("Medium")){
            memoryPriority = 50;
        }else if(selected.equals("Low")){
            memoryPriority = 10;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
