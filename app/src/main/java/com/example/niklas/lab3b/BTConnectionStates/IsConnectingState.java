package com.example.niklas.lab3b.BTConnectionStates;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import com.example.niklas.lab3b.DeviceActivity;
import com.example.niklas.lab3b.MainActivity;


public class IsConnectingState extends SelectedDeviceState {
    public IsConnectingState(MainActivity mainActivity, BluetoothAdapter mBluetoothAdapter, BluetoothDevice device, Handler handler) {
        super(mainActivity, mBluetoothAdapter, device, handler);
    }

    @Override
    public BTConnectionState connect(DeviceActivity deviceActivity) {
        if (connectedDevice != null) {
            // register call backs for bluetooth gatt
            super.deviceActivity = deviceActivity;
            handler.post(() -> deviceActivity.setmDeviceText(connectedDevice.toString()));
            Log.i("connecters", "connectGatt called");
            return new ConnectedState(mainActivity, mBluetoothAdapter, connectedDevice, handler,deviceActivity);
        }
        Log.i("connecters", "No connected device, could not connect");
        deviceActivity.finish();
        return new ScanForDeviceState(mainActivity);
    }


}
