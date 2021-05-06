package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
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
            GetCategoriesTask task = new GetCategoriesTask(getActivity());
            Category[] categoryArray = task.execute().get();
            if(categoryArray ==null){
                getActivity().runOnUiThread(()->{
                    messageTextView.setVisibility(View.VISIBLE);
                    if(task.getError().contains("Unable to resolve host")){
                        messageTextView.setText("Problem with the Internet connection");
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
                    adapter = new CategoryListAdapter(
                            getContext(),
                            categories,
                            getActivity()
                    );
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
}
