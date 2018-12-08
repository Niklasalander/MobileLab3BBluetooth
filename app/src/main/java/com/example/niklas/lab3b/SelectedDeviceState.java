package com.example.niklas.lab3b;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;

import java.util.UUID;

public class SelectedDeviceState extends BTConnectionState {
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID  UARTSERVICE_SERVICE_UUID =
            UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID UART_TX_CHARACTERISTIC_UUID = // receive data(!)
            UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID UART_RX_CHARACTERISTIC_UUID = // transmit data (!)
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID TEST = // transmit data (!)
            UUID.fromString("e97dd91d-251d-470a-a062-fa1922dfa9a8");
    protected DeviceActivity deviceActivity;
    protected BluetoothGatt mBluetoothGatt = null;
    protected BluetoothGattService mUartService = null;
    protected DataHandler dataHandler;

    public SelectedDeviceState(MainActivity mainActivity, BluetoothAdapter mBluetoothAdapter, BluetoothDevice device, Handler handler) {
        super(mainActivity, mBluetoothAdapter, device, handler);
        dataHandler = DataHandler.getInstance();
    }

    public BTConnectionState connect(DeviceActivity deviceActivity) {
        return this;
    }

    @Override
    public BTConnectionState disconnectDevice() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        if (dataHandler != null)
            dataHandler.reset();
        if (deviceActivity != null)
            deviceActivity.finish();
        return super.disconnectDevice();
    }

}
