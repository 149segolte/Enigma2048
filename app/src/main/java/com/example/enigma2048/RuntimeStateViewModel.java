package com.example.enigma2048;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class RuntimeStateViewModel extends ViewModel {
    private final MutableLiveData<RuntimeState> data = new MutableLiveData<>();

    public void set(RuntimeState state) {
        data.setValue(state);
    }

    public void setScore(int score) {
        data.setValue(data.getValue().toBuilder().setScore(score).build());
    }

    public void setMoves(int moves) {
        data.setValue(data.getValue().toBuilder().setMoves(moves).build());
    }

    public void setHigh(int high) {
        data.setValue(data.getValue().toBuilder().setHigh(high).build());
    }

    public void setBoardCell(int index, int value) {
        data.setValue(data.getValue().toBuilder().setBoardCell(index, value).build());
    }

    public void setPreviousGame(boolean previousGame) {
        data.setValue(data.getValue().toBuilder().setPreviousGame(previousGame).build());
    }

    public void setBoard(List<Integer> board) {
        data.setValue(data.getValue().toBuilder().clearBoardCell().addAllBoardCell(board).build());
    }

    public RuntimeState get() {
        return data.getValue();
    }

    public void observe(LifecycleOwner owner, Observer<RuntimeState> observer) {
        data.observe(owner, observer);
    }
}