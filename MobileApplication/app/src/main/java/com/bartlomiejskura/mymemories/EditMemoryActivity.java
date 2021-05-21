package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.adapter.FriendsAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.task.CreateOrGetCategoriesTask;
import com.bartlomiejskura.mymemories.task.EditMemoryTask;
import com.bartlomiejskura.mymemories.utils.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class EditMemoryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView memoryImage;
    private ImageButton deleteImageButton, deleteTimeButton, addCategoryButton;
    private FloatingActionButton saveMemoryButton;
    private Button addPersonButton, dateButton, timeButton, locationButton, selectImageButton, selectLocationButton, deleteLocationButton, addCategoriesButton;
    private TextInputLayout titleInputLayout;
    private SwitchMaterial makePublicSwitch;
    private ChipGroup chipGroup, friendsChipGroup;
    private EditText titleEditText, descriptionEditText, categoryEditText;
    private Spinner prioritySpinner;
    private SupportMapFragment mapFragment;
    private TextView toolbarTextView;
    private ImageButton backButton;
    private LinearLayout addCategoriesLayout;
    private LinearProgressIndicator editMemoryProgressIndicator;
    private ConstraintLayout editMemoryConstraintLayout;


    private Memory memory = new Memory();
    private SharedPreferences sharedPreferences;
    private int memoryPriority;
    private StorageReference storageReference;
    private List<User> memoryFriends = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private String imageUrl;
    private Boolean makeMemoryPublic = false;
    private List<String> categories = new LinkedList<>();
    private Gson gson = new Gson();
    private ArrayList<User> friends;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map;
    private Double latitude = null, longitude = null;
    private Marker marker;
    private Thread saveMemoryThread;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);

        findViews();
        initValues();
        prepareViews();
        setListeners();
    }

    private void findViews(){
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        saveMemoryButton = findViewById(R.id.saveMemoryButton);
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
        editMemoryProgressIndicator = findViewById(R.id.editMemoryProgressIndicator);
        editMemoryConstraintLayout = findViewById(R.id.editMemoryConstraintLayout);
    }

    private void initValues(){
        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        makeMemoryPublic = getIntent().getBooleanExtra("isMemoryPublic", false);
        latitude = getIntent().getDoubleExtra("latitude", 500)==500?null:getIntent().getDoubleExtra("latitude", 500);
        longitude = getIntent().getDoubleExtra("longitude", 500)==500?null:getIntent().getDoubleExtra("longitude", 500);

        friends = initFriendsList();
    }

    @SuppressLint("SetTextI18n")
    private void prepareViews(){
        //toolbar
        toolbarTextView.setText("Edit a memory");
        findViewById(R.id.searchButton).setVisibility(View.GONE);

        //title edit text
        titleEditText.setText(getIntent().getStringExtra("title"));

        //description edit text
        descriptionEditText.setText(getIntent().getStringExtra("description"));

        //public to friends switch
        makePublicSwitch.setChecked(makeMemoryPublic);

        //date and time buttons
        String date = getIntent().getStringExtra("date");
        calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7))-1, Integer.parseInt(date.substring(8, 10)), Integer.parseInt(date.substring(11, 13)), Integer.parseInt(date.substring(14, 16)));
        dateButton.setText(date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4));
        if(getIntent().getStringExtra("date").endsWith("0")){
            timeButton.setText("Select");
            deleteTimeButton.setVisibility(View.GONE);
        }else{
            timeButton.setText(date.substring(11, 16));
        }

        //add categories layout
        addCategoriesLayout.setVisibility(View.GONE);

        //category chips
        try {
            JSONArray array = new JSONArray(getIntent().getStringExtra("categories"));
            Gson gson = new Gson();
            String category;

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                category = gson.fromJson(object.toString(), Category.class).getName();
                initChipCategory(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //memory image
        imageUrl = getIntent().getStringExtra("imageUrl");
        if(imageUrl!=null){
            Glide.with(this).load(imageUrl).into(memoryImage);
        }else{
            deleteImageButton.setVisibility(View.GONE);
            memoryImage.setVisibility(View.GONE);
        }

        //friends chip group
        initFriendsChipGroup();

        //memory priority spinner
        memoryPriority = getIntent().getIntExtra("memoryPriority", 0);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setSelection(memoryPriority==10?0:(memoryPriority==50?1:2));

        //map fragment and delete location button
        mapFragment.getMapAsync(this);
        if(latitude!=null && longitude!=null){
            if(map!=null){
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                marker = map.addMarker(markerOptions);
            }
        }else{
            mapFragment.getView().setVisibility(View.GONE);
            deleteLocationButton.setVisibility(View.GONE);
        }

        //progress indicator
        editMemoryProgressIndicator.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setListeners(){
        dateButton.setOnClickListener(v -> {
            selectDate();
        });

        timeButton.setOnClickListener(v -> {
            selectTime();
        });

        selectImageButton.setOnClickListener(v -> {
            openFileChooser();
        });

        saveMemoryButton.setOnClickListener(v -> {
            if(saveMemoryThread==null||!saveMemoryThread.isAlive()){
                saveMemoryThread = new Thread(() -> editMemory(titleEditText.getText().toString(), descriptionEditText.getText().toString()));
                saveMemoryThread.start();
            }
        });

        deleteImageButton.setOnClickListener(v -> {
            if(memory.getImageUrl()!=null){
                deleteImage(memory.getImageUrl(), false);
            }else{
                deleteImageButton.setVisibility(View.GONE);
                memoryImage.setVisibility(View.GONE);
            }
        });

        deleteTimeButton.setOnClickListener(v -> {
            timeButton.setText("Select");
            deleteTimeButton.setVisibility(View.GONE);
        });

        addPersonButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditMemoryActivity.this);
            builder.setTitle("Tag a Friend");
            FriendsAdapter adapter1 = new FriendsAdapter(getApplicationContext(), friends, this);
            builder.setAdapter(adapter1, (dialog, which) -> {
                final User friend = friends.get(which);
                LayoutInflater inflater = LayoutInflater.from(EditMemoryActivity.this);
                Chip chip = (Chip)inflater.inflate(R.layout.chip_with_close_icon, null, false);
                chip.setText(friend.getFirstName()+" "+friend.getLastName());
                Target target=getTargetOfPicasso(chip);
                if(friend.getAvatarUrl()!=null){
                    Picasso.get().load(friend.getAvatarUrl()).transform(new CircleTransform()).resize(20,20).into(target);
                }else{
                    Picasso.get().load(R.drawable.default_avatar).transform(new CircleTransform()).resize(20,20).into(target);
                }

                chip.setOnCloseIconClickListener(v1 -> {
                    friendsChipGroup.removeView(v1);
                    memoryFriends.remove(friend);
                    friends.add(friend);
                });
                chip.setCheckable(false);
                memoryFriends.add(friend);
                friendsChipGroup.addView(chip);
                friends.remove(friend);
            });
            builder.show();
        });

        makePublicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            makeMemoryPublic=!makeMemoryPublic;
        });

        addCategoryButton.setOnClickListener(v -> {
            String category = categoryEditText.getText().toString().toLowerCase();
            if(category.length()>20){
                runOnUiThread(() -> {
                    Snackbar.make(editMemoryConstraintLayout, "Category name cannot be longer than 20 characters", Snackbar.LENGTH_LONG).show();
                });
                return;
            }
            if(!categories.contains(category)&&!category.isEmpty()){
                initChipCategory(category);
            }
            categoryEditText.setText("");
        });

        locationButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(EditMemoryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }else{
                ActivityCompat.requestPermissions(EditMemoryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        selectLocationButton.setOnClickListener(v -> {
            if(latitude!=null&&longitude!=null){
                showPlacePicker(latitude, longitude);
            }else if(ActivityCompat.checkSelfPermission(EditMemoryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if(location != null){
                        showPlacePicker(location.getLatitude(), location.getLongitude());
                    }
                });
            }else{
                ActivityCompat.requestPermissions(EditMemoryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        deleteLocationButton.setOnClickListener(v -> {
            latitude = null;
            longitude = null;
            mapFragment.getView().setVisibility(View.GONE);
            deleteLocationButton.setVisibility(View.GONE);
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

        prioritySpinner.setOnItemSelectedListener(new OnItemSelectedListener());

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleInputLayout.setError("");
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
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
                        .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            deleteImageButton.setVisibility(View.VISIBLE);
                            memoryImage.setVisibility(View.VISIBLE);
                            deleteImage(memory.getImageUrl(), true);
                            memory.setImageUrl(uri.toString());
                            Glide.with(this).load(imageUri).into(memoryImage);
                        }));
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


                if(marker!=null){
                    marker.remove();
                }

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(addressData.getLatitude(), addressData.getLongitude()));
                marker = map.addMarker(markerOptions);
            }
        }
    }

    @Override
    public void onBackPressed() {
        deleteImage(memory.getImageUrl(), false);

        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map == null) {
            return;
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude!=null?latitude:0, longitude!=null?longitude:0), 15f));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude!=null?latitude:0, longitude!=null?longitude:0));
        marker = map.addMarker(markerOptions);
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
                    showPlacePicker(51.75, 19.45);
                }
            }
        }
    }


    private void initChipCategory(String category){
        LayoutInflater inflater = LayoutInflater.from(EditMemoryActivity.this);

        Chip chip = (Chip)inflater.inflate(R.layout.chip_with_close_icon, null, false);
        chip.setText(category);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(v);
            categories.remove(((Chip)v).getText().toString());
        });
        chip.setCheckable(false);

        chipGroup.addView(chip);
        categories.add(category);
    }

    private void initFriendsChipGroup(){
        try {
            JSONArray array = new JSONArray(getIntent().getStringExtra("memoryFriends"));
            Gson gson = new Gson();
            User friend;

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                friend = gson.fromJson(object.toString(), User.class);
                initChipMemoryFriend(friend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initChipMemoryFriend(final User friend){
        LayoutInflater inflater = LayoutInflater.from(EditMemoryActivity.this);
        Chip chip = (Chip)inflater.inflate(R.layout.chip_with_close_icon, null, false);
        chip.setText(friend.getFirstName()+" "+friend.getLastName());
        Target target=getTargetOfPicasso(chip);
        if(friend.getAvatarUrl()!=null){
            Picasso.get().load(friend.getAvatarUrl()).transform(new CircleTransform()).resize(20,20).into(target);
        }else{
            Picasso.get().load(R.drawable.default_avatar).transform(new CircleTransform()).resize(20,20).into(target);
        }
        chip.setOnCloseIconClickListener(v -> {
            friendsChipGroup.removeView(v);
            memoryFriends.remove(friend);
            friends.add(friend);
        });
        chip.setCheckable(false);
        memoryFriends.add(friend);
        friendsChipGroup.addView(chip);
        for(User user:friends){
            if(user.getId().equals(friend.getId())){
                friends.remove(user);
                break;
            }
        }
    }

    private void editMemory(String title, String description){
        runOnUiThread(()->editMemoryProgressIndicator.setVisibility(View.VISIBLE));

        if (!verifyData(title,description)) {
            return;
        }

        List<Category> categories = null;
        if(!this.categories.isEmpty()){
            categories = getCategories();
            if(categories ==null){
                runOnUiThread(()->editMemoryProgressIndicator.setVisibility(View.GONE));
                return;
            }
        }

        if(timeButton.getText().equals("Select")){//second should be 0 if user didn't choose time or 1 if time was chosen
            calendar.set(Calendar.SECOND, 0);
        }else{
            calendar.set(Calendar.SECOND, 1);
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        memory.setId(getIntent().getLongExtra("memoryId", 0));
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
                runOnUiThread(()->{
                    if(editMemoryTask.getError().contains("Unable to resolve host")){
                        Snackbar.make(editMemoryConstraintLayout, "Problem with the Internet connection", Snackbar.LENGTH_LONG).show();
                    }else if(editMemoryTask.getError().contains("timeout")){
                        Snackbar.make(editMemoryConstraintLayout, "Connection timed out", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(editMemoryConstraintLayout, "A problem occurred", Snackbar.LENGTH_LONG).show();
                    }
                    editMemoryProgressIndicator.setVisibility(View.GONE);
                });
                return;
            }
            if(oldImageToDelete){
                deleteImage(imageUrl, true);//usuwanie starego zdjęcia, które wcześniej było przypisane do wspomnienia
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("fragmentToLoad", "datesFragment");
            i.putExtra("date", memory.getDate());
            startActivity(i);
        }catch (Exception e){
            runOnUiThread(()->editMemoryProgressIndicator.setVisibility(View.GONE));
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    private boolean verifyData(String title, String description){
        if (title.isEmpty()) {
            runOnUiThread(() -> {
                titleInputLayout.setError("Title field cannot be empty!");
                editMemoryProgressIndicator.setVisibility(View.GONE);
            });
            return false;
        }
        if (title.length()>255) {
            runOnUiThread(() -> {
                titleInputLayout.setError("Title cannot be longer than 255 characters");
                editMemoryProgressIndicator.setVisibility(View.GONE);
            });
            return false;
        }
        if (description.length()>10000) {
            runOnUiThread(() -> {
                titleInputLayout.setError("Description cannot be longer than 10000 characters");
                editMemoryProgressIndicator.setVisibility(View.GONE);
            });
            return false;
        }

        return true;
    }

    private List<Category> getCategories(){
        final EditMemoryActivity activity = this;

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
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year1, month1, date) -> {

            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DATE, date);
            String dateText = DateFormat.format("dd-MM-yyyy", calendar).toString();

            dateButton.setText(dateText);
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void selectTime() {
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hour1, minute1) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour1);
            calendar.set(Calendar.MINUTE, minute1);
            String dateText = DateFormat.format("HH:mm", calendar).toString();
            timeButton.setText(dateText);

            deleteTimeButton.setVisibility(View.VISIBLE);
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void deleteImage(String imageUrl, final boolean imageViewVisible){
        if(imageUrl !=null){
            StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(imageUrl);
            photoRef.delete().addOnSuccessListener(aVoid -> {
                memory.setImageUrl(null);
                if(!imageViewVisible){
                    deleteImageButton.setVisibility(View.GONE);
                    memoryImage.setVisibility(View.GONE);
                }
            });
        }
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
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if(location != null){
                try {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    deleteLocationButton.setVisibility(View.VISIBLE);
                    Geocoder geocoder = new Geocoder(EditMemoryActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), 15f));
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();

                    if(marker!=null){
                        marker.remove();
                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                    marker = map.addMarker(markerOptions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showPlacePicker(double latitude, double longitude){
        Intent intent = new PlacePicker.IntentBuilder()
                .setLatLong(latitude, longitude)
                .showLatLong(true)
                .setMapZoom(12.0f)
                .setAddressRequired(true)
                .hideMarkerShadow(true)
                .setMarkerDrawable(R.drawable.baseline_room_24)
                .setMarkerImageImageColor(R.color.colorAccent)
                .setFabColor(R.color.colorAccent)
                .setPrimaryTextColor(R.color.colorAccent)
                .setSecondaryTextColor(R.color.colorAccentLight)
                .setBottomViewColor(R.color.white)
                .setMapType(MapType.NORMAL)
                .onlyCoordinates(true)
                .hideLocationButton(true)
                .disableMarkerAnimation(true)
                .build(this);

        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
    }


    class OnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
            String selected = parent.getItemAtPosition(position).toString();
            switch (selected) {
                case "High":
                    memoryPriority = 90;
                    break;
                case "Medium":
                    memoryPriority = 50;
                    break;
                case "Low":
                    memoryPriority = 10;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}