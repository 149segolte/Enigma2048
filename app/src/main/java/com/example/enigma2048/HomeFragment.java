package com.example.enigma2048;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(null);
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.continue_button) {
        } else if (id == R.id.play_button) {
        } else if (id == R.id.leaderboards_button) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, LeaderboardsFragment.class, null)
                    .commit();
        } else {
            getActivity().finishAffinity();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("status", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean previous_game = result.getBoolean("previous_game");
                Button continueButton = getActivity().findViewById(R.id.continue_button);
                if (previous_game) {
                    continueButton.setVisibility(View.VISIBLE);
                } else {
                    continueButton.setVisibility(View.GONE);
                }
            }
        });

        // This callback will only be called when the Fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
                dialogBuilder.setTitle("Are you sure you want to exit?");
                dialogBuilder.setMessage("Your progress will be lost.");
                dialogBuilder.setOnDismissListener((dialog) -> {});
                dialogBuilder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
                dialogBuilder.setPositiveButton("Yes", (dialog, which) -> getActivity().finishAffinity());
                dialogBuilder.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }
}