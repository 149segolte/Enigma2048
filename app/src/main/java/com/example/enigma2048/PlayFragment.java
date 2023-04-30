package com.example.enigma2048;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GestureDetectorCompat;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.google.android.material.appbar.MaterialToolbar;

public class PlayFragment extends Fragment {
    private RuntimeStateViewModel viewModel;
    private TextView score;
    private TextView moves;
    private TextView time;
    private TableLayout board;
    private int[] boardCache;
    private GestureDetectorCompat gestureDetector;

    public PlayFragment() {
        super(R.layout.fragment_play);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, HomeFragment.class, null)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(android.view.View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        score = view.findViewById(R.id.score);
        moves = view.findViewById(R.id.x);
        time = view.findViewById(R.id.y);
        board = view.findViewById(R.id.board);

        viewModel = new ViewModelProvider(requireActivity()).get(RuntimeStateViewModel.class);
        viewModel.observe(getViewLifecycleOwner(), state -> {
            score.setText(String.valueOf(state.getScore()));
            moves.setText(String.valueOf(state.getMoves()));
            time.setText(String.valueOf(state.getTime()));

            int[] array = new int[state.getBoardCellCount()];
            for(int i = 0; i < array.length; i++) array[i] = state.getBoardCell(i);
            if (boardCache != null && boardCache == array) {
                return;
            } else {
                boardCache = array;
            }
            board.removeAllViews();

            if (state.getBoardCellCount() == 0) {
                RuntimeState.Builder builder = state.toBuilder();
                for (int i = 0; i < 16; i++) {
                    builder.addBoardCell(0);
                }
                viewModel.set(builder.build());
                return;
            } else {
                Log.d("Board", String.valueOf(state.getBoardCellCount()));

                for (int i = 0; i < 4; i++) {
                    board.addView(new TableRow(getActivity()), new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                    ));
                    TableRow row = (TableRow) board.getChildAt(i);
                    for (int j = 0; j < 4; j++) {
                        row.addView(new TextView(getActivity()), new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT,
                                1.0f
                        ));
                        TextView cell = (TextView) row.getChildAt(j);
                        cell.setGravity(Gravity.CENTER);
                        int value = state.getBoardCell(i * 4 + j);
                        if (value == 0) {
                            cell.setText(String.valueOf(value));
                        } else {
                            cell.setText(String.valueOf(value));
                        }
                    }
                }
            }
        });

        board.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, HomeFragment.class, null)
                    .commit();
        });
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(false);

        viewModel.setPreviousGame(true);
    }

    private static class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            float diffX = event2.getX() - event1.getX();
            float diffY = event2.getY() - event1.getY();
            Log.d("Swipe", "diffX: " + diffX + " diffY: " + diffY);

            // Check for a right swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe right detected
                    Log.d("Swipe", "right");
                } else {
                    // Swipe left detected
                    Log.d("Swipe", "left");
                }
                return true;
            }

            // Check for a down swipe
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    // Swipe down detected
                    Log.d("Swipe", "down");
                } else {
                    // Swipe up detected
                    Log.d("Swipe", "up");
                }
                return true;
            }

            return false;
        }
    }
}