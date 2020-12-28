package com.bartlomiejskura.mymemories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bartlomiejskura.mymemories.fragment.CategoriesFragment;
import com.bartlomiejskura.mymemories.fragment.MemoryBoardFragment;
import com.bartlomiejskura.mymemories.fragment.RecentEntriesFragment;
import com.bartlomiejskura.mymemories.fragment.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private TextView toolbarTextView;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_view);
        toolbarTextView = findViewById(R.id.toolbarTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        sharedPreferences = getSharedPreferences("MyMemoriesPref", Context.MODE_PRIVATE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        setFragment();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i = getIntent();
        switch (item.getItemId()){
            case R.id.nav_recent_entries:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentEntriesFragment()).commit();
                toolbarTextView.setText("Recent entries");
                i.putExtra("fragmentToLoad", "recentEntriesFragment");
                break;
            case R.id.nav_memory_board:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MemoryBoardFragment()).commit();
                toolbarTextView.setText("Memory board");
                i.putExtra("fragmentToLoad", "memoryBoardFragment");
                break;
            case R.id.nav_Categories:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoriesFragment()).commit();
                toolbarTextView.setText("Categories");
                i.putExtra("fragmentToLoad", "categoriesFragment");
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                toolbarTextView.setText("Settings");
                i.putExtra("fragmentToLoad", "settingsFragment");
                break;
            case R.id.nav_exit:
                exit();
                break;
            case R.id.nav_logout:
                logOut();
                return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(){
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle!=null){
            switch(bundle.getString("fragmentToLoad")){
                case "memoryBoardFragment":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MemoryBoardFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_memory_board);
                    toolbarTextView.setText("Memory board");
                    i.putExtra("fragmentToLoad", "memoryBoardFragment");
                    break;
                case "categoriesFragment":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoriesFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_Categories);
                    toolbarTextView.setText("Categories");
                    i.putExtra("fragmentToLoad", "categoriesFragment");
                    break;
                case "settingsFragment":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_settings);
                    toolbarTextView.setText("Settings");
                    i.putExtra("fragmentToLoad", "settingsFragment");
                    break;
                case "recentEntriesFragment":
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentEntriesFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_recent_entries);
                    toolbarTextView.setText("Recent entries");
                    i.putExtra("fragmentToLoad", "recentEntriesFragment");
                    break;
            }
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentEntriesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_recent_entries);
            toolbarTextView.setText("Recent entries");
            i.putExtra("fragmentToLoad", "recentEntriesFragment");
        }
    }

    public void logOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.remove("email");
        editor.remove("firstName");
        editor.remove("lastName");
        editor.remove("birthday");
        editor.remove("token");
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
}
