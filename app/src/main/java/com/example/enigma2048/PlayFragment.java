package com.example.enigma2048;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.google.android.material.appbar.MaterialToolbar;

public class PlayFragment extends Fragment {
    private RuntimeStateViewModel viewModel;
    private GestureDetectorCompat mDetector;
    private TableLayout board;

    public PlayFragment() {
        super(R.layout.fragment_play);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetectorCompat(getActivity(), new GesturesListener());

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

        viewModel = new ViewModelProvider(requireActivity()).get(RuntimeStateViewModel.class);
        viewModel.getValue().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                viewModel.resetValue();
            }
        });

        board = getActivity().findViewById(R.id.board);
        board.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, HomeFragment.class, null)
                    .commit();
        });
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(false);

        initializeBoard();
    }

    public void initializeBoard() {
        board.removeAllViews();
    }
    class GesturesListener extends GestureDetector.SimpleOnGestureListener {
        private final int UP = 0;
        private final int DOWN = 1;
        private final int LEFT = 2;
        private final int RIGHT = 3;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        int getDirection(double x1,double y1,double x2,double y2) {
            if(Math.abs(y2-y1)>Math.abs(x2-x1)) {
                if(y2>y1)
                    return DOWN;
                else
                    return UP;
            } else{
                if(x2>x1)
                    return RIGHT;
                else
                    return LEFT;
            }
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            int dir = getDirection(event1.getX(),event1.getY(),event2.getX(),event2.getY());
            if(dir == UP) {
            } else if(dir == DOWN) {
            } else if(dir == LEFT) {
            } else {
            }
            Toast.makeText(getActivity(), "Fling", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}