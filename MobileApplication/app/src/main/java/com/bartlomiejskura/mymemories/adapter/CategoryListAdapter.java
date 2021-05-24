package com.bartlomiejskura.mymemories.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.CategoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;

import java.util.Collections;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categories;
    private Activity activity;
    private Fragment fragment;

    public CategoryListAdapter(Context context, List<Category> categories, Activity activity) {
        this.context = context;
        this.categories = categories;
        this.activity = activity;
        sort(categories);
    }

    @NonNull
    @Override
    public CategoryListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_category, parent,false);
        return new CategoryListAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.MyViewHolder holder, int position) {
        holder.categoryName.setText(categories.get(position).getName());
        List<Memory> memories = categories.get(position).getMemories();
        holder.memoryNumber.setText(String.valueOf(memories == null ? 0 : memories.size()));
        if (memories != null && memories.size() == 1) {
            holder.textView28.setText("memory");
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void sort(List<Category> categories){
        Collections.sort(categories, (category1, category2) -> {
            if(category1.getMemories()==null){
                if(category2.getMemories()==null){
                    return 0;
                }
                return 1;
            }
            if(category2.getMemories()==null){
                return -1;
            }
            int size1 = category1.getMemories().size();
            int size2 = category2.getMemories().size();
            return size1==size2?0:(size1<size2?1:-1);
        });
    }

    public void setFragment(Fragment fragment){
        this.fragment = fragment;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView categoryName, memoryNumber, textView28;
        LinearLayout categoryLinearLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryLinearLayout = itemView.findViewById(R.id.categoryLinearLayout);
            categoryName = itemView.findViewById(R.id.categoryName);
            memoryNumber = itemView.findViewById(R.id.memoryNumber);
            textView28 = itemView.findViewById(R.id.textView28);

            categoryLinearLayout.setOnClickListener(v -> startCategoryActivity(getAdapterPosition()));
        }

        private void startCategoryActivity(int position){
            int LAUNCH_SECOND_ACTIVITY = 1;

            Intent i = new Intent(activity.getApplicationContext(), CategoryActivity.class);
            i.putExtra("category", categories.get(position).getName());
            i.putExtra("categoryId", categories.get(position).getId());
            if(fragment!=null){
                fragment.startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }else{
                activity.startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        }
    }
}
