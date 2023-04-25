package com.example.enigma2048;

import androidx.datastore.core.Serializer;

import java.io.InputStream;
import java.io.OutputStream;

import kotlin.Unit;
import kotlin.coroutines.Continuation;

class RuntimeStateSerializer implements Serializer<RuntimeState> {
    @Override
    public RuntimeState getDefaultValue() {
        return RuntimeState.getDefaultInstance();
    }

    @Override
    public RuntimeState readFrom(InputStream input, Continuation<? super RuntimeState> continuation) {
        try {
            return RuntimeState.parseFrom(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object writeTo(RuntimeState t, OutputStream output, Continuation<? super Unit> continuation) {
        try {
            t.writeTo(output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}