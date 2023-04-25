package com.example.enigma2048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RxDataStore<Byte> dataStore =
        //        new RxDataStoreBuilder<Byte>(this, /* fileName= */ "state.pb", new RuntimeStateSerializer()).build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, HomeFragment.class, null)
                .commit();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, SettingsFragment.class, null)
                        .commit();
                return true;
            }
            return false;
        });
    }
}