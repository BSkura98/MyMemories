package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemoryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView titleTextView, descriptionTextView, toolbarTextView, dateTextView, creationDateTextView, priorityTextView, memoryFriends, locationTextView;
    private ImageView memoryImage;
    private Toolbar toolbar;
    private ImageButton backButton;
    private LinearLayout publicLayout, editDeleteButtonsLayout, untagYourselfButtonLayout;
    private SupportMapFragment mapFragment;
    private ChipGroup categoriesChipGroup;
    private Button deleteButton, editButton, untagYourselfButton;

    private Memory memory;
    private GoogleMap map;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        bindViews();
        initToolbar();
        getMemory();
        setElements();
        setListeners();
        mapFragment.getMapAsync(this);
    }

    private void bindViews(){
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        dateTextView = findViewById(R.id.dateTextView);
        creationDateTextView = findViewById(R.id.creationDateTextView);
        priorityTextView = findViewById(R.id.priorityTextView);
        memoryFriends = findViewById(R.id.memoryFriends);
        memoryImage = findViewById(R.id.memoryImage);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbar = findViewById(R.id.toolbarMemory);
        backButton = findViewById(R.id.backButton);
        publicLayout = findViewById(R.id.publicLayout);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        locationTextView = findViewById(R.id.textView31);
        categoriesChipGroup = findViewById(R.id.chipGroup2);
        editButton = findViewById(R.id.editButton2);
        deleteButton = findViewById(R.id.deleteButton5);
        editDeleteButtonsLayout = findViewById(R.id.linearLayout8);
        untagYourselfButtonLayout = findViewById(R.id.linearLayout9);
        untagYourselfButton = findViewById(R.id.untagYourselfButton);
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Memory");

        backButton.setOnClickListener(v -> super.onBackPressed());
    }

    private void setListeners(){
        if(!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            untagYourselfButton.setOnClickListener(v -> untagYourselfFromMemory());
            editDeleteButtonsLayout.setVisibility(View.GONE);
        }else{
            editButton.setOnClickListener(v -> editMemory());
            deleteButton.setOnClickListener(v -> deleteMemory());
            untagYourselfButtonLayout.setVisibility(View.GONE);
        }
    }

    private void getMemory(){
        Gson gson = new Gson();
        memory = gson.fromJson(getIntent().getStringExtra("memory"), Memory.class);
    }

    private void setElements(){
        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        titleTextView.setText(memory.getShortDescription());
        if(memory.getLongDescription().isEmpty()){
            descriptionTextView.setVisibility(View.GONE);
        }else{
            descriptionTextView.setText(memory.getLongDescription());
        }
        priorityTextView.setText(getPriorityOption(memory.getMemoryPriority()));
        String date = memory.getDate();
        String creationDate = memory.getCreationDate();
        dateTextView.setText(DateUtil.formatDateTime(date));
        creationDateTextView.setText(DateUtil.formatDateTime(creationDate));
        if(memory.getMemoryFriends().size()>0||!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            memoryFriends.setText(MemoryUtil.getTaggedFriends(memory, this));
        }else{
            memoryFriends.setVisibility(View.GONE);
        }

        if(memory.getImageUrl()!=null){
            RequestCreator creator = Picasso.get().load(memory.getImageUrl());

            memoryImage.post(() -> creator.resize(memoryImage.getWidth(), 0)
                    .into(memoryImage));
        }else{
            memoryImage.setVisibility(View.GONE);
        }

        if(!memory.getPublicToFriends()){
            publicLayout.setVisibility(View.GONE);
        }

        if(memory.getCategories()!=null&&memory.getCategories().size()>0){
            initCategoriesChipGroup(memory.getCategories());
        }
    }

    private String getPriorityOption(int priority){
        return priority<=10?"Low":(priority<=50?"Medium":"High");
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("hasBackPressed",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }catch (Exception e){
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
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map == null) {
            return;
        }

        if(memory != null &&memory.getLatitude()!=null&&memory.getLongitude()!=null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(memory.getLatitude(), memory.getLongitude()), 15f));
        }else{
            mapFragment.getView().setVisibility(View.GONE);
            locationTextView.setVisibility(View.GONE);
        }
    }
}
