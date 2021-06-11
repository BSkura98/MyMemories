package com.bartlomiejskura.mymemories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.DeleteMemoryTask;
import com.bartlomiejskura.mymemories.task.DeleteUserFromMemoryTask;
import com.bartlomiejskura.mymemories.utils.DateUtil;
import com.bartlomiejskura.mymemories.utils.MemoryUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemoryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView titleTextView, descriptionTextView, dateTextView, creationDateTextView, priorityTextView, memoryFriendsTextView, publicTextView;
    private ImageView memoryImage, publicIcon;
    private SupportMapFragment mapFragment;
    private ChipGroup categoriesChipGroup;
    private ImageButton moreButton, editButton, untagYourselfButton;
    private ConstraintLayout memoryConstraintLayout;
    private View divider;
    private LinearProgressIndicator memoryProgressIndicator;

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
        moreButton = findViewById(R.id.moreButton);
        //editButton = findViewById(R.id.editButton3);
        untagYourselfButton = findViewById(R.id.untagYourselfButton2);
        memoryConstraintLayout = findViewById(R.id.memoryConstraintLayout);
        publicIcon =  findViewById(R.id.imageView8);
        publicTextView = findViewById(R.id.textView11);
        divider = findViewById(R.id.divider3);
        memoryProgressIndicator = findViewById(R.id.memoryProgressIndicator);
    }

    private void initValues(){
        Gson gson = new Gson();
        memory = gson.fromJson(getIntent().getStringExtra("memory"), Memory.class);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
    private void prepareViews(){
        //title text view
        titleTextView.setText(memory.getTitle());

        //description text view
        if(memory.getDescription().isEmpty()){
            descriptionTextView.setVisibility(View.GONE);
        }else{
            descriptionTextView.setText(memory.getDescription());
        }

        //priority text view
        priorityTextView.setText(getPriorityOption(memory.getPriority()));

        //date text view
        if(memory.getDate().endsWith("0")){
            dateTextView.setText(DateUtil.formatDate(memory.getDate()));
        }else{
            dateTextView.setText(DateUtil.formatDateTime(memory.getDate()));
        }

        //creation date text view
        String creationDate = memory.getModificationDate();
        creationDateTextView.setText(DateUtil.formatDateTime(creationDate));

        //memory friends text view (users tagged in memory)
        if(memory.getMemoryFriends().size()>0||!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            memoryFriendsTextView.setText(MemoryUtil.getTaggedFriends(memory, this));
        }else{
            memoryFriendsTextView.setVisibility(View.GONE);
        }

        //memory image
        if(memory.getImageUrl()!=null){
            Glide.with(this).load(memory.getImageUrl()).apply(new RequestOptions().fitCenter()).into(memoryImage);
        }else{
            memoryImage.setVisibility(View.GONE);
        }

        //text view and icon related to public memory
        if(!memory.getIsPublicToFriends()){
            publicIcon.setImageResource(R.drawable.ic_lock_outline);
            publicTextView.setText("Private");
            //findViewById(R.id.imageView8).setVisibility(View.GONE);
            //findViewById(R.id.textView11).setVisibility(View.GONE);
        }

        //categories chip group
        if(memory.getCategories()!=null&&memory.getCategories().size()>0){
            initCategoriesChipGroup(memory.getCategories());
        }else{
            categoriesChipGroup.setVisibility(View.GONE);
        }

        //map fragment
        mapFragment.getMapAsync(this);

        //edit, delete and untag buttons
        if(!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            boolean hideUntagButton=true;
            if(memory.getMemoryFriends()!=null&&!memory.getMemoryFriends().isEmpty()){
                for(User friend:memory.getMemoryFriends()){
                    if(friend.getId().equals(sharedPreferences.getLong("userId",0))){
                        hideUntagButton=false;
                        break;
                    }
                }

            }
            if(hideUntagButton){
                untagYourselfButton.setVisibility(View.GONE);
            }
            moreButton.setVisibility(View.GONE);
            //editButton.setVisibility(View.GONE);
        }else{
            untagYourselfButton.setVisibility(View.GONE);
        }

        //divider
        if(memory.getDescription().isEmpty()){
            divider.setVisibility(View.GONE);
        }

        //progress indicator
        memoryProgressIndicator.setVisibility(View.GONE);
    }

    private void setListeners(){
        if(!memory.getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            untagYourselfButton.setOnClickListener(v -> untagYourselfFromMemory());
        }else{
            //editButton.setOnClickListener(v -> editMemory());
            //moreButton.setOnClickListener(v -> deleteMemory());

            moreButton.setOnClickListener(v->{
                showPopup();
            });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int LAUNCH_SECOND_ACTIVITY = 1;

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                Intent i = new Intent();
                setResult(Activity.RESULT_OK,i);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
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
        runOnUiThread(() -> memoryProgressIndicator.setVisibility(View.VISIBLE));
        Long memoryId = memory.getId();
        DeleteMemoryTask task = new DeleteMemoryTask(this, memoryId);
        try{
            Boolean result = task.execute().get();
            if(result){
                Intent i = new Intent();
                setResult(Activity.RESULT_OK,i);
                finish();
            }else{
                runOnUiThread(()->{
                    runOnUiThread(() -> memoryProgressIndicator.setVisibility(View.GONE));
                    if(task.getError().contains("Unable to resolve host")){
                        Snackbar.make(memoryConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show();
                    }else if(task.getError().contains("timeout")){
                        Snackbar.make(memoryConstraintLayout, "Connection timed out", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(memoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }catch (Exception e){
            runOnUiThread(()->{
                runOnUiThread(() -> memoryProgressIndicator.setVisibility(View.GONE));
                Snackbar.make(memoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
            });
            e.printStackTrace();
        }
    }

    private void editMemory(){
        Gson gson = new Gson();

        Intent i = new Intent(getApplicationContext(), EditMemoryActivity.class);
        i.putExtra("title", memory.getTitle());
        i.putExtra("description", memory.getDescription());
        i.putExtra("date", memory.getDate());
        i.putExtra("memoryId", memory.getId());
        i.putExtra("memoryPriority", memory.getPriority());
        i.putExtra("imageUrl", memory.getImageUrl());
        i.putExtra("isMemoryPublic", memory.getIsPublicToFriends());
        i.putExtra("latitude", memory.getLatitude());
        i.putExtra("longitude", memory.getLongitude());

        i.putExtra("memoryFriends", gson.toJson(memory.getMemoryFriends()));
        List<Category> categories = new ArrayList<>(memory.getCategories());
        i.putExtra("categories", gson.toJson(categories));
        startActivityForResult(i, 1);
    }

    public void showPopup(){
        PopupMenu popupMenu = new PopupMenu(this, moreButton);
        popupMenu.setOnMenuItemClickListener(item->{
            switch (item.getItemId()){
                case R.id.editItem:
                    editMemory();
                    return true;
                case R.id.deleteItem:
                    new Thread(this::deleteMemory).start();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.menu_edit_delete);
        popupMenu.show();
    }

    private void untagYourselfFromMemory(){
        memory.removeMemoryFriend(sharedPreferences.getLong("userId",0));

        try{
            DeleteUserFromMemoryTask task = new DeleteUserFromMemoryTask(this, memory);
            Boolean editMemoryResult = task.execute().get();
            if(editMemoryResult){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("hasBackPressed",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{
                runOnUiThread(()->{
                    if(task.getError().contains("Unable to resolve host")){
                        Snackbar.make(memoryConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show();
                    }else if(task.getError().contains("timeout")){
                        Snackbar.make(memoryConstraintLayout, "Connection timed out", Snackbar.LENGTH_LONG).show();
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
