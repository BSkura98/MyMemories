package com.bartlomiejskura.mymemories.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.MemoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    private Context context;
    private List<Memory> memories;
    private Activity activity;
    private Fragment fragment;
    private List<Memory> hidden = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

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
        View view = inflater.inflate(R.layout.row_memory, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryListAdapter.MyViewHolder holder, int position) {
        //title
        Typeface typeface = ResourcesCompat.getFont(context,R.font.quando);
        holder.memoryTitle.setTypeface(typeface);
        holder.memoryTitle.setText(memories.get(position).getTitle());

        //date
        if(memories.get(position).getDate().endsWith("0")){
            holder.memoryDate.setText(DateUtil.formatDate(memories.get(position).getDate()));
        }else{
            holder.memoryDate.setText(DateUtil.formatDateTime(memories.get(position).getDate()));
        }

        //card view stroke
        holder.cardView.setStrokeWidth(4);
        holder.cardView.setStrokeColor(activity.getResources().getColor(memories.get(position).getPriority()>=90?R.color.colorAccent:
                (memories.get(position).getPriority()>=50)?R.color.colorAccentVeryLight:R.color.white));

        if(memories.get(position).getDescription()==null||memories.get(position).getDescription().isEmpty()){
            holder.memoryDescription.setVisibility(View.GONE);
        }else{
            holder.memoryDescription.setText(memories.get(position).getDescription());
        }

        if(memories.get(position).getImageUrl()==null){
            holder.memoryImage.setVisibility(View.GONE);
        }else{
            holder.memoryImage.setVisibility(View.VISIBLE);
            Glide.with(activity).load(memories.get(position).getImageUrl()).into(holder.memoryImage);
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
            if(memories.get(position).getIsPublicToFriends()){//jeśli wspomnienie jest oznaczone jako publiczne
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

                User memoryOwner= memories.get(position).getMemoryOwner();
                StringBuilder friendsText= new StringBuilder(memoryOwner.getFirstName()+" "+ memoryOwner.getLastName() + " with you");

                List<User> memoryFriends = new ArrayList<>(memories.get(position).getMemoryFriends());
                for(User user:memoryFriends){
                    if(user.getId().equals(sharedPreferences.getLong("userId", 0))){
                        memoryFriends.remove(user);
                        break;
                    }
                }

                if(memoryFriends.size()>0){
                    friendsText.append(" and ");
                    for(User user:memoryFriends){
                        friendsText.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
                    }
                    friendsText.setLength(friendsText.length()-2);
                }
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
            if(!(memory.getPriority()>=memoryPriority)){
                memoriesToHide.add(memory);
            }
        }
        memories.removeAll(memoriesToHide);
        hidden.addAll(memoriesToHide);
        for(Memory memory:hidden){
            if(memory.getPriority()>=memoryPriority){
                memoriesToShow.add(memory);
            }
        }
        memories.addAll(memoriesToShow);
        hidden.removeAll(memoriesToShow);
        notifyDataSetChanged();

        sort(memories);
    }

    private void sort(List<Memory> memories){
        Collections.sort(memories, (memory1, memory2) -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddd hh:mm:ss");
            try {
                Date strDate1 = sdf.parse(memory1.getDate().replace("T"," "));
                Date strDate2 = sdf.parse(memory2.getDate().replace("T"," "));
                return strDate1.before(strDate2)?1:-1;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public List<Memory> getMemories(){
        return memories;
    }

    public void setFragment(Fragment fragment){
        this.fragment = fragment;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memoryTitle, memoryDate, memoryDescription, memoryFriends;
        LinearLayout memoryLinearLayout, memoryFriendLinearLayout;
        ImageView memoryImage;
        MaterialCardView cardView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            memoryTitle = itemView.findViewById(R.id.memoryTitle);
            memoryDate = itemView.findViewById(R.id.memoryDate);
            memoryDescription = itemView.findViewById(R.id.memoryDescription);
            memoryLinearLayout = itemView.findViewById(R.id.memoryLinearLayout);
            memoryImage = itemView.findViewById(R.id.memoryImage);
            memoryFriendLinearLayout = itemView.findViewById(R.id.memoryFriendsLinearLayout);
            memoryFriends = itemView.findViewById(R.id.memoryFriends);

            memoryLinearLayout.setOnClickListener(v -> startMemoryActivity(getAdapterPosition()));
        }

        private void startMemoryActivity(int position){
            int LAUNCH_SECOND_ACTIVITY = 1;

            Intent i = new Intent(activity.getApplicationContext(), MemoryActivity.class);
            i.putExtra("memory", gson.toJson(memories.get(position)));
            List<Category> categories = new ArrayList<>(memories.get(position).getCategories());
            i.putExtra("categories", gson.toJson(categories));
            if(fragment!=null){
                fragment.startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }else{
                activity.startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        }
    }
}
