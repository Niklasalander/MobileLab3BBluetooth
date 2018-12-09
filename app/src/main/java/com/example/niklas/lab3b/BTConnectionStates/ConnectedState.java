package com.example.niklas.lab3b.BTConnectionStates;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.util.Log;

import com.example.niklas.lab3b.MainActivity;
import com.example.niklas.lab3b.NoiseTimeout;
import com.example.niklas.lab3b.R;
import com.example.niklas.lab3b.StateHandler;

import java.util.List;
import java.util.Timer;

public class ConnectedState extends SelectedDeviceState {
    protected static final int NOISE_TIMEOUT_TIME = 2000;
    public ConnectedState(MainActivity mainActivity, BluetoothAdapter mBluetoothAdapter, BluetoothDevice device, Handler handler) {
        super(mainActivity, mBluetoothAdapter, device, handler);
        if (connectedDevice != null) {
            // register call backs for bluetooth gatt
            mBluetoothGatt = connectedDevice.connectGatt(deviceActivity, false, mBtGattCallback);
            Log.i("connecters", "connectGatt called");
        }
    }

    /**
     * Callbacks for bluetooth gatt changes/updates
     * The documentation is not clear, but (some of?) the callback methods seems to
     * be executed on a worker thread - hence use a Handler when updating the ui.
     */
    private BluetoothGattCallback mBtGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("BluetoothGattCallback", "onConnectionStateChange");

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i("BluetoothGattCallback", "new state conn");
                mBluetoothGatt = gatt;
                gatt.discoverServices();
                handler.post(() -> deviceActivity.setmDataText(deviceActivity.getString(R.string.connected_msg)));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BluetoothGattCallback", "new state diss");
                mBluetoothGatt = null;
                handler.post(() -> deviceActivity.setmDataText(deviceActivity.getString(R.string.disconnected_msg)));
                StateHandler.disconnectDevice();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            Log.i("BluetoothGattCallback", "onServicesDiscovered");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                // debug, list services
//                BleLogger.logServices(gatt.getServices());
                List<BluetoothGattService> list = gatt.getServices();
                for (BluetoothGattService s : list) {
                    Log.i("mUartService", s.toString() + " | " + s.getUuid());
                }
                // Get the UART service
                mUartService = gatt.getService(UARTSERVICE_SERVICE_UUID);
                Log.i("mUartService",
                        mUartService==null? "null" : mUartService.getUuid().toString() );

                if (mUartService != null) {
                    // debug, list characteristics
//                    BleLogger.logCharacteristicsForService(mUartService);

                    // Enable indications for UART data
                    // 1. Enable notification/indication on ble peripheral (Micro:bit)
                    BluetoothGattCharacteristic txCharac =
                            mUartService.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
                    BluetoothGattDescriptor descriptor =
                            txCharac.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);

                    // 2. Enable indications/notification locally (this android device)
                    mBluetoothGatt.setCharacteristicNotification(txCharac, true);
                    Log.i("onServiceDiscovered", "notification/indication set");
                } else {
                    handler.post(() -> showToast("Uart-data characteristic not found"));
                    StateHandler.disconnectDevice();
                }
            }
        }

        @Override
        public void onDescriptorWrite(final BluetoothGatt gatt, BluetoothGattDescriptor
                descriptor, int status) {
            Log.i("BluetoothGattCallback", "onDescriptorWrite");

            Log.i("onDescriptorWrite", "descriptor " + descriptor.getUuid());
            Log.i("onDescriptorWrite", "status " + status);

            if (CLIENT_CHARACTERISTIC_CONFIG.equals(descriptor.getUuid()) &&
                    status == BluetoothGatt.GATT_SUCCESS) {

                handler.post(() -> {
                    showToast("Uart-data notifications enabled");
                    deviceActivity.setmDeviceText(deviceActivity.getString(R.string.uart_sensor_info));
                });
            }
        }

        /**
         * Callback called on characteristic changes, e.g. when a data value is changed.
         * This is where we receive notifications on updates of accelerometer data.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            Log.i("BluetoothGattCallback", "onCharacteristicChanged: " + characteristic.toString());

            // TODO: check which service and characteristic caused this call
            BluetoothGattCharacteristic uartTxCharacteristic =
                    mUartService.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);

            // We assume we receive a string from the Micro:bit
            final String msg = uartTxCharacteristic.getStringValue(0);
            handler.post(() -> {
//                    showToast(msg);
                Log.i("uartMessage", msg);
                deviceActivity.setmDataText(msg);
                if (!dataHandler.newValue(msg)) {
                    //start timer
                    initTimeout();
                }

            });
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            Log.i("BluetoothGattCallback",
                    "onCharacteristicWrite: " + characteristic.getUuid().toString());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            Log.i("BluetoothGattCallback",
                    "onCharacteristicRead: " + characteristic.getUuid().toString());
        }
    };

    @Override
    public void startTransfer() {
/*        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.getConnectionState(connectedDevice) == BluetoothGatt.STATE_DISCONNECTED)
                mBluetoothGatt.connect();
        }*/
        if (mBluetoothGatt != null && mUartService != null) {
            BluetoothGattCharacteristic txCharac =
                    mUartService.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
            BluetoothGattDescriptor descriptor =
                    txCharac.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
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

    private void initTimeout() {
        stopTransfer();
        handler.post(() -> deviceActivity.setBpmTextView(deviceActivity.getString(R.string.to_much_noise)));
        NoiseTimeout noiseTimeout = new NoiseTimeout();
        Timer timer = new Timer();
        timer.schedule(noiseTimeout, NOISE_TIMEOUT_TIME);
    }

}
