package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.fragment.AdvancedSearchFragment;
import com.bartlomiejskura.mymemories.fragment.RecentSearchesFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SearchMemoryActivity extends AppCompatActivity {
    private SearchView searchView;
    private Toolbar toolbar;
    private ImageButton backButton;
    private TextView textView;

    private AdvancedSearchFragment advancedSearchFragment;
    private RecentSearchesFragment recentSearchesFragment;
    private Boolean advancedSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_memory);

        findViews();
        initValues();
        prepareViews();
        setListeners();
        restoreDataAfterRotation(savedInstanceState);

        changeFragment(advancedSearch);
    }

    private void findViews(){
        toolbar = findViewById(R.id.toolbar_search);
        searchView = findViewById(R.id.searchView);
        backButton = findViewById(R.id.backButton);

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        textView = searchView.findViewById(id);
    }

    private void initValues(){
        advancedSearchFragment = new AdvancedSearchFragment();
        recentSearchesFragment = new RecentSearchesFragment();
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //search view
        searchView.setIconifiedByDefault(false);

        //text view
        textView.setTextColor(Color.WHITE);
    }

    private void setListeners(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchResultsActivity(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!advancedSearch){
                    recentSearchesFragment.filter(newText);
                }
                return false;
            }
        });

        backButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("advancedSearch", advancedSearch);
    }


    public void startSearchResultsActivity(final String query){
        new Thread(() -> {
            if(!query.isEmpty()){
                recentSearchesFragment.addRecentSearch(query);
            }
        }).start();

        Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
        i.putExtra("keyword", query);

        if(advancedSearch){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(advancedSearchFragment.getDateCalendars()[0]!=null){
                Calendar calendar = advancedSearchFragment.getDateCalendars()[0];
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                i.putExtra("creationDateStart", sdf.format(calendar.getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getDateCalendars()[1]!=null){
                Calendar calendar = advancedSearchFragment.getDateCalendars()[1];
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                i.putExtra("creationDateEnd", sdf.format(calendar.getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getDateCalendars()[2]!=null){
                Calendar calendar = advancedSearchFragment.getDateCalendars()[2];
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                i.putExtra("dateStart", sdf.format(calendar.getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getDateCalendars()[3]!=null){
                Calendar calendar = advancedSearchFragment.getDateCalendars()[3];
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                i.putExtra("dateEnd", sdf.format(calendar.getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getPublicToFriends()!=null){
                i.putExtra("publicToFriends", advancedSearchFragment.getPublicToFriends()?"true":"false");
            }
            if(advancedSearchFragment.getSharedMemories()!=null){
                i.putExtra("sharedMemories", advancedSearchFragment.getSharedMemories()?"true":"false");
            }
            if(advancedSearchFragment.getWithImage()!=null){
                i.putExtra("withImage", advancedSearchFragment.getWithImage()?"true":"false");
            }

            StringBuilder priorities = new StringBuilder();
            for(Integer priority:advancedSearchFragment.getPriorityList()){
                priorities.append(priority.toString()).append("+");
            }
            i.putExtra("memoryPriorities", priorities.toString());

            StringBuilder categories = new StringBuilder();
            for(String category:advancedSearchFragment.getCategories()){
                categories.append(category).append("+");
            }
            i.putExtra("categories", categories.toString());
        }

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void changeFragment(Boolean advancedSearch){
        this.advancedSearch = advancedSearch;
        if(advancedSearch){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, advancedSearchFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, recentSearchesFragment).commit();
            recentSearchesFragment.filter(searchView.getQuery().toString());
        }
    }

    public String getQuery(){
        return searchView.getQuery().toString();
    }

    private void restoreDataAfterRotation(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            advancedSearch = savedInstanceState.getBoolean("advancedSearch");
        }
    }
}
