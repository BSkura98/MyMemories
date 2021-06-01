package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.CategoryListAdapter;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.task.GetCategoriesTask;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private RecyclerView categoryList;
    private CircularProgressIndicator categoriesProgressIndicator;
    private TextView messageTextView;

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
        categoriesProgressIndicator = view.findViewById(R.id.categoriesProgressIndicator);
        messageTextView = view.findViewById(R.id.categoriesMessageTextView);
    }

    private void prepareViews(){
        //recycler view with category list
        new Thread(this::getCategories).start();

        //TextView with text "No categories"
        messageTextView.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void getCategories(){
        try{
            if(getActivity()==null){
                throw new NullPointerException();
            }
            getActivity().runOnUiThread(()->{
                categoryList.setAdapter(null);
                messageTextView.setVisibility(View.GONE);
                categoriesProgressIndicator.setVisibility(View.VISIBLE);
            });
            GetCategoriesTask task = new GetCategoriesTask(getActivity());
            Category[] categoryArray = task.execute().get();
            if(categoryArray ==null){
                getActivity().runOnUiThread(()->{
                    messageTextView.setVisibility(View.VISIBLE);
                    if(task.getError().contains("Unable to resolve host")){
                        messageTextView.setText("Problem with the Internet connection");
                    }else if(task.getError().contains("timeout")){
                        messageTextView.setText("Connection timed out");
                    }else{
                        messageTextView.setText("A problem occurred");
                    }
                    categoriesProgressIndicator.setVisibility(View.GONE);
                });
                return;
            }
            final List<Category> categories = new ArrayList<>(Arrays.asList(categoryArray));
            getActivity().runOnUiThread(() -> {
                if(categories.isEmpty()){
                    messageTextView.setText("No categories");
                    messageTextView.setVisibility(View.VISIBLE);
                    categoriesProgressIndicator.setVisibility(View.GONE);
                }else{
                    //for(Category category:categories){
                    //    if(category.getMemories().size()==0){
                    //        categories.remove(category);
                    //    }
                    //}
                    for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext(); ) {
                        Category category = iterator.next();
                        if (category.getMemories().size()==0) {
                            iterator.remove();
                        }
                    }
                    adapter = new CategoryListAdapter(
                            getContext(),
                            categories,
                            getActivity()
                    );
                    adapter.setFragment(this);
                    categoriesProgressIndicator.setVisibility(View.GONE);
                    categoryList.setAdapter(adapter);
                    categoryList.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            });
        }catch (Exception e){
            if(getActivity()!=null){
                getActivity().runOnUiThread(()->categoriesProgressIndicator.setVisibility(View.GONE));
            }
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int LAUNCH_SECOND_ACTIVITY = 1;
        int CATEGORY_MODIFIED = 123;

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == CATEGORY_MODIFIED){
                new Thread(this::getCategories).start();
            }
        }
    }
}
