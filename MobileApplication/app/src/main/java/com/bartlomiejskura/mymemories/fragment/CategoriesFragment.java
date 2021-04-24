package com.bartlomiejskura.mymemories.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.CategoryListAdapter;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.task.GetCategoriesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private RecyclerView categoryList;

    private CategoryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        findViews(view);
        prepareViews();

        return view;
    }

    private void findViews(View view){
        categoryList = view.findViewById(R.id.categoryList);
    }

    private void prepareViews(){
        //recycler view with category list
        new Thread(this::getCategories).start();
    }

    private void getCategories(){
        try{
            GetCategoriesTask task = new GetCategoriesTask(getActivity());
            Category[] categoryArray = task.execute().get();
            if(categoryArray ==null){
                return;
            }
            final List<Category> categories = new ArrayList<>(Arrays.asList(categoryArray));
            getActivity().runOnUiThread(() -> {
                adapter = new CategoryListAdapter(
                        getContext(),
                        categories,
                        getActivity()
                );
                categoryList.setAdapter(adapter);
                categoryList.setLayoutManager(new LinearLayoutManager(getContext()));
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
