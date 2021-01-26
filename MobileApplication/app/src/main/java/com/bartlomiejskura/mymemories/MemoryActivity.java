package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        TextView memoryFriends = findViewById(R.id.memoryFriends);
        ImageView memoryImage = findViewById(R.id.memoryImage);

        titleTextView.setText(getIntent().getStringExtra("title"));
        String description = getIntent().getStringExtra("description");
        if(description.isEmpty()){
            descriptionTextView.setVisibility(View.GONE);
        }else{
            descriptionTextView.setText(description);
        }
        priorityTextView.setText(getPriorityOption(getIntent().getIntExtra("memoryPriority",0)));
        String date = getIntent().getStringExtra("date");
        String creationDate = getIntent().getStringExtra("creationDate");
        dateTextView.setText(date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4)+" "+date.substring(11, 16));
        creationDateTextView.setText(creationDate.substring(8, 10) + "-" + creationDate.substring(5, 7) + "-" + creationDate.substring(0, 4)+" "+creationDate.substring(11, 16));
        if(!getIntent().getStringExtra("memoryFriends").isEmpty()){
            memoryFriends.setText(getIntent().getStringExtra("memoryFriends"));
        }else{
            memoryFriends.setVisibility(View.GONE);
        }

        String category = getIntent().getStringExtra("category");
        if(category !=null&&!category.isEmpty()){
            categoryTextView.setText(category);
        }else{
            categoryTextView.setVisibility(View.GONE);
            categoryLabel.setVisibility(View.GONE);
        }

        String imageUrl = getIntent().getStringExtra("imageUrl");

        if(imageUrl!=null){
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(memoryImage);
        }else{
            memoryImage.setVisibility(View.GONE);
        }
    }

    private String getPriorityOption(int priority){
        return priority==10?"Low":(priority==50?"Medium":"High");
    }
}
