package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.DeleteMemoryTask;
import com.bartlomiejskura.mymemories.task.EditMemoryTask;
import com.bartlomiejskura.mymemories.utils.DateUtil;
import com.bartlomiejskura.mymemories.utils.MemoryUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemoryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView titleTextView, descriptionTextView, dateTextView, creationDateTextView, priorityTextView, memoryFriendsTextView;
    private ImageView memoryImage;
    private SupportMapFragment mapFragment;
    private ChipGroup categoriesChipGroup;
    private ImageButton deleteButton, editButton, untagYourselfButton;
    private ConstraintLayout memoryConstraintLayout;

    private Memory memory;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
        creationDateTextView = findViewById(R.id.creationDateTextView);
        priorityTextView = findViewById(R.id.priorityTextView);
        memoryFriendsTextView = findViewById(R.id.memoryFriends);
        memoryImage = findViewById(R.id.memoryImage);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        categoriesChipGroup = findViewById(R.id.chipGroup2);
        deleteButton = findViewById(R.id.deleteButton6);
        editButton = findViewById(R.id.editButton3);
        untagYourselfButton = findViewById(R.id.untagYourselfButton2);
        memoryConstraintLayout = findViewById(R.id.memoryConstraintLayout);
    }

    private void initValues(){
        Gson gson = new Gson();
        memory = gson.fromJson(getIntent().getStringExtra("memory"), Memory.class);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    private void prepareViews(){
        //title text view
        titleTextView.setText(memory.getShortDescription());

        //description text view
        if(memory.getLongDescription().isEmpty()){
            descriptionTextView.setVisibility(View.GONE);
        }else{
            descriptionTextView.setText(memory.getLongDescription());
        }

        //priority text view
        priorityTextView.setText(getPriorityOption(memory.getMemoryPriority()));

        //date text view
        String date = memory.getDate();
        dateTextView.setText(DateUtil.formatDateTime(date));

        //creation date text view
        String creationDate = memory.getCreationDate();
        creationDateTextView.setText(DateUtil.formatDateTime(creationDate));

        //memory friends text view (users tagged in memory)
        if(memory.getMemoryFriends().size()>0||!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            memoryFriendsTextView.setText(MemoryUtil.getTaggedFriends(memory, this));
        }else{
            memoryFriendsTextView.setVisibility(View.GONE);
        }

        //memory image
        if(memory.getImageUrl()!=null){
            RequestCreator creator = Picasso.get().load(memory.getImageUrl());
            memoryImage.post(() -> creator.resize(memoryImage.getWidth(), 0)
                    .into(memoryImage));
        }else{
            memoryImage.setVisibility(View.GONE);
        }

        //text view and icon related to public memory
        if(!memory.getPublicToFriends()){
            findViewById(R.id.imageView8).setVisibility(View.GONE);
            findViewById(R.id.textView11).setVisibility(View.GONE);
        }

        //categories chip group
        if(memory.getCategories()!=null&&memory.getCategories().size()>0){
            initCategoriesChipGroup(memory.getCategories());
        }

        //map fragment
        mapFragment.getMapAsync(this);
    }

    private void setListeners(){
        if(!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            untagYourselfButton.setOnClickListener(v -> untagYourselfFromMemory());
            deleteButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }else{
            editButton.setOnClickListener(v -> editMemory());
            deleteButton.setOnClickListener(v -> deleteMemory());
            untagYourselfButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap == null) {
            return;
        }

        if(memory != null &&memory.getLatitude()!=null&&memory.getLongitude()!=null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(memory.getLatitude(), memory.getLongitude()), 15f));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(memory.getLatitude(), memory.getLongitude()));
            googleMap.addMarker(markerOptions);
        }else{
            mapFragment.getView().setVisibility(View.GONE);
        }
    }


    private String getPriorityOption(int priority){
        return priority<=10?"Low priority":(priority<=50?"Medium priority":"High priority");
    }

    private void initCategoriesChipGroup(List<Category> categories){
        LayoutInflater inflater = LayoutInflater.from(MemoryActivity.this);

        for(Category category:categories){
            Chip chip = (Chip)inflater.inflate(R.layout.chip, null, false);
            chip.setText(category.getName());
            chip.setCheckable(false);

            categoriesChipGroup.addView(chip);
        }
    }

    private void deleteMemory(){
        Long memoryId = memory.getId();
        DeleteMemoryTask task = new DeleteMemoryTask(this, memoryId);
        try{
            Boolean result = task.execute().get();
            if(result){
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("date", memory.getDate());
                i.putExtra("fragmentToLoad", "datesFragment");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }else{
                runOnUiThread(()->{
                    if(task.getError().contains("Unable to resolve host")){
                        Snackbar.make(memoryConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(memoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }catch (Exception e){
            runOnUiThread(()->{
                Snackbar.make(memoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
            });
            e.printStackTrace();
        }
    }

    private void editMemory(){
        Gson gson = new Gson();

        Intent i = new Intent(getApplicationContext(), EditMemoryActivity.class);
        i.putExtra("title", memory.getShortDescription());
        i.putExtra("description", memory.getLongDescription());
        i.putExtra("date", memory.getDate());
        i.putExtra("memoryId", memory.getId());
        i.putExtra("memoryPriority", memory.getMemoryPriority());
        i.putExtra("imageUrl", memory.getImageUrl());
        i.putExtra("isMemoryPublic", memory.getPublicToFriends());
        i.putExtra("latitude", memory.getLatitude());
        i.putExtra("longitude", memory.getLongitude());

        i.putExtra("memoryFriends", gson.toJson(memory.getMemoryFriends()));
        List<Category> categories = new ArrayList<>(memory.getCategories());
        i.putExtra("categories", gson.toJson(categories));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void untagYourselfFromMemory(){
        memory.removeMemoryFriend(sharedPreferences.getLong("userId",0));

        try{
            EditMemoryTask editMemoryTask = new EditMemoryTask(this, memory);
            Boolean editMemoryResult = editMemoryTask.execute().get();
            if(editMemoryResult){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("hasBackPressed",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{
                runOnUiThread(()->{
                    if(editMemoryTask.getError().contains("Unable to resolve host")){
                        Snackbar.make(memoryConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(memoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            runOnUiThread(()->{
                Snackbar.make(memoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
            });
            e.printStackTrace();
        }
    }
}
