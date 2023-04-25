package com.example.enigma2048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private RxDataStore<RuntimeState> dataStore;
    private RuntimeStateViewModel viewModel = new RuntimeStateViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = new RxDataStoreBuilder<RuntimeState>(this, /* fileName= */ "state.pb", new RuntimeStateSerializer()).build();
        viewModel = new ViewModelProvider(this).get(RuntimeStateViewModel.class);
        viewModel.setValue(dataStore.data().blockingFirst());
        viewModel.getValue().observe(this, item -> {
            Single<RuntimeState> updateResult =
                    dataStore.updateDataAsync(currentState ->
                            Single.just(
                                    currentState.toBuilder()
                                            .setScore(item.getScore())
                                            .setTime(item.getTime())
                                            .setMoves(item.getMoves())
                                            .clearBoardCell()
                                            .addAllBoardCell(item.getBoardCellList())
                                            .build()));
        });

        RuntimeState currentState = viewModel.getValue().getValue();
        if (currentState == null) {
            Toast.makeText(this, "Error loading game state", Toast.LENGTH_SHORT).show();
            finish();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean previous_game = currentState.getPreviousGame();
        Bundle bundle = new Bundle();
        bundle.putBoolean("previous_game", previous_game);
        fragmentManager.setFragmentResult("status", bundle);
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