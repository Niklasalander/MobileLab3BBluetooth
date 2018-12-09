package com.example.niklas.lab3b;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *  * Code is based on https://github.com/anderslmatkthdotse/MicrobitUART
 * An example on how to use the Android BLE API to connect to a BLE device, in this case
 * a BBC Micro:bit, and read some data from UART.
 * The actual manipulation of the sensors services and characteristics is performed in the
 * DeviceActivity class.
 * NB! This example only aims to demonstrate the basic functionality in the Android BLE API.
 * Checks for life cycle connectivity, correct service, nulls et c. is not fully implemented.
 * This code should also be refactored into a set of suitable classes.
 * A state machine to handle the different states, not paired/paired/connected/receiving data/...
 * would be nice.
 * This is left for the student to implement.
 * <p/>
 * More elaborate example on Android BLE:
 * http://developer.android.com/guide/topics/connectivity/bluetooth-le.html
 * Documentation on the BBC Micro:bit:
 * https://lancaster-university.github.io/microbit-docs/ble/profile/
 */
public class MainActivity extends AppCompatActivity {

    public static final String BBC_MICRO_BIT = "BBC micro:bit";

    public static final int REQUEST_ENABLE_BT = 1000;
    public static final int REQUEST_ACCESS_LOCATION = 1001;

    private ArrayList<BluetoothDevice> mDeviceList;
    private BTDeviceArrayAdapter mAdapter;
    private TextView mScanInfoView;

    /**
     * Tries to initiate BLE.
     * If bluetooth is not on, start an activity for the user to start BLE.
     */
    private void initBLE() {
        if (!StateHandler.initBLE()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // callback for ActivityCompat.requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION: {
                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO:
                    // ...
                } else {
                    // stop this activity
                    this.finish();
                }
                break;
            }
        }
    }

    // callback for request to turn on BT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if user chooses not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * When a device is selected, start the connection process.
     * @param position The position of the device in the array of devices.
     */
    private void onDeviceSelected(int position) {
        StateHandler.onDeviceSelected(mDeviceList.get(position));
    }


    /**
     * Below: Manage activity, and hence bluetooth, life cycle,
     * via onCreate, onStart and onStop.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StateHandler.initConnectionState(this);

        mScanInfoView = findViewById(R.id.scanInfo);

        Button startScanButton = findViewById(R.id.startScanButton);
        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceList.clear();
                StateHandler.scanLeDevice(true);
            }
        });

        ListView scanListView = findViewById(R.id.scanListView);
        mDeviceList = new ArrayList<>();
        mAdapter = new BTDeviceArrayAdapter(this, mDeviceList);
        scanListView.setAdapter(mAdapter);
        scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                onDeviceSelected(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBLE();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        StateHandler.scanLeDevice(false);
        mDeviceList.clear();
        mAdapter.notifyDataSetChanged();
        // NB !release additional resources
        // ...BleGatt...
    }

    // short messages
    protected void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * When a new device is found, add it to the array of available devices.
     * @param name The name of the device.
     * @param device The found deivce.
     */
    public void newDeviceFound(String name, BluetoothDevice device) {
        if (name != null
                && name.contains(BBC_MICRO_BIT)
                && !mDeviceList.contains(device)) {
            mDeviceList.add(device);
            mAdapter.notifyDataSetChanged();
            String msg =
                    getString(R.string.found_devices_msg, mDeviceList.size());
            mScanInfoView.setText(msg);
        }
    }

    public void setmScanInfoView(String msg) {
        mScanInfoView.setText(msg);
    }
}
