package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bartlomiejskura.mymemories.ChangePasswordActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.EditUserInformationTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private StorageReference storageReference;
    private Boolean firstNameEditionActive = false, secondNameEditionActive = false, emailEditionActive = false;
    private Calendar calendar = Calendar.getInstance();

    private ImageView avatarImageView;
    private Button changeAvatarButton, deleteAvatarButton, firstNameButton, secondNameButton, emailButton, birthdayButton, changePasswordButton;
    private EditText firstNameEditText, secondNameEditText, emailEditText;
    private TextInputLayout textInputLayout, textInputLayout2, textInputLayout3;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bindViews(view);

        sharedPreferences = getContext().getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference("avatars");

        setListeners();
        initViews();

        return view;
    }

    private void bindViews(View view){
        avatarImageView = view.findViewById(R.id.avatarImageView);
        changeAvatarButton = view.findViewById(R.id.changeAvatarButton);
        deleteAvatarButton = view.findViewById(R.id.deleteAvatarButton);
        firstNameButton = view.findViewById(R.id.firstNameButton);
        secondNameButton = view.findViewById(R.id.secondNameButton);
        emailButton = view.findViewById(R.id.emailButton);
        birthdayButton = view.findViewById(R.id.birthdayButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        secondNameEditText = view.findViewById(R.id.secondNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        textInputLayout = view.findViewById(R.id.textInputLayout);
        textInputLayout2 = view.findViewById(R.id.textInputLayout2);
        textInputLayout3 = view.findViewById(R.id.textInputLayout3);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
    }

    @SuppressLint("SetTextI18n")
    private void setListeners(){
        changeAvatarButton.setOnClickListener(v -> openFileChooser());
        deleteAvatarButton.setOnClickListener(v -> deleteProfilePicture());
        firstNameButton.setOnClickListener(v -> {
            if(firstNameEditionActive){
                new Thread(()-> {
                    editUserInformation(
                        sharedPreferences.getLong("userId", 0),
                        sharedPreferences.getString("email", null),
                        firstNameEditText.getText().toString(),
                        sharedPreferences.getString("lastName", null),
                        sharedPreferences.getString("birthday", null),
                        sharedPreferences.getString("avatarUrl", null));
                    firstNameEditText.setText(sharedPreferences.getString("firstName", null));
                })
                .start();
            }
            setEditTextEditable(firstNameEditText, textInputLayout, firstNameButton,!firstNameEditionActive);
            setEditTextEditable(secondNameEditText, textInputLayout2, secondNameButton, false);
            setEditTextEditable(emailEditText, textInputLayout3, emailButton,false);
            firstNameEditionActive=!firstNameEditionActive;
            secondNameEditionActive=false;
            emailEditionActive=false;
            secondNameEditText.setText(sharedPreferences.getString("lastName", ""));
            emailEditText.setText(sharedPreferences.getString("email",""));
        });
        secondNameButton.setOnClickListener(v -> {
            if(secondNameEditionActive){
                new Thread(()-> {
                    editUserInformation(
                            sharedPreferences.getLong("userId", 0),
                            sharedPreferences.getString("email", null),
                            sharedPreferences.getString("firstName", null),
                            secondNameEditText.getText().toString(),
                            sharedPreferences.getString("birthday", null),
                            sharedPreferences.getString("avatarUrl", null));
                    secondNameEditText.setText(sharedPreferences.getString("lastName", null));
                })
                        .start();
            }
            setEditTextEditable(firstNameEditText, textInputLayout,firstNameButton,false);
            setEditTextEditable(secondNameEditText, textInputLayout2,secondNameButton,!secondNameEditionActive);
            setEditTextEditable(emailEditText, textInputLayout3,emailButton,false);
            firstNameEditionActive=false;
            secondNameEditionActive=!secondNameEditionActive;
            emailEditionActive=false;
            firstNameEditText.setText(sharedPreferences.getString("firstName", ""));
            emailEditText.setText(sharedPreferences.getString("email",""));
        });
        emailButton.setOnClickListener(v -> {
            if(emailEditionActive){
                new Thread(() -> {
                    editUserInformation(
                        sharedPreferences.getLong("userId", 0),
                        emailEditText.getText().toString(),
                        sharedPreferences.getString("firstName", null),
                        sharedPreferences.getString("lastName", null),
                        sharedPreferences.getString("birthday", null),
                        sharedPreferences.getString("avatarUrl", null));
                    emailEditText.setText(sharedPreferences.getString("email", null));
                }).start();
            }
            setEditTextEditable(firstNameEditText, textInputLayout,firstNameButton,false);
            setEditTextEditable(secondNameEditText, textInputLayout2,secondNameButton,false);
            setEditTextEditable(emailEditText, textInputLayout3,emailButton,!emailEditionActive);
            firstNameEditionActive=false;
            secondNameEditionActive=false;
            emailEditionActive=!emailEditionActive;
            firstNameEditText.setText(sharedPreferences.getString("firstName",""));
            secondNameEditText.setText(sharedPreferences.getString("lastName", ""));
        });
        birthdayButton.setOnClickListener(v -> selectDate());
        changePasswordButton.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class)));
    }

    @SuppressLint("SetTextI18n")
    private void initViews(){
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

        firstNameEditText.setText(sharedPreferences.getString("firstName", ""));
        secondNameEditText.setText(sharedPreferences.getString("lastName", ""));
        emailEditText.setText(sharedPreferences.getString("email", ""));
        firstNameEditText.setEnabled(false);
        secondNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);

        setEditTextEditable(firstNameEditText, textInputLayout, firstNameButton,false);
        setEditTextEditable(secondNameEditText, textInputLayout2, secondNameButton, false);
        setEditTextEditable(emailEditText, textInputLayout3, emailButton,false);

        String date = sharedPreferences.getString("birthday", "");
        calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7))-1, Integer.parseInt(date.substring(8, 10)), Integer.parseInt(date.substring(11, 13)), Integer.parseInt(date.substring(14, 16)));
        birthdayButton.setText(date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4));
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

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    private void setEditTextEditable(EditText editText, TextInputLayout textInputLayout, Button button, Boolean editable){
        if (!editable) {
            editText.setEnabled(false);
            textInputLayout.setBoxStrokeWidth(0);
            textInputLayout.setBoxStrokeWidthFocused(0);
            editText.setTextColor(Color.parseColor("#000000"));


            button.setText("Edit");
        } else {
            editText.setEnabled(true);
            editText.requestFocus();
            button.setText("Confirm");
            textInputLayout.setBoxStrokeWidthFocused(8);
        }
    }

    private void editUserInformation(Long id, String email, String firstName, String lastName, String birthday, String uri){
        User user = new User(
                id,
                email,
                firstName,
                lastName,
                birthday,
                uri);


        EditUserInformationTask editUserInformationTask =
                new EditUserInformationTask(
                        user,
                        sharedPreferences);
        try {
            editUserInformationTask.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void selectDate() {
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year1, month1, date) -> {

            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DATE, date);
            String dateText = DateFormat.format("dd-MM-yyyy", calendar).toString();

            birthdayButton.setText(dateText);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            new Thread(() -> editUserInformation(
                    sharedPreferences.getLong("userId", 0),
                    sharedPreferences.getString("email", null),
                    sharedPreferences.getString("firstName", null),
                    sharedPreferences.getString("lastName", null),
                    sdf.format(calendar.getTime()).replace(" ", "T"),
                    sharedPreferences.getString("avatarUrl", null)))
                    .start();
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }
}
