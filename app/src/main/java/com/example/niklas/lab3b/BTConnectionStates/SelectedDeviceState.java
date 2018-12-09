package com.example.niklas.lab3b.BTConnectionStates;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;

import com.example.niklas.lab3b.DataHandler;
import com.example.niklas.lab3b.DeviceActivity;
import com.example.niklas.lab3b.MainActivity;
import com.example.niklas.lab3b.R;

import java.util.UUID;

/**
 *  * Code is based on https://github.com/anderslmatkthdotse/MicrobitUART
 */
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

    public SelectedDeviceState(MainActivity mainActivity, BluetoothAdapter mBluetoothAdapter, BluetoothDevice connectedDevice, Handler handler, DeviceActivity deviceActivity) {
        super(mainActivity, mBluetoothAdapter, connectedDevice, handler);
        this.deviceActivity = deviceActivity;
        dataHandler = DataHandler.getInstance();
    }

    @Override
    public BTConnectionState connect(DeviceActivity deviceActivity) {
        return this;
    }

    @Override
    public BTConnectionState disconnectDevice() {
        stopTransfer();
        if (mBluetoothGatt != null)
            mBluetoothGatt.disconnect();
        if (mBluetoothGatt != null)
            mBluetoothGatt.close();
        if (dataHandler != null)
            dataHandler.reset();
        if (deviceActivity != null)
            deviceActivity.finish();
        return super.disconnectDevice();
    }
    @Override
    public void stopTransfer() {
        if (mBluetoothGatt != null && mUartService != null) {
            BluetoothGattCharacteristic txCharac =
                    mUartService.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
            BluetoothGattDescriptor descriptor =
                    txCharac.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

/*    @Override
    public void stopTransfer() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            if (dataHandler != null)
                dataHandler.reset();
            handler.post(() -> deviceActivity.setBpmTextView(deviceActivity.getString(R.string.no_bpm_available)));
        }
    }*/
}
