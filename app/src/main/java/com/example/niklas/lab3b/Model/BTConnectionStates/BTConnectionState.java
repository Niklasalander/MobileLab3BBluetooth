package com.example.niklas.lab3b.Model.BTConnectionStates;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.widget.Toast;

import com.example.niklas.lab3b.DeviceActivity;
import com.example.niklas.lab3b.MainActivity;

/**
 * State machine to handle the different stages of a connection to
 * a BLE device. The state may be, Scanning for devices. Connecting to a device.
 * Connected to a specific device.
 */
public class BTConnectionState {
    protected MainActivity mainActivity;
    protected DeviceActivity deviceActivity;
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothDevice connectedDevice;


    protected Handler handler;

    protected BTConnectionState(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.handler = new Handler();
        this.connectedDevice = ConnectedDevice.getInstance();
    }

    protected BTConnectionState(MainActivity mainActivity, BluetoothAdapter mBluetoothAdapter, BluetoothDevice device, Handler handler) {
        this.mainActivity = mainActivity;
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.connectedDevice = device;
        this.handler = handler;
    }

    /**
     * Initiates BLE for the device.
     * Ends Main activity if BLE is not supported
     * @return True is BLE is on, false if not
     */
    public boolean initBLE() {
        return false;
    }

    /**
     * Starts a scan for BLE devices
     * @param enable True is scan is to be made, else false
     */
    public boolean scanLeDevice(final boolean enable) {
        return false;
    }

    /**
     * If a device is selected, store the device in memory and start
     * the connection process.
     * @param device The device to connect to.
     */
    public BTConnectionState onDeviceSelected(BluetoothDevice device) {
        return this;
    }

    /**
     * Starts the connection to a device.
     * @param deviceActivity The device activity that is used when a device is connected.
     */
    public BTConnectionState connect(DeviceActivity deviceActivity) {
        return this;
    }

    /**
     * Shows a toast on screen.
     * @param msg The toast message.
     */
    protected void showToast(String msg) {
        Toast toast = Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Disconnect from a device.
     */
    public BTConnectionState disconnectDevice() {
        connectedDevice = null;
        ConnectedDevice.removeInstance();
        return new ScanForDeviceState(mainActivity);
    }

    /**
     * Restarts the data transfer from an already connected device.
     */
    public void startTransfer() {
        // do nothing
    }

    /**
     * Stops the transfer from the connected device.
     * The device is still connected.
     */
    public void stopTransfer() {
        // do nothing
    }
}
