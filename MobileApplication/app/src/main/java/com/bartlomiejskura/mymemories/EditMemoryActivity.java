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
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.CreateOrGetTagsTask;
import com.bartlomiejskura.mymemories.task.EditMemoryTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class EditMemoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView memoryImage;
    private ImageButton deleteImageButton;
    private LinearLayout peopleList;
    private Button addPersonButton, dateButton, timeButton, addCategoryButton;
    private TextInputLayout titleInputLayout;
    private SwitchMaterial makePublicSwitch;
    private ChipGroup chipGroup;

    private Memory memory = new Memory();
    private SharedPreferences sharedPreferences;
    private int memoryPriority;
    private StorageReference storageReference;
    private List<User> memoryFriends = new ArrayList<>();
    private String memoryFriendsEmails;
    private Calendar calendar = Calendar.getInstance();
    private String imageUrl;
    private Boolean makeMemoryPublic = false;
    private List<String> categories = new LinkedList<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);

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
        makePublicSwitch = findViewById(R.id.makePublicSwitch);
        makePublicSwitch.setChecked(makeMemoryPublic);
        chipGroup = findViewById(R.id.chipGroup);
        addCategoryButton = findViewById(R.id.addCategoryButton);


        final String title = getIntent().getStringExtra("title");
        final String description = getIntent().getStringExtra("description");
        final String date = getIntent().getStringExtra("date");
        //final String category = getIntent().getStringExtra("category");
        memoryPriority = getIntent().getIntExtra("memoryPriority", 0);
        imageUrl = getIntent().getStringExtra("imageUrl");
        memoryFriendsEmails = getIntent().getStringExtra("memoryFriends");
        makeMemoryPublic = getIntent().getBooleanExtra("isMemoryPublic", false);

        try {
            JSONArray array = new JSONArray(getIntent().getStringExtra("categories"));
            Gson gson = new Gson();
            String category;

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                category = gson.fromJson(object.toString(), Tag.class).getName();
                initChipCategory(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        /*if(category !=null&&!category.isEmpty()){
            categoryEditText.setText(category);
        }*/
        calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7))-1, Integer.parseInt(date.substring(8, 10)), Integer.parseInt(date.substring(11, 13)), Integer.parseInt(date.substring(14, 16)));
        dateButton.setText(date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4));
        timeButton.setText(date.substring(11, 16));
        if(imageUrl!=null){
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
                        editMemory(titleEditText.getText().toString(), descriptionEditText.getText().toString());
                    }
                }).start();
            }
        });

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(memory.getImageUrl()!=null){
                    deleteImage(memory.getImageUrl(), false);
                }else{
                    deleteImageButton.setVisibility(View.GONE);
                    memoryImage.setVisibility(View.GONE);
                }
            }
        });

        addPersonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addView();
            }
        });

        makePublicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                makeMemoryPublic=!makeMemoryPublic;
            }
        });

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(EditMemoryActivity.this);

                String category = categoryEditText.getText().toString().toLowerCase();
                if(!categories.contains(category)||category.isEmpty()){
                    initChipCategory(category);
                }
                categoryEditText.setText("");
            }
        });
    }

    private void initChipCategory(String category){
        LayoutInflater inflater = LayoutInflater.from(EditMemoryActivity.this);

        Chip chip = (Chip)inflater.inflate(R.layout.chip_category, null, false);
        chip.setText(category);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(v);
                categories.remove(((Chip)v).getText().toString());
            }
        });

        chipGroup.addView(chip);
        categories.add(category);
    }

    private void initializeMemoryFriendsList(){
        String[] emails =memoryFriendsEmails.split(";");
        for(String email:emails){
            EditText emailEditText = addView().findViewById(R.id.emailEditText);
            emailEditText.setText(email);
        }
    }


    private void editMemory(String title, String description){
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

        Long memoryOwnerId = sharedPreferences.getLong("userId", 0);
        List<Tag> tags = null;
        if(!categories.isEmpty()){
            tags = getCategories();
            if(tags==null){
                return;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        memory.setId(getIntent().getLongExtra("memoryId", 0));
        memory.setShortDescription(title);
        memory.setLongDescription(description==null?"":description);
        memory.setCreationDate(sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"));
        memory.setDate(sdf.format(calendar.getTime()).replace(" ", "T"));
        memory.setMemoryOwner(new User(memoryOwnerId));
        memory.setMemoryPriority(memoryPriority);
        memory.setTags(tags);
        memory.setMemoryFriends(memoryFriends);
        memory.setPublicToFriends(makeMemoryPublic);

        final EditMemoryActivity activity = this;

        boolean oldImageToDelete = true;
        if(imageUrl == null || imageUrl.isEmpty()){
            oldImageToDelete = false;
        }else if(memory.getImageUrl()==null){
            memory.setImageUrl(imageUrl);
            oldImageToDelete = false;
        }else if(memory.getImageUrl()!=null){
            oldImageToDelete = true;
        }
        try{
            EditMemoryTask editMemoryTask = new EditMemoryTask(activity, memory);
            Boolean editMemoryResult = editMemoryTask.execute().get();
            if(!editMemoryResult){
                return;
            }
            if(oldImageToDelete){
                deleteImage(imageUrl, true);//usuwanie starego zdjęcia, które wcześniej było przypisane do wspomnienia
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("fragmentToLoad", "recentEntriesFragment");
            startActivity(i);
        }catch (Exception e){
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    private List<Tag> getCategories(){
        final EditMemoryActivity activity = this;

        try{
            CreateOrGetTagsTask task = new CreateOrGetTagsTask(activity, categories);
            Tag[] categoryArray = task.execute().get();
            return new ArrayList<>(Arrays.asList(categoryArray));
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
                                        deleteImage(memory.getImageUrl(), true);
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

    private void deleteImage(String imageUrl, final boolean imageViewVisible){
        if(imageUrl !=null){
            StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(imageUrl);
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    memory.setImageUrl(null);
                    if(!imageViewVisible){
                        deleteImageButton.setVisibility(View.GONE);
                        memoryImage.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        deleteImage(memory.getImageUrl(), false);

        super.onBackPressed();
    }
}