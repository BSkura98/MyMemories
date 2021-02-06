package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.EditUserInformationTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private StorageReference storageReference;

    private ImageView avatarImageView;
    private Button changeAvatarButton;
    private Button deleteAvatarButton;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = getContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference("avatars");

        avatarImageView = view.findViewById(R.id.avatarImageView);
        changeAvatarButton = view.findViewById(R.id.changeAvatarButton);
        deleteAvatarButton = view.findViewById(R.id.deleteAvatarButton);

        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        deleteAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfilePicture();
            }
        });

        String avatarUrl = sharedPreferences.getString("avatarUrl", null);
        if(avatarUrl!=null&&!avatarUrl.isEmpty()){
            Picasso.get()
                    .load(avatarUrl)
                    .fit()
                    .centerCrop()
                    .into(avatarImageView);
        }else{
            deleteAvatarButton.setVisibility(View.GONE);
            changeAvatarButton.setText("Select");
        }

        return view;
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void deleteProfilePicture(){
        StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(sharedPreferences.getString("avatarUrl", null));
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Void aVoid) {
                Picasso.get()
                        .load(R.drawable.default_avatar)
                        .fit()
                        .centerCrop()
                        .into(avatarImageView);
                deleteAvatarButton.setVisibility(View.GONE);
                changeAvatarButton.setText("Select");

                try{
                    User user = new User(
                            sharedPreferences.getLong("userId", 0),
                            sharedPreferences.getString("email", null),
                            sharedPreferences.getString("firstName", null),
                            sharedPreferences.getString("lastName", null),
                            sharedPreferences.getString("birthday", null),
                            null);

                    EditUserInformationTask editUserInformationTask =
                            new EditUserInformationTask(
                                    user,
                                    sharedPreferences);
                    Boolean result = editUserInformationTask.execute().get();
                    if(!result){
                        return;
                    }
                }catch (Exception e){
                    System.out.println("ERROR:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){

            Uri imageUri = data.getData();
            final String oldAvatarUrl = sharedPreferences.getString("avatarUrl", null);

            try{
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        deleteAvatarButton.setVisibility(View.VISIBLE);
                                        changeAvatarButton.setText("Change");
                                        Picasso.get()
                                                .load(uri.toString())
                                                .fit()
                                                .centerCrop()
                                                .into(avatarImageView);

                                        try{
                                            User user = new User(
                                                    sharedPreferences.getLong("userId", 0),
                                                    sharedPreferences.getString("email", null),
                                                    sharedPreferences.getString("firstName", null),
                                                    sharedPreferences.getString("lastName", null),
                                                    sharedPreferences.getString("birthday", null),
                                                    uri.toString());


                                            EditUserInformationTask editUserInformationTask =
                                                    new EditUserInformationTask(
                                                            user,
                                                            sharedPreferences);
                                            Boolean result = editUserInformationTask.execute().get();
                                            if(!result){
                                                return;
                                            }
                                            if(oldAvatarUrl!=null&&!oldAvatarUrl.isEmpty()){
                                                StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(oldAvatarUrl);
                                                photoRef.delete();
                                            }
                                        }catch (Exception e){
                                            System.out.println("ERROR:" + e.getMessage());
                                        }
                                    }
                                });

                            }
                        });
            }catch (Exception e){
                System.out.println("ERROR:" + e.getMessage());
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
