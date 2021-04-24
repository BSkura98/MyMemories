package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.RecentSearchesAdapter;
import com.bartlomiejskura.mymemories.fragment.AdvancedSearchFragment;
import com.bartlomiejskura.mymemories.fragment.RecentSearchesFragment;
import com.bartlomiejskura.mymemories.fragment.SettingsFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class SearchMemoryActivity extends AppCompatActivity {
    private AdvancedSearchFragment advancedSearchFragment;
    private RecentSearchesFragment recentSearchesFragment;
    private SearchView searchView;
    private Boolean advancedSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_memory);

        loadInstanceState(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar_search);

        advancedSearchFragment = new AdvancedSearchFragment();
        recentSearchesFragment = new RecentSearchesFragment();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
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

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

        changeFragment(advancedSearch);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if(advancedSearchFragment.getDateCalendars()[0]!=null){
                i.putExtra("creationDateStart", sdf.format(advancedSearchFragment.getDateCalendars()[0].getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getDateCalendars()[1]!=null){
                i.putExtra("creationDateEnd", sdf.format(advancedSearchFragment.getDateCalendars()[1].getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getDateCalendars()[2]!=null){
                i.putExtra("dateStart", sdf.format(advancedSearchFragment.getDateCalendars()[2].getTime()).replace(" ", "T"));
            }
            if(advancedSearchFragment.getDateCalendars()[3]!=null){
                i.putExtra("dateEnd", sdf.format(advancedSearchFragment.getDateCalendars()[3].getTime()).replace(" ", "T"));
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("advancedSearch", advancedSearch);
    }

    private void loadInstanceState(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            advancedSearch = savedInstanceState.getBoolean("advancedSearch");
        }
    }
}
