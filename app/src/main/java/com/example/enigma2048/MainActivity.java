package com.example.enigma2048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.rxjava3.core.Single;

public class MainActivity extends AppCompatActivity {
    private RxDataStore<RuntimeState> dataStore;
    private RuntimeStateViewModel viewModel = new RuntimeStateViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = new RxDataStoreBuilder<RuntimeState>(this, /* fileName= */ "state.pb", new RuntimeStateSerializer()).build();
        viewModel = new ViewModelProvider(this).get(RuntimeStateViewModel.class);
        viewModel.observe(this, item -> {
            if (item == null) {
                viewModel.resetValue();
            } else {
                String message = "Score: " + item.getScore() + "\nMoves: " + item.getMoves() + "\nTime: " + item.getTime() + "\nBoard: " + item.getBoardCellList() + "\nPrevious game state: " + item.getPreviousGame();
                Log.d("MainActivity", "Updating data store with:\n" + message);
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
                Log.d("MainActivity", "Update result: " + updateResult);
                RuntimeState currentState = dataStore.data().blockingFirst();
                message = "Score: " + currentState.getScore() + "\nMoves: " + currentState.getMoves() + "\nTime: " + currentState.getTime() + "\nBoard: " + currentState.getBoardCellList() + "\nPrevious game state: " + currentState.getPreviousGame();
                Log.d("MainActivity", "Data store:\n" + message);
            }
        });
        viewModel.setValue(dataStore.data().blockingFirst());

        RuntimeState currentState = viewModel.getValue();
        if (currentState == null) {
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