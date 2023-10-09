package com.magister.myapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.magister.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Tutor_dashboard extends AppCompatActivity {
    BottomNavigationView BottomNavigationView;
    Home_Admin_Frag homeAdmin = new Home_Admin_Frag();
    Settings_Frag settingAdmin = new Settings_Frag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_dashboard);

        BottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeAdmin).commit();
        BottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Prof:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeAdmin).commit();
                        return true;
                    case R.id.mez:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, settingAdmin).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
