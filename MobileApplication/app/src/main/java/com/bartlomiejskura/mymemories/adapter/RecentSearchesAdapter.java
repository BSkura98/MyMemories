package com.bartlomiejskura.mymemories.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.SearchMemoryActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecentSearchesAdapter extends RecyclerView.Adapter<RecentSearchesAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<String> recentSearches;
    private List<String> recentSearchesTemp;
    private SearchMemoryActivity activity;

    public RecentSearchesAdapter(Context context, List<String> recentSearches, SearchMemoryActivity activity) {
        this.context = context;
        this.recentSearches = new ArrayList<>(recentSearches);
        this.activity = activity;
        recentSearchesTemp = recentSearches;
    }

    @NonNull
    @Override
    public RecentSearchesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_recent_search, parent,false);
        return new RecentSearchesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchesAdapter.MyViewHolder holder, int position) {
        holder.search.setText(recentSearches.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.min(recentSearches.size(), 20);
    }

    public void refresh(){
        notifyItemRangeChanged(0, recentSearches.size());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new LinkedList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(recentSearchesTemp);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(String search: recentSearchesTemp){
                    if(search.toLowerCase().contains(filterPattern)){
                        filteredList.add(search);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recentSearches.clear();
            recentSearches.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView search;
        LinearLayout recentSearchesLinearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recentSearchesLinearLayout = itemView.findViewById(R.id.searchLinearLayout);
            search = itemView.findViewById(R.id.search);

            recentSearchesLinearLayout.setOnClickListener(v -> activity.startSearchResultsActivity(search.getText().toString()));
        }
    }
}
