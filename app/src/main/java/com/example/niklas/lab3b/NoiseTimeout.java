package com.example.niklas.lab3b;

import java.util.TimerTask;

public class NoiseTimeout extends TimerTask {
    public NoiseTimeout() {

    }
    @Override
    public void run() {
        StateHandler.startTransfer();
    }

}