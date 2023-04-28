package com.example.enigma2048;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    public PlayFragment() {
        super(R.layout.fragment_play);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    public void swipeRight()
    {
        List<Integer> a=viewModel.get().getBoardCellList(); //list of integers in java
        for(int i=0;i<16;i=i+4)
        {
            for(int j=i+3; j>i+1; j--)
            {
                if(a.get(j)==a.get(j-1)
                {
                    a.get(j)+=a.get(j-1);
                    a.get(j-1)=0;
                }
            }
        }
        for(int i=0;i<16;i=i+4)
        {
            for(int j=i+3; j>i+1; j--)
            {
                if(a.get(j)==0 && a.get(j-1)!=0)
                {
                    a.get(j)+=a.get(j-1);
                    a.get(j-1)=0;
                }
            }
        }
        viewModel.setBoard(a);
    }

}