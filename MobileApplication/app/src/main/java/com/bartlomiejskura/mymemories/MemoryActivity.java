package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MemoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView creationDateTextView = findViewById(R.id.creationDateTextView);
        TextView priorityTextView = findViewById(R.id.priorityTextView);

        titleTextView.setText(getIntent().getStringExtra("title"));
        descriptionTextView.setText(getIntent().getStringExtra("description"));
        dateTextView.setText(getIntent().getStringExtra("date"));
        creationDateTextView.setText(getIntent().getStringExtra("creationDate"));
        priorityTextView.setText(getPriorityOption(getIntent().getIntExtra("memoryPriority",0)));
    }

    private String getPriorityOption(int priority){
        return priority==10?"Low":(priority==50?"Medium":"High");
    }
}
