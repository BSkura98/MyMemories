package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
        TextView categoryTextView = findViewById(R.id.categoryTextView);
        TextView categoryLabel = findViewById(R.id.categoryLabel);

        titleTextView.setText(getIntent().getStringExtra("title"));
        descriptionTextView.setText(getIntent().getStringExtra("description"));
        dateTextView.setText(getIntent().getStringExtra("date"));
        creationDateTextView.setText(getIntent().getStringExtra("creationDate"));
        priorityTextView.setText(getPriorityOption(getIntent().getIntExtra("memoryPriority",0)));

        String category = getIntent().getStringExtra("category");
        if(category !=null&&!category.isEmpty()){
            categoryTextView.setText(category);
        }else{
            categoryTextView.setVisibility(View.GONE);
            categoryLabel.setVisibility(View.GONE);
        }
    }

    private String getPriorityOption(int priority){
        return priority==10?"Low":(priority==50?"Medium":"High");
    }
}
