package com.example.enigma2048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.play) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, PlayFragment.class, null)
                        .commit();
            } else if (id == R.id.leaderboards) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, LeaderboardsFragment.class, null)
                        .commit();
            } else if (id == R.id.settings) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, SettingsFragment.class, null)
                        .commit();
            } else {
                return false;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.play);
    }
}