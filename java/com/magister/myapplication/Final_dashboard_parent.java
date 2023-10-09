package com.magister.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Final_dashboard_parent extends AppCompatActivity {

    BottomNavigationView BottomNavigationView;
    homeFragment HomeFragment = new homeFragment();
    scheduleFragment ScheduleFragment = new scheduleFragment();
    messageFragment MessageFragment = new messageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_dashboard_parent);

        BottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, HomeFragment).commit();

        BottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, HomeFragment).commit();
                        return true;
                    case R.id.Prof:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, ScheduleFragment).commit();
                        return true;
                    case R.id.mez:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, MessageFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
