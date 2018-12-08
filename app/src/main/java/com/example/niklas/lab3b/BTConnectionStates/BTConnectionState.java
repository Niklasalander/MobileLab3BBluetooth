package com.example.niklas.lab3b.BTConnectionStates;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.widget.Toast;

import com.example.niklas.lab3b.DeviceActivity;
import com.example.niklas.lab3b.MainActivity;


public class BTConnectionState {
    // What datamembers do we need?
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

    public boolean initBLE() {
        return false;
    }

    public void scanLeDevice(final boolean enable) {

    }

    public BTConnectionState onDeviceSelected(BluetoothDevice device) {
        return this;
    }

    public BTConnectionState connect(DeviceActivity deviceActivity) {
        return this;
    }

    protected void showToast(String msg) {
        Toast toast = Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public BTConnectionState disconnectDevice() {
        connectedDevice = null;
        ConnectedDevice.removeInstance();
        return new ScanForDeviceState(mainActivity);
    }

    public void startTransfer() {

    }

    public void stopTransfer() {

    }
}
