package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private ConstraintLayout categoryConstraintLayout;

    private MemoryListAdapter adapter;
    private Long categoryId;
    private SharedPreferences sharedPreferences;

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
    }

    private void setListeners(){
        backButton.setOnClickListener(v -> super.onBackPressed());

        editCategoryNameButton.setOnClickListener(v->{
            openChangeCategoryNameDialog();
        });
    }


    @Override
    public void applyCategoryName(String name) {
        try{
            if(name.isEmpty()){
                runOnUiThread(()-> Snackbar.make(categoryConstraintLayout, "Incorrect category name", Snackbar.LENGTH_LONG).show());
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
            name = name.toLowerCase();
            EditCategoryTask task = new EditCategoryTask(this, new Category(categoryId, name, new User(sharedPreferences.getLong("userId", 0),sharedPreferences.getString("email","")), adapter.getMemories()));
            boolean result = task.execute().get();
            String finalName = name;
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
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    public void getMemories(){
        try{
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
