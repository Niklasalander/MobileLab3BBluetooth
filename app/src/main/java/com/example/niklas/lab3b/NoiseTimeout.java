package com.example.niklas.lab3b;

import com.example.niklas.lab3b.Model.StateHandler;

import java.util.TimerTask;

/**
 * Timeout to start transfer again after an interuption.
 */
public class NoiseTimeout extends TimerTask {
    public NoiseTimeout() {

    }
    @Override
    public void run() {
        StateHandler.startTransfer();
    }

}