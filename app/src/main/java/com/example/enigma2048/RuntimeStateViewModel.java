package com.example.enigma2048;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public void setTime(int time) {
        data.setValue(data.getValue().toBuilder().setTime(time).build());
    }

    public void setBoardCell(int index, int value) {
        data.setValue(data.getValue().toBuilder().setBoardCell(index, value).build());
    }

    public void setPreviousGame(boolean previousGame) {
        data.setValue(data.getValue().toBuilder().setPreviousGame(previousGame).build());
    }

    public void setBoard(int[] board) {
        data.setValue(data.getValue().toBuilder().clearBoardCell().addAllBoardCell(Arrays.stream(board).boxed().collect(Collectors.toList())).build());
    }

    public RuntimeState get() {
        return data.getValue();
    }

    public void observe(LifecycleOwner owner, Observer<RuntimeState> observer) {
        data.observe(owner, observer);
    }
}