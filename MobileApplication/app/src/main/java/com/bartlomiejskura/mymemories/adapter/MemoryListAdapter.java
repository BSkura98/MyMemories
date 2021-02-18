package com.bartlomiejskura.mymemories.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.EditMemoryActivity;
import com.bartlomiejskura.mymemories.MemoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.DeleteMemoryTask;
import com.bartlomiejskura.mymemories.task.EditMemoryTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    private Context context;
    private List<Memory> memories;
    private Activity activity;
    private List<Memory> hidden = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public MemoryListAdapter(Context context, List<Memory> memories, Activity activity) {
        this.context = context;
        this.memories = memories;
        this.activity = activity;
        sort(memories);
        sharedPreferences = activity.getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
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
        holder.memoryDate.setText(getFormattedDate(memories.get(position).getDate()));

        if(memories.get(position).getLongDescription()==null||memories.get(position).getLongDescription().isEmpty()){
            holder.memoryDescription.setVisibility(View.GONE);
        }else{
            holder.memoryDescription.setText(memories.get(position).getLongDescription());
        }

        if(memories.get(position).getImageUrl()==null){
            holder.memoryImage.setVisibility(View.GONE);
        }else{
            holder.memoryImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(memories.get(position).getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.memoryImage);
        }

        if(memories.get(position).getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
            if(memories.get(position).getMemoryFriends()==null||memories.get(position).getMemoryFriends().isEmpty()){
                holder.memoryFriendLinearLayout.setVisibility(View.GONE);
                holder.memoryFriends.setText("");
            }else{
                holder.memoryFriendLinearLayout.setVisibility(View.VISIBLE);
                StringBuilder friendsText= new StringBuilder("with ");
                for(User user:memories.get(position).getMemoryFriends()){
                    friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                }
                friendsText.setLength(friendsText.length()-2);
                holder.memoryFriends.setText(friendsText);

            }
        }else{
            if(memories.get(position).getPublicToFriends()){//jeśli wspomnienie jest oznaczone jako publiczne
                holder.entryRowButtons.setVisibility(View.GONE);

                User memoryOwner= memories.get(position).getMemoryOwner();
                StringBuilder friendsText= new StringBuilder(memoryOwner.getFirstName()+" "+ memoryOwner.getLastName());
                if(memories.get(position).getMemoryFriends().size()>0){//jeśli wspomnienie jest wspólne, lecz znajomy nie oznaczył konkretnego użytkownika w nim
                    friendsText.append(" with ");
                    for(User user:memories.get(position).getMemoryFriends()){
                        friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                    }
                    friendsText.setLength(friendsText.length()-2);
                }
                holder.memoryFriends.setText(friendsText);
            }else{//jeśli jest to wspólne wspomnienie
                holder.memoryFriendLinearLayout.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.GONE);
                holder.editButton.setText("Untag yourself");

                User memoryOwner= memories.get(position).getMemoryOwner();
                StringBuilder friendsText= new StringBuilder(memoryOwner.getFirstName()+" "+ memoryOwner.getLastName() + " with you");
                if(memories.get(position).getMemoryFriends().size()>0){
                    friendsText.append(" and ");
                }
                for(User user:memories.get(position).getMemoryFriends()){
                    friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                }
                friendsText.setLength(friendsText.length()-2);
                holder.memoryFriends.setText(friendsText);
            }
        }
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    public void setMemoryPriority(int memoryPriority){
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

    private void sort(List<Memory> memories){
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

    private String getFormattedDate(String date){
        String[] dateElements = date.replace("T", " ").replace("-", " ").replace(":"," ").split(" ");
        return dateElements[2]+"-"+dateElements[1]+"-"+dateElements[0]+" "+dateElements[3]+":"+dateElements[4]+":"+dateElements[5];
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memoryTitle, memoryDate, memoryDescription, memoryFriends;
        Button deleteButton, editButton;
        LinearLayout memoryLinearLayout, memoryFriendLinearLayout, entryRowButtons;
        ImageView memoryImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            memoryTitle = itemView.findViewById(R.id.memoryTitle);
            memoryDate = itemView.findViewById(R.id.memoryDate);
            memoryDescription = itemView.findViewById(R.id.memoryDescription);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            memoryLinearLayout = itemView.findViewById(R.id.memoryLinearLayout);
            memoryImage = itemView.findViewById(R.id.memoryImage);
            memoryFriendLinearLayout = itemView.findViewById(R.id.memoryFriendsLinearLayout);
            memoryFriends = itemView.findViewById(R.id.memoryFriends);
            entryRowButtons = itemView.findViewById(R.id.entryRowButtons);

            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    deleteMemory(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(memories.get(getAdapterPosition()).getMemoryOwner().getId().equals(sharedPreferences.getLong("userId",0))){
                        editMemory(getAdapterPosition());
                    }else{
                        untagYourselfFromMemory();
                    }
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
            i.putExtra("imageUrl", memories.get(position).getImageUrl());
            i.putExtra("isMemoryPublic", memories.get(position).getPublicToFriends());

            Gson gson = new Gson();
            i.putExtra("memoryFriends", gson.toJson(memories.get(position).getMemoryFriends()));
            List<Tag> tags = new ArrayList<>(memories.get(position).getTags());
            i.putExtra("categories", gson.toJson(tags));
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
            i.putExtra("imageUrl", memories.get(position).getImageUrl());
            i.putExtra("memoryFriends", memoryFriends.getText());

            Gson gson = new Gson();
            List<Tag> tags = new ArrayList<>(memories.get(position).getTags());
            i.putExtra("categories", gson.toJson(tags));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(i);
        }

        private void untagYourselfFromMemory(){
            memories.get(getAdapterPosition()).removeMemoryFriend(sharedPreferences.getLong("userId",0));

            try{
                EditMemoryTask editMemoryTask = new EditMemoryTask(activity, memories.get(getAdapterPosition()));
                Boolean editMemoryResult = editMemoryTask.execute().get();
                if(editMemoryResult){
                    memories.remove(getAdapterPosition());
                    //recycler.removeViewAt(position);
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), memories.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
