package com.bartlomiejskura.mymemories.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.SearchMemoryActivity;
import com.bartlomiejskura.mymemories.adapter.RecentSearchesAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RecentSearchesFragment extends Fragment {
    private List<String> recentSearches;
    private SharedPreferences sharedPreferences;
    private RecentSearchesAdapter adapter;
    private RecyclerView recentSearchesList;
    private Button advancedSearchButton;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_searches, container, false);

        findViews(view);
        initValues();
        prepareViews();
        setListeners();

        return view;
    }

    private void findViews(View view){
        recentSearchesList = view.findViewById(R.id.recentSearchesList);
        advancedSearchButton = view.findViewById(R.id.advancedSearchButton);
    }

    private void initValues(){
        if(getActivity()==null){
            return;
        }
        sharedPreferences = getActivity().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    private void prepareViews(){
        new Thread(this::getRecentSearches).start();
    }

    private void setListeners(){
        if(getActivity()==null){
            return;
        }
        advancedSearchButton.setOnClickListener(v -> ((SearchMemoryActivity)getActivity()).changeFragment(true));
    }


    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null&& getActivity()!=null){
            getActivity().runOnUiThread(() -> adapter.refresh());
        }
    }


    private void getRecentSearches() {
        String json = sharedPreferences.getString("recentSearches", null);
        if(json == null){
            recentSearches = new LinkedList<>();
        }else{
            JSONArray array = null;
            try {
                array = new JSONArray(json);
                String[] recentSearchesArray = new String[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    recentSearchesArray[i] = array.getString(i);
                }
                recentSearches = new LinkedList<>(Arrays.asList(recentSearchesArray));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

        final SearchMemoryActivity activity = (SearchMemoryActivity)getActivity();
        if(activity==null){
            return;
        }
        activity.runOnUiThread(() -> {
            adapter = new RecentSearchesAdapter(
                    getContext(),
                    recentSearches,
                    activity
            );
            recentSearchesList.setAdapter(adapter);
            recentSearchesList.setLayoutManager(new LinearLayoutManager(getContext()));
            filter(((SearchMemoryActivity)getActivity()).getQuery());
        });
    }

    public void filter(String query){
        if(adapter!=null){
            adapter.getFilter().filter(query);
        }
    }

    private void saveRecentSearches(){
        String json = gson.toJson(recentSearches);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("recentSearches", json);
        editor.apply();
    }

    public void addRecentSearch(String search){
        recentSearches.remove(search);

        recentSearches.add(0, search);
        if(recentSearches.size()>200){
            recentSearches.remove(recentSearches.size()-1);
        }
        saveRecentSearches();
    }
}
