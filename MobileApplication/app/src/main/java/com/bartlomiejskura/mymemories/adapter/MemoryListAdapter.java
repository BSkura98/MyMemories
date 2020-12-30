package com.bartlomiejskura.mymemories.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.EditMemoryActivity;
import com.bartlomiejskura.mymemories.MemoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.DeleteMemoryTask;

import java.util.List;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    private Context context;
    private List<Memory> memories;
    private Activity activity;

    public MemoryListAdapter(Context context, List<Memory> memories, Activity activity) {
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
        holder.memoryTitle.setText(memories.get(position).getShortDescription());
        holder.memoryDate.setText(memories.get(position).getDate());
        holder.memoryDescription.setText(memories.get(position).getLongDescription());
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memoryTitle, memoryDate, memoryDescription;
        Button deleteButton, editButton;
        LinearLayout memoryLinearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            memoryTitle = itemView.findViewById(R.id.memoryTitle);
            memoryDate = itemView.findViewById(R.id.memoryDate);
            memoryDescription = itemView.findViewById(R.id.memoryDescription);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            memoryLinearLayout = itemView.findViewById(R.id.memoryLinearLayout);

            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    deleteMemory(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editMemory(getAdapterPosition());
                }
            });

            memoryLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMemoryActivity(getAdapterPosition());
                }
            });
        }

        private void deleteMemory(int position){
            Long memoryId = memories.get(position).getId();
            DeleteMemoryTask task = new DeleteMemoryTask(activity, memoryId);
            try{
                Boolean result = task.execute().get();
                if(result){
                    memories.remove(position);
                    //recycler.removeViewAt(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, memories.size());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void editMemory(int position){
            Intent i = new Intent(activity.getApplicationContext(), EditMemoryActivity.class);
            i.putExtra("title", memories.get(position).getShortDescription());
            i.putExtra("description", memories.get(position).getLongDescription());
            i.putExtra("date", memories.get(position).getDate());
            i.putExtra("memoryId", memories.get(position).getId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(i);
        }

        private void startMemoryActivity(int position){
            Intent i = new Intent(activity.getApplicationContext(), MemoryActivity.class);
            i.putExtra("title", memories.get(position).getShortDescription());
            i.putExtra("description", memories.get(position).getLongDescription());
            i.putExtra("date", memories.get(position).getDate());
            i.putExtra("creationDate", memories.get(position).getCreationDate());
            i.putExtra("memoryId", memories.get(position).getId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(i);
        }
    }
}
