package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.bartlomiejskura.mymemories.adapter.FriendsAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.CreateMemoryTask;
import com.bartlomiejskura.mymemories.task.CreateOrGetCategoriesTask;
import com.bartlomiejskura.mymemories.utils.CircleTransform;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class AddMemoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private ImageView memoryImage;
    private ImageButton deleteImageButton, deleteTimeButton;
    private ImageButton addCategoryButton;
    private Button addPersonButton, dateButton, timeButton, locationButton, addMemoryButton, selectImageButton, selectLocationButton, deleteLocationButton, addCategoriesButton;
    private TextInputLayout titleInputLayout;
    private SwitchMaterial makePublicSwitch;
    private ChipGroup chipGroup, friendsChipGroup;
    private EditText titleEditText, description, categoryEditText;
    private Spinner prioritySpinner;
    private SupportMapFragment mapFragment;
    private TextView toolbarTextView;
    private ImageButton backButton;
    private LinearLayout addCategoriesLayout;

    private Memory memory = new Memory();
    private Integer day, month, year, hour, minute;
    private SharedPreferences sharedPreferences;
    private int memoryPriority=90;
    private StorageReference storageReference;
    private List<User> memoryFriends = new ArrayList<>();
    private Boolean makeMemoryPublic = false;
    private List<String> categories = new LinkedList<>();
    private Gson gson = new Gson();
    private ArrayList<User> friends;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map;
    private Double latitude = null, longitude = null;

    private static final int PICK_IMAGE_REQUEST = 1;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        bindViews();

        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(this);
        deleteImageButton.setVisibility(View.GONE);
        memoryImage.setVisibility(View.GONE);
        deleteTimeButton.setVisibility(View.GONE);
        friends = initFriendsList();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment.getView().setVisibility(View.GONE);
        deleteLocationButton.setVisibility(View.GONE);
        addCategoriesLayout.setVisibility(View.GONE);

        dateButton.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

        toolbarTextView.setText("Add a memory");
        findViewById(R.id.searchButton).setVisibility(View.GONE);

        setListeners();
        mapFragment.getMapAsync(this);
    }

    private void bindViews(){
        titleEditText = findViewById(R.id.titleEditText);
        description = findViewById(R.id.descriptionEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        addMemoryButton = findViewById(R.id.addMemoryButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        deleteImageButton = findViewById(R.id.deleteImageButton);
        deleteTimeButton = findViewById(R.id.deleteTimeButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        memoryImage = findViewById(R.id.memoryImage);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        addPersonButton = findViewById(R.id.addPersonButton);
        titleInputLayout = findViewById(R.id.textInputLayout);
        makePublicSwitch = findViewById(R.id.makePublicSwitch);
        chipGroup = findViewById(R.id.chipGroup);
        friendsChipGroup = findViewById(R.id.friendsChipGroup);
        addCategoryButton = findViewById(R.id.addCategoryButton);
        locationButton = findViewById(R.id.locationButton);
        selectLocationButton = findViewById(R.id.selectLocationButton);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        deleteLocationButton = findViewById(R.id.deleteLocationButton);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        backButton = findViewById(R.id.backButton);
        addCategoriesButton = findViewById(R.id.addCategoriesButton);
        addCategoriesLayout = findViewById(R.id.linearLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){
        dateButton.setOnClickListener(v -> {
            selectDate();
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        timeButton.setOnClickListener(v -> {
            selectTime();
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        selectImageButton.setOnClickListener(v -> {
            openFileChooser();
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        addMemoryButton.setOnClickListener(v -> new Thread(() -> addMemory(titleEditText.getText().toString(), description.getText().toString())).start());

        deleteImageButton.setOnClickListener(v -> {
            deleteImage(memory.getImageUrl(), false);
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        deleteTimeButton.setOnClickListener(v -> {
            timeButton.setText("Select");
            deleteTimeButton.setVisibility(View.GONE);
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        addPersonButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddMemoryActivity.this);
            builder.setTitle("Tag a Friend");
            FriendsAdapter adapter = new FriendsAdapter(getApplicationContext(), friends);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    final User friend = friends.get(which);
                    LayoutInflater inflater = LayoutInflater.from(AddMemoryActivity.this);
                    Chip chip = (Chip)inflater.inflate(R.layout.chip_category, null, false);
                    chip.setText(friend.getFirstName()+" "+friend.getLastName());
                    Target target=getTargetOfPicasso(chip);
                    if(friend.getAvatarUrl()!=null){
                        Picasso.get().load(friend.getAvatarUrl()).transform(new CircleTransform()).resize(20,20).into(target);
                    }else{
                        Picasso.get().load(R.drawable.default_avatar).transform(new CircleTransform()).resize(20,20).into(target);
                    }

                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            friendsChipGroup.removeView(v);
                            memoryFriends.remove(friend);
                            friends.add(friend);
                        }
                    });

                    chip.setCheckable(false);

                    memoryFriends.add(friend);
                    friendsChipGroup.addView(chip);
                    friends.remove(friend);
                }
            });
            builder.show();
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        makePublicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            makeMemoryPublic=!makeMemoryPublic;
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        addCategoryButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(AddMemoryActivity.this);

            String category = categoryEditText.getText().toString().toLowerCase();
            if(!categories.contains(category)||category.isEmpty()){
                Chip chip = (Chip)inflater.inflate(R.layout.chip_category, null, false);
                chip.setText(category);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chipGroup.removeView(v);
                        categories.remove(((Chip)v).getText().toString());
                    }
                });

                chip.setCheckable(false);

                chipGroup.addView(chip);
                categories.add(category);
            }
            categoryEditText.setText("");
        });

        locationButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(AddMemoryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }else{
                ActivityCompat.requestPermissions(AddMemoryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }

            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        selectLocationButton.setOnClickListener(v -> {
            if(latitude!=null&&longitude!=null){
                showPlacePicker(latitude, longitude);
            }else if(ActivityCompat.checkSelfPermission(AddMemoryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if(location != null){
                        showPlacePicker(location.getLatitude(), location.getLongitude());
                    }
                });
            }else{
                ActivityCompat.requestPermissions(AddMemoryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        deleteLocationButton.setOnClickListener(v -> {
            latitude = null;
            longitude = null;
            mapFragment.getView().setVisibility(View.GONE);
            deleteLocationButton.setVisibility(View.GONE);
            addCategoriesLayout.setVisibility(View.GONE);
            addCategoriesButton.setVisibility(View.VISIBLE);
        });

        backButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

        addCategoriesButton.setOnClickListener(v -> {
            addCategoriesLayout.setVisibility(View.VISIBLE);
            addCategoriesButton.setVisibility(View.GONE);
            categoryEditText.requestFocus();
        });

        categoryEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                addCategoriesLayout.setVisibility(View.GONE);
                addCategoriesButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addMemory(String title, String description){
        if (title.isEmpty()) {
            runOnUiThread(() -> titleInputLayout.setError("Title field cannot be empty!"));
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        if(year!=null&&month!=null&&day!=null){
            calendar.set(year, month, day, hour==null?0:hour, minute==null?0:minute);
        }

        Long memoryOwnerId = sharedPreferences.getLong("userId", 0);
        List<Category> categories = null;
        if(!this.categories.isEmpty()){
            categories = getCategories();
            if(categories ==null){
                return;
            }
        }
        //Memory memory = new Memory(title, description, sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"), sdf.format(calendar.getTime()).replace(" ", "T"), new User(memoryOwnerId), memoryPriority, tag);
        memory.setShortDescription(title);
        memory.setLongDescription(description==null?"":description);
        memory.setCreationDate(sdf.format(Calendar.getInstance().getTime()).replace(" ", "T"));
        memory.setDate(sdf.format(calendar.getTime()).replace(" ", "T"));
        memory.setMemoryOwner(new User(sharedPreferences.getLong("userId", 0), sharedPreferences.getString("email", "")));
        memory.setMemoryPriority(memoryPriority);
        memory.setCategories(categories);
        memory.setMemoryFriends(memoryFriends);
        memory.setPublicToFriends(makeMemoryPublic);
        memory.setLatitude(latitude);
        memory.setLongitude(longitude);

        final AddMemoryActivity activity = this;

        try{
            CreateMemoryTask createMemoryTask = new CreateMemoryTask(activity, memory);
            Boolean createMemoryResult = createMemoryTask.execute().get();
            if(!createMemoryResult){
                return;
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("fragmentToLoad", "datesFragment");
            startActivity(i);
        }catch (Exception e){
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    private List<Category> getCategories(){
        final AddMemoryActivity activity = this;

        try{
            CreateOrGetCategoriesTask task = new CreateOrGetCategoriesTask(activity, categories);
            Category[] categoryArray = task.execute().get();
            return new ArrayList<>(Arrays.asList(categoryArray));
        }catch (Exception e){
            System.out.println("ERROR:" + e.getMessage());
            return null;
        }
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year1);
                calendar1.set(Calendar.MONTH, month1);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("dd-MM-yyyy", calendar1).toString();

                dateButton.setText(dateText);
                year = year1;
                month = month1;
                day = date;
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour1, int minute1) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, hour1);
                calendar1.set(Calendar.MINUTE, minute1);
                String dateText = DateFormat.format("HH:mm", calendar1).toString();
                timeButton.setText(dateText);
                hour = hour1;
                minute = minute1;

                deleteTimeButton.setVisibility(View.VISIBLE);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
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
        }else if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addressData.getLatitude(), addressData.getLongitude()), 15f));
                latitude = addressData.getLatitude();
                longitude = addressData.getLongitude();
                mapFragment.getView().setVisibility(View.VISIBLE);
                deleteLocationButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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

    private ArrayList<User> initFriendsList(){
        ArrayList<User> friends = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(sharedPreferences.getString("friends", ""));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                friends.add(gson.fromJson(object.toString(), User.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friends;
    }

    private Target getTargetOfPicasso(final Chip targetChip){
        return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                targetChip.setChipIcon(d);
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                targetChip.setChipIcon(errorDrawable);
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                targetChip.setChipIcon(placeHolderDrawable);
            }
        };
    }

    private void getLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){
                    try {
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        deleteLocationButton.setVisibility(View.VISIBLE);
                        Geocoder geocoder = new Geocoder(AddMemoryActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), 15f));
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map == null) {
            return;
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 15f));
    }

    public void showPlacePicker(double latitude, double longitude){
        Intent intent = new PlacePicker.IntentBuilder()
                .setLatLong(latitude, longitude)
                .showLatLong(true)
                .setMapZoom(12.0f)
                .setAddressRequired(true)
                .hideMarkerShadow(true)
                .setMarkerDrawable(R.drawable.ic_map_marker)
                .setMarkerImageImageColor(R.color.colorPrimary)
                .setFabColor(R.color.colorPrimary)
                .setPrimaryTextColor(R.color.colorPrimary)
                .setSecondaryTextColor(R.color.colorAccent)
                .setBottomViewColor(R.color.white)
                .setMapType(MapType.NORMAL)
                .onlyCoordinates(true)
                .hideLocationButton(true)
                .disableMarkerAnimation(true)
                .build(this);

        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 44:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                        Location location = task.getResult();
                        if(location != null){
                            showPlacePicker(location.getLatitude(), location.getLongitude());
                        }
                    });
                }else{
                    showPlacePicker(0, 0);
                }
            }
        }
    }
}