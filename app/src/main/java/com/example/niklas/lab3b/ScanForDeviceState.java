package com.example.niklas.lab3b;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import static com.example.niklas.lab3b.MainActivity.REQUEST_ACCESS_LOCATION;

public class ScanForDeviceState extends BTConnectionState {
    private static final long SCAN_PERIOD = 5000;
//    private Handler handler;
//    private MainActivity mainActivity;
    private boolean mScanning;

    public ScanForDeviceState(MainActivity mainActivity) {
        super(mainActivity);
    }

    public boolean initBLE() {
        if (!mainActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            handler.post(() -> showToast("BLE is not supported"));

            mainActivity.finish();
        } else {
            showToast("BLE is supported");
            // Access Location is a "dangerous" permission
            int hasAccessLocation = ContextCompat.checkSelfPermission(mainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasAccessLocation != PackageManager.PERMISSION_GRANTED) {
                // ask the user for permission
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
                // the callback method onRequestPermissionsResult gets the result of this request
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on BT
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;

    }

    /*
     * Scan for BLE devices.
     */
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            if (!mScanning) {
                // stop scanning after a pre-defined scan period, SCAN_PERIOD
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mScanning) {
                            mScanning = false;
                            // stop/startLeScan is deprecated from API 21,
                            // but we support API 18 and up
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            showToast("BLE scan stopped");
                        }
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                handler.post(() -> {
                    mainActivity.setmScanInfoView(mainActivity.getString(R.string.no_devices_msg));
                    showToast("BLE scan started");
                });
            }
        } else {
            if (mScanning) {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                handler.post(() -> showToast("BLE scan stopped"));
            }
        }
    }

    /**
     * Implementation of the device scan callback.
     * Only adding devices matching name BBC_MICRO_BIT.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    Log.i("Asd", "asd");
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Asd", "qwe");
                            String name = device.getName();
                            mainActivity.newDeviceFound(name, device);
                        }
                    });
                }
            };
}
