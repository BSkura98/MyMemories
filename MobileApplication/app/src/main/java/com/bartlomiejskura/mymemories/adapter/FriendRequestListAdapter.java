package com.bartlomiejskura.mymemories.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.MainActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.ConfirmFriendRequestTask;
import com.bartlomiejskura.mymemories.task.RemoveFriendRequestTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.MyViewHolder> {
    private Context context;
    private List<User> users;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private boolean friendRequestsByOtherUsers;

    public FriendRequestListAdapter(Context context, List<User> users, Activity activity, boolean friendRequestsByOtherUsers){
        this.context = context;
        this.users = users;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        this.friendRequestsByOtherUsers = friendRequestsByOtherUsers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_friend_request, parent,false);
        return new FriendRequestListAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameTextView.setText(users.get(position).getFirstName().concat(" ").concat(users.get(position).getLastName()));
        holder.birthdateTextView.setText("date of birth: "+getFormattedDate(users.get(position).getBirthday()));

        if(users.get(position).getAvatarUrl()!=null){
            Picasso.get()
                    .load(users.get(position).getAvatarUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.avatarImageView);
        }else{
            Picasso.get()
                    .load(R.drawable.default_avatar)
                    .fit()
                    .centerCrop()
                    .into(holder.avatarImageView);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private String getFormattedDate(String date){
        String[] dateElements = date.replace("T", " ").replace("-", " ").replace(":"," ").split(" ");
        return dateElements[2]+"-"+dateElements[1]+"-"+dateElements[0];
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, birthdateTextView;
        ImageView avatarImageView;
        Button confirmButton, deleteButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            birthdateTextView = itemView.findViewById(R.id.birthdateTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            if(friendRequestsByOtherUsers){
                confirmButton.setOnClickListener(v -> confirmFriendRequest(getAdapterPosition()));
            }else{
                confirmButton.setVisibility(View.GONE);
            }

            deleteButton.setOnClickListener(v -> deleteFriendRequest(getAdapterPosition()));
        }

        private void confirmFriendRequest(int position){
            try{
                ConfirmFriendRequestTask task = new ConfirmFriendRequestTask(activity,
                        sharedPreferences.getString("email", ""),
                        users.get(position).getId());
                Boolean result = task.execute().get();
                if(result){
                    users.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, users.size());
                }else{
                    if(activity instanceof MainActivity){
                        if(task.getError().contains("Unable to resolve host")){
                            ((MainActivity)activity).showSnackbar("Problem with the Internet connection");
                        }else{
                            ((MainActivity)activity).showSnackbar("A problem occurred");
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void deleteFriendRequest(int position){
            RemoveFriendRequestTask task;
            try{
                if(friendRequestsByOtherUsers){
                    task = new RemoveFriendRequestTask(activity,
                            sharedPreferences.getString("email", ""),
                            users.get(position).getEmail());
                }else{
                    task = new RemoveFriendRequestTask(activity,
                            users.get(position).getEmail(),
                            sharedPreferences.getString("email", ""));
                }
                Boolean result = task.execute().get();
                if(result){
                    users.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, users.size());
                }else{
                    if(activity instanceof MainActivity){
                        if(task.getError().contains("Unable to resolve host")){
                            ((MainActivity)activity).showSnackbar("Problem with the Internet connection");
                        }else{
                            ((MainActivity)activity).showSnackbar("A problem occurred");
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
