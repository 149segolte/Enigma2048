package com.example.enigma2048;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RuntimeStateViewModel extends ViewModel {
    private final MutableLiveData<RuntimeState> currentData = new MutableLiveData<RuntimeState>();
    public void setValue(RuntimeState state) {
        currentData.setValue(state);
    }
    public LiveData<RuntimeState> getValue() {
        return currentData;
    }

    public void resetValue() {
        currentData.setValue(RuntimeState.getDefaultInstance());
    }
}