package com.magister.myapplication;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class Dashboard extends AppCompatActivity {

    BottomNavigationView BottomNavigationView;
    HomeFragStudents HomeFragment = new HomeFragStudents();
    ScheduleFragmentStudent ScheduleFragment = new ScheduleFragmentStudent();
    SettingFragmentFragment MessageFragment = new SettingFragmentFragment();
    ChatFragmentStudent SettingFragment = new ChatFragmentStudent();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashbord);

        BottomNavigationView = findViewById(R.id.BottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, HomeFragment).commit();

        BottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, HomeFragment).commit();
                        return true;
                    case R.id.chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, SettingFragment).commit();
                        return true;
                    case R.id.Prof:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, ScheduleFragment).commit();
                        return true;
                    case R.id.mez:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, MessageFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
    }
