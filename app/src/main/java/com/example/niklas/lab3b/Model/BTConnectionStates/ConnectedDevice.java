package com.example.niklas.lab3b.Model.BTConnectionStates;

import android.bluetooth.BluetoothDevice;

/**
 * Code is based on https://github.com/anderslmatkthdotse/MicrobitUART
 * An ugly hack to administrate the selected Bluetooth device
 * between activities.
 */
class ConnectedDevice {

    private static BluetoothDevice theDevice = null;
    private static final Object lock = new Object();

    private ConnectedDevice() {
    }

    static BluetoothDevice getInstance() {
        synchronized (lock) {
            return theDevice;
        }
    }

    static void setInstance(BluetoothDevice newDevice) {
        synchronized (lock) {
            theDevice = newDevice;
        }
    }

    /**
     * The device will be null.
     */
    static void removeInstance() {
        synchronized(lock) {
            theDevice = null;
        }
    }
}
