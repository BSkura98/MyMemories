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
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.task.DeleteMemoryTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    private Context context;
    private List<Memory> memories;
    private Activity activity;
    private int memoryPriority = 0;
    private List<Memory> hidden = new ArrayList<>();

    public MemoryListAdapter(Context context, List<Memory> memories, Activity activity) {
        this.context = context;
        this.memories = memories;
        this.activity = activity;
        sort(memories);
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

    public void setMemoryPriority(int memoryPriority){
        this.memoryPriority = memoryPriority;
        List<Memory> memoriesToHide = new ArrayList<>();
        List<Memory> memoriesToShow = new ArrayList<>();
        for(Memory memory:memories){
            if(!(memory.getMemoryPriority()>=memoryPriority)){
                memoriesToHide.add(memory);
            }
        }
        memories.removeAll(memoriesToHide);
        hidden.addAll(memoriesToHide);
        for(Memory memory:hidden){
            if(memory.getMemoryPriority()>=memoryPriority){
                memoriesToShow.add(memory);
            }
        }
        memories.addAll(memoriesToShow);
        hidden.removeAll(memoriesToShow);
        notifyDataSetChanged();

        sort(memories);
    }

    public void sort(List<Memory> memories){
        Collections.sort(memories, new Comparator<Memory>() {
            @Override
            public int compare(Memory memory1, Memory memory2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddd hh:mm:ss");
                try {
                    Date strDate1 = sdf.parse(memory1.getDate().replace("T"," "));
                    Date strDate2 = sdf.parse(memory2.getDate().replace("T"," "));
                    return strDate1.before(strDate2)?1:-1;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
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
            i.putExtra("memoryPriority", memories.get(position).getMemoryPriority());

            Tag tag = memories.get(position).getTag();
            if(tag!=null){
                i.putExtra("category", memories.get(position).getTag().getName());
            }
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
            i.putExtra("memoryPriority", memories.get(position).getMemoryPriority());

            Tag tag = memories.get(position).getTag();
            if(tag!=null){
                i.putExtra("category", memories.get(position).getTag().getName());
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(i);
        }
    }
}
