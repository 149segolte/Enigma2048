package com.example.enigma2048;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class RuntimeStateViewModel extends ViewModel {
    private final MutableLiveData<RuntimeState> currentData = new MutableLiveData<RuntimeState>();
    public void setValue(RuntimeState state) {
        currentData.setValue(state);
    }
    public RuntimeState getValue() {
        return currentData.getValue();
    }
    public void observe(LifecycleOwner owner, Observer<? super RuntimeState> observer) {
        currentData.observe(owner, observer);
    }
    public void resetValue() {
        currentData.setValue(RuntimeState.getDefaultInstance());
    }
}