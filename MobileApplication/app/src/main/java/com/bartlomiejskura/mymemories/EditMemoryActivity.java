package com.bartlomiejskura.mymemories;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.CreateOrGetTagTask;
import com.bartlomiejskura.mymemories.task.EditMemoryTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditMemoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView memoryImage;
    private ImageButton deleteImageButton;
    private LinearLayout peopleList;
    private Button addPersonButton, dateButton, timeButton;
    private TextInputLayout titleInputLayout;

    private Memory memory = new Memory();
    private int day, month, year, hour, minute;
    private SharedPreferences sharedPreferences;
    private int memoryPriority;
    private StorageReference storageReference;
    private List<User> memoryFriends = new ArrayList<>();
    String memoryFriendsEmails;
    private Calendar calendar = Calendar.getInstance();;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);

        final String title = getIntent().getStringExtra("title");
        final String description = getIntent().getStringExtra("description");
        final String date = getIntent().getStringExtra("date");
        final String category = getIntent().getStringExtra("category");
        memoryPriority = getIntent().getIntExtra("memoryPriority", 0);
        final String imageUrl = getIntent().getStringExtra("imageUrl");
        memoryFriendsEmails = getIntent().getStringExtra("memoryFriends");

        final EditText titleEditText = findViewById(R.id.titleEditText);
        final EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        final EditText categoryEditText = findViewById(R.id.categoryEditText);
        final Button editMemoryButton = findViewById(R.id.editMemoryButton);
        final Button selectImageButton = findViewById(R.id.selectImageButton);
        deleteImageButton = findViewById(R.id.deleteImageButton);
        final Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        memoryImage = findViewById(R.id.memoryImage);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        peopleList = findViewById(R.id.people_list);
        addPersonButton = findViewById(R.id.addPersonButton);
        titleInputLayout = findViewById(R.id.textInputLayout);

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        if(category !=null&&!category.isEmpty()){
            categoryEditText.setText(category);
        }
        calendar.set(Integer.parseInt(date.substring(8, 10)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(11, 13)), Integer.parseInt(date.substring(14, 16)));
        dateButton.setText(date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4));
        timeButton.setText(date.substring(11, 16));
        if(imageUrl!=null){
            memory.setImageUrl(imageUrl);

            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(memoryImage);
        }else{
            deleteImageButton.setVisibility(View.GONE);
            memoryImage.setVisibility(View.GONE);
        }
        initializeMemoryFriendsList();


        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(this);
        prioritySpinner.setSelection(memoryPriority==10?2:(memoryPriority==50?1:0));

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        editMemoryButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        editMemory(titleEditText.getText().toString(), descriptionEditText.getText().toString(), categoryEditText.getText().toString());
                    }
                }).start();
            }
        });

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory.setImageUrl(null);
                deleteImageButton.setVisibility(View.GONE);
                memoryImage.setVisibility(View.GONE);
            }
        });

        addPersonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addView();
            }
        });
    }

    private void initializeMemoryFriendsList(){
        String[] emails =memoryFriendsEmails.split(";");
        for(String email:emails){
            EditText emailEditText = addView().findViewById(R.id.emailEditText);
            emailEditText.setText(email);
        }
    }


    private void editMemory(String title, String description, String category){
        if (title.isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    titleInputLayout.setError("Title field cannot be empty!");
                }
            });
            return;
        }

        if (!addMemoryFriends()) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(year, month, day, hour, minute);

        Long memoryOwnerId = sharedPreferences.getLong("userId", 0);
        Tag tag = null;
        if(!category.isEmpty()){
            tag = getCategory(category);
            if(tag==null){
                return;
            }
        }
        //Memory memory = new Memory(getIntent().getLongExtra("memoryId", 0), title, description, sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"), sdf.format(calendar.getTime()).replace(" ", "T"), new User(memoryOwnerId), memoryPriority, tag);
        memory.setId(getIntent().getLongExtra("memoryId", 0));
        memory.setShortDescription(title);
        memory.setLongDescription(description);
        memory.setCreationDate(sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"));
        memory.setDate(sdf.format(calendar.getTime()).replace(" ", "T"));
        memory.setMemoryOwner(new User(memoryOwnerId));
        memory.setMemoryPriority(memoryPriority);
        memory.setTag(tag);
        memory.setMemoryFriends(memoryFriends);

        final EditMemoryActivity activity = this;

        try{
            EditMemoryTask editMemoryTask = new EditMemoryTask(activity, memory);
            Boolean editMemoryResult = editMemoryTask.execute().get();
            if(!editMemoryResult){
                return;
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("fragmentToLoad", "recentEntriesFragment");
            startActivity(i);
        }catch (Exception e){
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    private Tag getCategory(String category){
        final EditMemoryActivity activity = this;

        try{
            CreateOrGetTagTask task = new CreateOrGetTagTask(activity, category);
            return task.execute().get();
        }catch (Exception e){
            System.out.println("ERROR:" + e.getMessage());
            return null;
        }
    }

    private void selectDate() {
        //Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int date) {

                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.DATE, date);
                String dateText = DateFormat.format("dd-MM-yyyy", calendar).toString();

                dateButton.setText(dateText);
                year = year1;
                month = month1;
                day = date;
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void selectTime() {
        //Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour1, int minute1) {
                calendar.set(Calendar.HOUR_OF_DAY, hour1);
                calendar.set(Calendar.MINUTE, minute1);
                String dateText = DateFormat.format("HH:mm", calendar).toString();
                timeButton.setText(dateText);
                hour = hour1;
                minute = minute1;
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        if(selected.equals("High")){
            memoryPriority=90;
        }else if(selected.equals("Medium")){
            memoryPriority = 50;
        }else if(selected.equals("Low")){
            memoryPriority = 10;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Uri imageUri = data.getData();


            try{
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        deleteImageButton.setVisibility(View.VISIBLE);
                                        memoryImage.setVisibility(View.VISIBLE);
                                        memory.setImageUrl(uri.toString());
                                        Picasso.get().load(uri.toString()).into(memoryImage);
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
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private View addView(){
        final View view = getLayoutInflater().inflate(R.layout.row_add_person, null, false);

        EditText emailEditText = view.findViewById(R.id.emailEditText);
        ImageButton deletePersonButton = view.findViewById(R.id.deletePersonButton);

        deletePersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(view);
            }
        });

        peopleList.addView(view);
        return view;
    }

    private void removeView(View view){
        peopleList.removeView(view);
    }

    private boolean addMemoryFriends(){
        memoryFriends.clear();
        boolean result = true;

        for(int i = 0;i <peopleList.getChildCount();i++){
            View view = peopleList.getChildAt(i);

            EditText emailEditText = view.findViewById(R.id.emailEditText);

            if(!emailEditText.getText().toString().equals("")){
                memoryFriends.add(new User(emailEditText.getText().toString().trim()));
            }else{
                result = false;
                break;
            }
        }

        if(!result){
            final Activity activity = this;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Email field cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return result;
    }
}