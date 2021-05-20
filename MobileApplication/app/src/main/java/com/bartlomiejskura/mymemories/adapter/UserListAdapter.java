package com.bartlomiejskura.mymemories.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.AddFriendsActivity;
import com.bartlomiejskura.mymemories.MainActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.RemoveFriendTask;
import com.bartlomiejskura.mymemories.task.SendFriendRequestTask;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private Context context;
    private List<User> users;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private boolean usersAreFriends;

    public UserListAdapter(Context context, List<User> users, Activity activity, boolean usersAreFriends){
        this.context = context;
        this.users = users;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        this.usersAreFriends = usersAreFriends;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(usersAreFriends?R.layout.row_friend:R.layout.row_user, parent,false);
        return new UserListAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Typeface typeface = ResourcesCompat.getFont(context,R.font.quando);
        holder.nameTextView.setTypeface(typeface);

        holder.nameTextView.setText(users.get(position).getFirstName().concat(" ").concat(users.get(position).getLastName()));
        holder.birthdateTextView.setText("date of birth: "+getFormattedDate(users.get(position).getBirthday()));

        if(users.get(position).getAvatarUrl()!=null){
            Glide.with(activity).load(users.get(position).getAvatarUrl()).into(holder.avatarImageView);
        }else{
            Glide.with(activity).load(R.drawable.default_avatar).into(holder.avatarImageView);
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
        Button requestButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            birthdateTextView = itemView.findViewById(R.id.birthdateTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            requestButton = itemView.findViewById(R.id.requestButton);

            if(!usersAreFriends){
                requestButton.setOnClickListener(v -> sendFriendRequest(getAdapterPosition()));
            }else{
                requestButton.setOnClickListener(v -> removeFriend(getAdapterPosition()));
            }
        }

        private void sendFriendRequest(int position){
            try{
                SendFriendRequestTask task = new SendFriendRequestTask(activity,
                        sharedPreferences.getString("email", ""),
                        users.get(position).getId());
                Boolean result = task.execute().get();
                if(result){
                    users.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, users.size());
                }else{
                    if(activity instanceof AddFriendsActivity){
                        if(task.getError().contains("Unable to resolve host")){
                            ((AddFriendsActivity)activity).showSnackbar("Problem with the Internet connection");
                        }else{
                            ((AddFriendsActivity)activity).showSnackbar("A problem occurred");
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void removeFriend(int position){
            RemoveFriendTask task = new RemoveFriendTask(activity,
                    sharedPreferences.getString("email", ""),
                    users.get(position).getId());
            try{
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
