package com.example.niklas.lab3b.Model;

import android.bluetooth.BluetoothDevice;

import com.example.niklas.lab3b.Model.BTConnectionStates.BTConnectionState;
import com.example.niklas.lab3b.Model.BTConnectionStates.ScanForDeviceState;
import com.example.niklas.lab3b.DeviceActivity;
import com.example.niklas.lab3b.MainActivity;

/**
 * This class is used to handle the state machine which is used to
 * set up the connection, remove the connection and send and receive
 * data from the microbit.
 */
public class StateHandler {
    private static BTConnectionState btConnectionState;

    /**
     * This creates the state machine, must be called first to establish a
     * bluetooth connection.
     * @param mainActivity The Main Activity of the project.
     */
    public static void initConnectionState(MainActivity mainActivity) {
        btConnectionState = new ScanForDeviceState(mainActivity);
    }

    /**
     * Initiates BLE for the device.
     * Ends Main activity if BLE is not supported
     * @return True is BLE is on, false if not
     */
    public static boolean initBLE() {
        if (btConnectionState != null) {
            return btConnectionState.initBLE();
        }
        return false;
    }

    /**
     * Starts a scan for BLE devices
     * @param enable True is scan is to be made, else false
     */
    public static boolean scanLeDevice(boolean enable) {
        if (btConnectionState != null)
            return btConnectionState.scanLeDevice(enable);
        return false;
    }

    /**
     * If a device is selected, store the device in memory and start
     * the connection process.
     * @param device The device to connect to.
     */
    public static void onDeviceSelected(BluetoothDevice device) {
        if (btConnectionState != null)
            btConnectionState = btConnectionState.onDeviceSelected(device);
    }

    /**
     * Starts the connection to a device.
     * @param deviceActivity The device activity that is used when a device is connected.
     */
    public static void connect(DeviceActivity deviceActivity) {
        btConnectionState = btConnectionState.connect(deviceActivity);
    }

    /**
     * Disconnect from a device.
     */
    public static void disconnectDevice() {
        if (btConnectionState != null)
            btConnectionState = btConnectionState.disconnectDevice();
    }

    /**
     * Stops the transfer from the connected device.
     * The device is still connected.
     */
    public static void stopTransfer() {
        if (btConnectionState != null)
            btConnectionState.stopTransfer();
    }

    /**
     * Restarts the data transfer from an already connected device.
     */
    public static void startTransfer() {
        if (btConnectionState != null) {
            btConnectionState.startTransfer();
        }
    }
}
