package com.bartlomiejskura.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.RecentSearchesAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SearchMemoryActivity extends AppCompatActivity {
    private List<String> recentSearches;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private RecentSearchesAdapter adapter;
    private RecyclerView recentSearchesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_memory);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        recentSearchesList = findViewById(R.id.recentSearchesList);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchResultsActivity(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getRecentSearches();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.refresh();
                }
            });
        }
    }

    public void startSearchResultsActivity(final String query){
        new Thread(new Runnable() {
            @Override
            public void run() {
                addRecentSearch(query);
            }
        }).start();

        Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
        i.putExtra("query", query);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void getRecentSearches() throws JSONException {
        String json = sharedPreferences.getString("recentSearches", null);
        if(json == null){
            recentSearches = new LinkedList<>();
            return;
        }
        JSONArray array = new JSONArray(json);
        String[] recentSearchesArray = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            recentSearchesArray[i] = array.getString(i);
            //JSONObject object = array.getJSONObject(i);
            //recentSearchesArray[i] = gson.fromJson(object.toString(), String.class);
        }
        recentSearches = new LinkedList<>(Arrays.asList(recentSearchesArray));

        final SearchMemoryActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new RecentSearchesAdapter(
                        getApplicationContext(),
                        recentSearches,
                        activity
                );
                recentSearchesList.setAdapter(adapter);
                recentSearchesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
    }

    private void saveRecentSearches(){
        String json = gson.toJson(recentSearches);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("recentSearches", json);
        editor.apply();
    }

    private void addRecentSearch(String search){
        recentSearches.remove(search);

        recentSearches.add(0, search);
        if(recentSearches.size()>200){
            recentSearches.remove(recentSearches.size()-1);
        }
        saveRecentSearches();
    }
}
