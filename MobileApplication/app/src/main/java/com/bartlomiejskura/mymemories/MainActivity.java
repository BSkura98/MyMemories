package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.fragment.CategoriesFragment;
import com.bartlomiejskura.mymemories.fragment.MemoriesFragment;
import com.bartlomiejskura.mymemories.fragment.FriendsFragment;
import com.bartlomiejskura.mymemories.fragment.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private TextView toolbarTextView;
    private DrawerLayout drawerLayout;
    private ImageButton searchButton;
    private Toolbar toolbar;

    private SharedPreferences sharedPreferences;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initValues();
        setListeners();
        prepareViews();

        setFragment();
    }


    private void findViews(){
        navigationView = findViewById(R.id.nav_view);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        searchButton = findViewById(R.id.searchButton);
    }

    private void initValues(){
        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);
    }

    private void setListeners(){
        searchButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SearchMemoryActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        navigationView.setNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener());
    }

    private void prepareViews(){
        //toolbar
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //drawer layout and navigation view
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @SuppressLint("SetTextI18n")
    private void setFragment(){
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle!=null){
            switch(bundle.getString("fragmentToLoad")){
                case "friendsMemoriesFragment":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendsFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_friends);
                    toolbarTextView.setText("Friends");
                    i.putExtra("fragmentToLoad", "friendsMemoriesFragment");
                    searchButton.setVisibility(View.GONE);
                    break;
                case "categoriesFragment":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoriesFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_categories);
                    toolbarTextView.setText("Categories");
                    i.putExtra("fragmentToLoad", "categoriesFragment");
                    searchButton.setVisibility(View.GONE);
                    break;
                case "settingsFragment":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_settings);
                    toolbarTextView.setText("Settings");
                    i.putExtra("fragmentToLoad", "settingsFragment");
                    searchButton.setVisibility(View.GONE);
                    break;
                case "datesFragment":
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MemoriesFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_memories);
                    toolbarTextView.setText("Memories");
                    i.putExtra("fragmentToLoad", "datesFragment");
                    searchButton.setVisibility(View.VISIBLE);
                    break;
            }
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MemoriesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_memories);
            toolbarTextView.setText("Memories");
            i.putExtra("fragmentToLoad", "datesFragment");
            searchButton.setVisibility(View.VISIBLE);
        }
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.remove("email");
        editor.remove("firstName");
        editor.remove("lastName");
        editor.remove("birthday");
        editor.remove("token");
        editor.remove("avatarUrl");
        editor.remove("friends");
        editor.remove("friendRequestsIds");
        editor.apply();

        drawerLayout.closeDrawer(GravityCompat.START);

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void exit(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private class MyOnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener{
        private MyOnNavigationItemSelectedListener(){}

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i = getIntent();
            switch (item.getItemId()){
                case R.id.nav_memories:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MemoriesFragment()).commit();
                    toolbarTextView.setText("Memories");
                    i.putExtra("fragmentToLoad", "datesFragment");
                    searchButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.nav_friends:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FriendsFragment()).commit();
                    toolbarTextView.setText("Friends");
                    i.putExtra("fragmentToLoad", "friendsMemoriesFragment");
                    searchButton.setVisibility(View.GONE);
                    break;
                case R.id.nav_categories:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoriesFragment()).commit();
                    toolbarTextView.setText("Categories");
                    i.putExtra("fragmentToLoad", "categoriesFragment");
                    searchButton.setVisibility(View.GONE);
                    break;
                case R.id.nav_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                    toolbarTextView.setText("Settings");
                    i.putExtra("fragmentToLoad", "settingsFragment");
                    searchButton.setVisibility(View.GONE);
                    break;
                case R.id.nav_exit:
                    exit();
                    break;
                case R.id.nav_logout:
                    logout();
                    return false;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}
