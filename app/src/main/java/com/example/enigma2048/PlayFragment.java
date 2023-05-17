package com.example.enigma2048;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlayFragment extends Fragment {
    private RuntimeStateViewModel viewModel;
    private TextView score;
    private TextView moves;
    private TextView high;
    private TableLayout board;
    private List<Integer> currBoard;
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
        high = view.findViewById(R.id.y);
        board = view.findViewById(R.id.board);

        viewModel = new ViewModelProvider(requireActivity()).get(RuntimeStateViewModel.class);
        viewModel.observe(getViewLifecycleOwner(), state -> {
            score.setText(String.valueOf(state.getScore()));
            moves.setText(String.valueOf(state.getMoves()));
            high.setText(String.valueOf(state.getHigh()));

            List<Integer> array = viewModel.get().getBoardCellList();
            if (array.equals(currBoard)) {
                return;
            } else {
                currBoard = array;
            }

            if (board.getChildCount() != 4) {
                return;
            }
            for (int i = 0; i < 4; i++) {
                TableRow row = (TableRow) board.getChildAt(i);
                for (int j = 0; j < 4; j++) {
                    TextView cell = (TextView) row.getChildAt(j);
                    cell.setText("");
                    cell.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
                }
            }

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
                    TableRow row = (TableRow) board.getChildAt(i);
                    for (int j = 0; j < 4; j++) {
                        TextView cell = (TextView) row.getChildAt(j);
                        int value = state.getBoardCell(i * 4 + j);
                        if (value == 0) {
                            cell.setText("");
                        } else {
                            cell.setText(String.valueOf(value));
                            int color_saturation = (10 - (int) (Math.log(value) / Math.log(2))) * 10;
                            int backColor = ColorUtils.blendARGB(ContextCompat.getColor(getContext(), R.color.md_theme_light_secondary), ContextCompat.getColor(getContext(), R.color.md_theme_light_background), 1 - (float) color_saturation / 100);
                            cell.setBackgroundColor(backColor);
                            int textColor = ColorUtils.blendARGB(ContextCompat.getColor(getContext(), R.color.md_theme_light_onSecondary), ContextCompat.getColor(getContext(), R.color.md_theme_light_onBackground), 1 - (float) color_saturation / 100);
                            cell.setTextColor(textColor);
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

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 150;
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            float angle = (float) Math.atan2(diffY, diffX) * 180 / (float) Math.PI;
            float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            viewModel.setMoves(viewModel.get().getMoves() + 1);

            List<Integer> board = currBoard.stream().map(i -> i).collect(Collectors.toList());
            if (distance > SWIPE_THRESHOLD) {
                if (angle > -45 && angle <= 60) {
                    Log.d("Swipe", "Right");
                    board = swipeRight();
                }
                if (angle > 60 && angle <= 120) {
                    Log.d("Swipe", "Down");
                    board = swipeDown();
                }
                if (angle > 120 || angle <= -120) {
                    Log.d("Swipe", "Left");
                    board = swipeLeft();
                }
                if (angle > -120 && angle <= -45) {
                    Log.d("Swipe", "Up");
                    board = swipeUp();
                }
            }

            board = randomTile(board);
            return true;
        }
    }

    public List<Integer> swipeRight() {
        List<Integer> board = currBoard.stream().collect(Collectors.toList());
        for (int i = 0; i < 4; ++i) {
            for (int j = 2; j >= 0; --j) {
                int index = i * 4 + j;
                if (board.get(index) != 0) {
                    int k = j + 1;
                    while (k < 4 && board.get(i * 4 + k) == 0) {
                        ++k;
                    }
                    if (k < 4 && board.get(i * 4 + k).equals(board.get(index))) {
                        board.set(i * 4 + k, board.get(i * 4 + k) * 2);
                        board.set(index, 0);
                    } else {
                        k = j + 1;
                        while (k < 4 && board.get(i * 4 + k) == 0) {
                            ++k;
                        }
                        --k;
                        if (k != j) {
                            board.set(i * 4 + k, board.get(index));
                            board.set(index, 0);
                        }
                    }
                }
            }
        }
        return board;
    }

    public List<Integer> swipeLeft() {
        List<Integer> board = currBoard.stream().map(i -> i).collect(Collectors.toList());
        for (int i = 0; i < 4; ++i) {
            for (int j = 1; j < 4; ++j) {
                int index = i * 4 + j;
                if (board.get(index) != 0) {
                    int k = j - 1;
                    while (k >= 0 && board.get(i * 4 + k) == 0) {
                        --k;
                    }
                    if (k >= 0 && board.get(i * 4 + k).equals(board.get(index))) {
                        board.set(i * 4 + k, board.get(i * 4 + k) * 2);
                        board.set(index, 0);
                    } else {
                        k = j - 1;
                        while (k >= 0 && board.get(i * 4 + k) == 0) {
                            --k;
                        }
                        ++k;
                        if (k != j) {
                            board.set(i * 4 + k, board.get(index));
                            board.set(index, 0);
                        }
                    }
                }
            }
        }
        return board;
    }

    public List<Integer> swipeDown() {
        List<Integer> board = currBoard.stream().map(i -> i).collect(Collectors.toList());
        for (int j = 0; j < 4; ++j) {
            for (int i = 2; i >= 0; --i) {
                int index = i * 4 + j;
                if (board.get(index) != 0) {
                    int k = i + 1;
                    while (k < 4 && board.get(k * 4 + j) == 0) {
                        ++k;
                    }
                    if (k < 4 && board.get(k * 4 + j).equals(board.get(index))) {
                        board.set(k * 4 + j, board.get(k * 4 + j) * 2);
                        board.set(index, 0);
                    } else {
                        k = i + 1;
                        while (k < 4 && board.get(k * 4 + j) == 0) {
                            ++k;
                        }
                        --k;
                        if (k != i) {
                            board.set(k * 4 + j, board.get(index));
                            board.set(index, 0);
                        }
                    }
                }
            }
        }
        return board;
    }

    public List<Integer> swipeUp() {
        List<Integer> board = currBoard.stream().map(i -> i).collect(Collectors.toList());
        for (int j = 0; j < 4; ++j) {
            for (int i = 1; i < 4; ++i) {
                int index = i * 4 + j;
                if (board.get(index) != 0) {
                    int k = i - 1;
                    while (k >= 0 && board.get(k * 4 + j) == 0) {
                        --k;
                    }
                    if (k >= 0 && board.get(k * 4 + j).equals(board.get(index))) {
                        board.set(k * 4 + j, board.get(k * 4 + j) * 2);
                        board.set(index, 0);
                    } else {
                        k = i - 1;
                        while (k >= 0 && board.get(k * 4 + j) == 0) {
                            --k;
                        }
                        ++k;
                        if (k != i) {
                            board.set(k * 4 + j, board.get(index));
                            board.set(index, 0);
                        }
                    }
                }
            }
        }
        return board;
    }

    // random tile genertor
    public List<Integer> randomTile(List<Integer> board) {
        if (board.contains(0)) {
            Random rand = new Random();
            int flag = 0;
            while (flag != 1) {
                int random = rand.nextInt(16);
                if (board.get(random) == 0) {
                    //place a tile
                    int n = rand.nextInt(4);
                    if (n == 1)
                        board.set(random, 4);
                    else
                        board.set(random, 2);
                    flag = 1;
                }
            }
        } else {
            gameOver();
        }
        return board;
    }

    // function for check game over
    public boolean gameOver(){
        List<Integer> a=viewModel.get().getBoardCellList().stream().collect(Collectors.toList());
        Collections.sort(a);
        if(a.get(0)==0)
            return true;
        else {
            viewModel.setPreviousGame(false);
            return false;
        }}
    public void calculateScoreAfterMove() {
        List<Integer> board = viewModel.get().getBoardCellList().stream().collect(Collectors.toList());
        int score = 0;
        for (int i = 0; i < 16; i++) {
            int tileValue = board.get(i);
            if (tileValue != 0) {
                score += tileValue;
            }
        }
        int prevScore = 0;
        for (int i = 0; i < 16; i++) {
            int tileValue = prevBoard.get(i);
            if (tileValue != 0) {
                prevScore += tileValue;
            }
        }
        int diff = score - prevScore;
        score = viewModel.get().getScore();
        viewModel.setScore((int) (score + (diff * Math.exp(diff / 2))));
    }
}