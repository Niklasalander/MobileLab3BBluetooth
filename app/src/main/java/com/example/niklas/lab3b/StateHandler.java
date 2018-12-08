package com.example.niklas.lab3b;

import android.bluetooth.BluetoothDevice;

import com.example.niklas.lab3b.BTConnectionStates.BTConnectionState;
import com.example.niklas.lab3b.BTConnectionStates.ScanForDeviceState;

public class StateHandler {
    private static BTConnectionState btConnectionState;
    private StateHandler stateHandler;

//    public StateHandler getInstance() {
//        if (stateHandler == null)
//    }

    public static void initConnectionState(MainActivity mainActivity) {
        btConnectionState = new ScanForDeviceState(mainActivity);
    }

    public static boolean initBLE() {
        if (btConnectionState != null) {
            return btConnectionState.initBLE();
        }
        return false;
    }

    public static void scanLeDevice(boolean enable) {
        if (btConnectionState != null)
            btConnectionState.scanLeDevice(enable);
    }

    public static void onDeviceSelected(BluetoothDevice device) {
        if (btConnectionState != null)
            btConnectionState = btConnectionState.onDeviceSelected(device);
    }

    public static void connect(DeviceActivity deviceActivity) {
        btConnectionState = btConnectionState.connect(deviceActivity);
    }

    public static void disconnectDevice() {
        if (btConnectionState != null)
            btConnectionState = btConnectionState.disconnectDevice();
    }

    public static void stopTransfer() {
        if (btConnectionState != null)
            btConnectionState.stopTransfer();
    }

    public static void startTransfer() {
        if (btConnectionState != null) {
            btConnectionState.startTransfer();
        }
    }
}
