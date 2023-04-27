package com.example.enigma2048;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.rxjava3.core.Single;

public class MainActivity extends AppCompatActivity {
    private RxDataStore<RuntimeState> dataStore;
    private RuntimeStateViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = new RxDataStoreBuilder<>(this, "state.pb", new RuntimeStateSerializer()).build();
        viewModel = new ViewModelProvider(this).get(RuntimeStateViewModel.class);
        viewModel.set(dataStore.data().blockingFirst());
        viewModel.observe(this, state -> {
            Log.d("DataStore", "Saving state:\n" + state.toString());
            if (state != null) {
                dataStore.updateDataAsync(dataStoreUpdate ->
                    Single.just(
                            dataStoreUpdate.toBuilder()
                                    .setScore(state.getScore())
                                    .setMoves(state.getMoves())
                                    .setTime(state.getTime())
                                    .clearBoardCell()
                                    .addAllBoardCell(state.getBoardCellList())
                                    .setPreviousGame(state.getPreviousGame())
                                    .build()));
            }
        });

        RuntimeState state = viewModel.get();
        if (state == null) {
            Toast.makeText(this, "Error loading game state", Toast.LENGTH_SHORT).show();
            finish();
        }

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