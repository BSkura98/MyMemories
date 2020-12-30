package com.bartlomiejskura.mymemories.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.Memory;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    private Context context;
    private Memory[] memories;
    private Activity activity;

    public MemoryListAdapter(Context context, Memory[] memories, Activity activity) {
        this.context = context;
        this.memories = memories;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MemoryListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.entry_row, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryListAdapter.MyViewHolder holder, int position) {
        holder.memoryTitle.setText(memories[position].getShortDescription());
        holder.memoryDate.setText(memories[position].getDate());
        holder.memoryDescription.setText(memories[position].getLongDescription());
    }

    @Override
    public int getItemCount() {
        return memories.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memoryTitle, memoryDate, memoryDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            memoryTitle = itemView.findViewById(R.id.memoryTitle);
            memoryDate = itemView.findViewById(R.id.memoryDate);
            memoryDescription = itemView.findViewById(R.id.memoryDescription);
        }
    }
}
