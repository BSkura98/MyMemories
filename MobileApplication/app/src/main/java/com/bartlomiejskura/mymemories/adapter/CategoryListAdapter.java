package com.bartlomiejskura.mymemories.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.CategoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categories;
    private Activity activity;

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
        View view = inflater.inflate(R.layout.category_row, parent,false);
        return new CategoryListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.MyViewHolder holder, int position) {
        holder.categoryName.setText(categories.get(position).getName());
        List<Memory> memories = categories.get(position).getMemories();
        holder.memoryNumber.setText(String.valueOf(memories==null?0:memories.size()));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void sort(List<Category> categories){
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
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
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView categoryName, memoryNumber;
        LinearLayout categoryLinearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryLinearLayout = itemView.findViewById(R.id.categoryLinearLayout);
            categoryName = itemView.findViewById(R.id.categoryName);
            memoryNumber = itemView.findViewById(R.id.memoryNumber);

            categoryLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMemoryActivity(getAdapterPosition());
                }
            });
        }

        private void startMemoryActivity(int position){
            Intent i = new Intent(activity.getApplicationContext(), CategoryActivity.class);
            i.putExtra("category", categories.get(position).getName());
            i.putExtra("categoryId", categories.get(position).getId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(i);
        }
    }
}
