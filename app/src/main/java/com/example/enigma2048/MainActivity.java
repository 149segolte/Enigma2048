package com.example.enigma2048;

import android.os.Bundle;
import android.widget.Toast;
import android.view.GestureDetector;

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

    private GestureDetector gestureDetector;
    private ImageView imageView;
    private float initialX, initialY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the GestureDetector with a custom MyGestureListener
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        dataStore = new RxDataStoreBuilder<RuntimeState>(this, /* fileName= */ "state.pb", new RuntimeStateSerializer()).build();
        viewModel = new ViewModelProvider(this).get(RuntimeStateViewModel.class);
        viewModel.getValue().observe(this, item -> {
            if (item == null) {
                viewModel.resetValue();
            } else {
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
            }
        });
        viewModel.setValue(dataStore.data().blockingFirst());

        RuntimeState currentState = viewModel.getValue().getValue();
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to the GestureDetector
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}