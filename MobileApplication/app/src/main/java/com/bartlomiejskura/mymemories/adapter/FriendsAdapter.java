package com.bartlomiejskura.mymemories.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdapter extends ArrayAdapter<User> {
    public FriendsAdapter(Context context, ArrayList<User> userList){
        super(context, 0, userList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @SuppressLint("SetTextI18n")
    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_friend, parent, false
            );
        }
        ImageView avatarImageView = convertView.findViewById(R.id.avatarImageView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView birthdayTextView = convertView.findViewById(R.id.birthdateTextView);

        convertView.findViewById(R.id.requestButton).setVisibility(View.GONE);

        User user = getItem(position);
        if(user!=null){
            if(user.getAvatarUrl()!=null){
                Picasso.get()
                        .load(user.getAvatarUrl())
                        .fit()
                        .centerCrop()
                        .into(avatarImageView);
            }else{
                Picasso.get()
                        .load(R.drawable.default_avatar)
                        .fit()
                        .centerCrop()
                        .into(avatarImageView);
            }
            nameTextView.setText(user.getFirstName().concat(" ").concat(user.getLastName()));
            birthdayTextView.setText("date of birth: "+getFormattedDate(user.getBirthday()));
        }
        return convertView;
    }

    private String getFormattedDate(String date){
        String[] dateElements = date.replace("T", " ").replace("-", " ").replace(":"," ").split(" ");
        return dateElements[2]+"-"+dateElements[1]+"-"+dateElements[0];
    }
}
