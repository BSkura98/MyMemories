package com.bartlomiejskura.mymemories;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.EditCategoryTask;
import com.bartlomiejskura.mymemories.task.GetMemoriesInCategoryTask;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements ChangeCategoryNameDialog.DialogListener {
    private RecyclerView memoryList;
    private TextView toolbarTextView, messageTextView;
    private Toolbar toolbar;
    private ImageButton backButton, editCategoryNameButton;
    private CircularProgressIndicator categoryProgressIndicator;
    private LinearProgressIndicator categoryNameProgressIndicator;
    private ConstraintLayout categoryConstraintLayout;

    private MemoryListAdapter adapter;
    private Long categoryId;
    private SharedPreferences sharedPreferences;
    private boolean categoryNameModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        memoryList = findViewById(R.id.memoryList);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backButton);
        categoryProgressIndicator = findViewById(R.id.categoryProgressIndicator);
        messageTextView = findViewById(R.id.categoryMessageTextView);
        editCategoryNameButton = findViewById(R.id.editButton);
        categoryConstraintLayout = findViewById(R.id.categoryConstraintLayout);
        categoryNameProgressIndicator = findViewById(R.id.categoryNameProgressIndicator);
    }

    private void initValues(){
        categoryId = getIntent().getLongExtra("categoryId",0);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText(getIntent().getStringExtra("category"));

        //priority spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //recycler view with memories
        new Thread(this::getMemories).start();

        //message TextView showing problems
        messageTextView.setVisibility(View.GONE);

        //category name change progress indicator
        categoryNameProgressIndicator.setVisibility(View.GONE);
    }

    private void setListeners(){
        backButton.setOnClickListener(v -> onBackPressed());

        editCategoryNameButton.setOnClickListener(v->{
            openChangeCategoryNameDialog();
        });
    }


    @Override
    public void applyCategoryName(String name) {
        try{
            if(name.isEmpty()){
                runOnUiThread(()-> Snackbar.make(categoryConstraintLayout, "Category name cannot be empty", Snackbar.LENGTH_LONG).show());
                return;
            }
            if(name.equalsIgnoreCase(getIntent().getStringExtra("category"))){
                return;
            }
            if(name.length()>20){
                runOnUiThread(() -> {
                    Snackbar.make(categoryConstraintLayout, "Category name cannot be longer than 20 characters", Snackbar.LENGTH_LONG).show();
                });
                return;
            }
            runOnUiThread(()->categoryNameProgressIndicator.setVisibility(View.VISIBLE));
            name = name.toLowerCase();
            EditCategoryTask task = new EditCategoryTask(this, new Category(categoryId, name, new User(sharedPreferences.getLong("userId", 0),sharedPreferences.getString("email","")), adapter.getMemories()));
            boolean result = task.execute().get();
            String finalName = name;
            categoryNameModified = true;
            runOnUiThread(()->{
                if(result){
                    toolbarTextView.setText(finalName);
                }else{
                    if(task.getError().contains("Unable to resolve host")){
                        Snackbar.make(categoryConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show();
                    }else if(task.getError().contains("409")){
                        Snackbar.make(categoryConstraintLayout, "Category with the given name already exists", Snackbar.LENGTH_LONG).show();
                    }else if(task.getError().contains("timeout")){
                        Snackbar.make(categoryConstraintLayout, "Connection timed out", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(categoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
                    }
                }
                categoryNameProgressIndicator.setVisibility(View.GONE);
            });
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(()->{
                categoryNameProgressIndicator.setVisibility(View.GONE);
                Snackbar.make(categoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int LAUNCH_SECOND_ACTIVITY = 1;

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                new Thread(this::getMemories).start();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    public void onBackPressed() {
        int CATEGORY_MODIFIED = 123;

        Intent i = new Intent();
        if(categoryNameModified){
            setResult(CATEGORY_MODIFIED,i);
        }else{
            setResult(Activity.RESULT_OK,i);
        }
        finish();
    }


    @SuppressLint("SetTextI18n")
    public void getMemories(){
        try{
            runOnUiThread(()->{
                memoryList.setAdapter(null);
                messageTextView.setVisibility(View.GONE);
                categoryProgressIndicator.setVisibility(View.VISIBLE);
            });
            GetMemoriesInCategoryTask task = new GetMemoriesInCategoryTask(this, categoryId);
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                runOnUiThread(()->{
                    messageTextView.setVisibility(View.VISIBLE);
                    if(task.getError().contains("Unable to resolve host")){
                        messageTextView.setText("Problem with the Internet connection");
                    }else if(task.getError().contains("timeout")){
                        messageTextView.setText("Connection timed out");
                    }else{
                        messageTextView.setText("A problem occurred");
                    }
                    categoryProgressIndicator.setVisibility(View.GONE);
                });
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            final CategoryActivity activity = this;
            runOnUiThread(() -> {
                adapter = new MemoryListAdapter(
                        getApplicationContext(),
                        memories,
                        activity
                );
                categoryProgressIndicator.setVisibility(View.GONE);
                memoryList.setAdapter(adapter);
                memoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            });
        }catch (Exception e){
            runOnUiThread(()->categoryProgressIndicator.setVisibility(View.GONE));
            e.printStackTrace();
        }
    }

    public void openChangeCategoryNameDialog(){
        ChangeCategoryNameDialog dialog = new ChangeCategoryNameDialog(getIntent().getStringExtra("category"));
        dialog.show(getSupportFragmentManager(), "change category name dialog");
    }
}
