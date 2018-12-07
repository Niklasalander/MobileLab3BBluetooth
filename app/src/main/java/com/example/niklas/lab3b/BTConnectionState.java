package com.example.niklas.lab3b;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.widget.Toast;


public class BTConnectionState {
    // What datamembers do we need?
    protected MainActivity mainActivity;
    protected BluetoothAdapter mBluetoothAdapter;
    protected Handler handler;

    protected BTConnectionState(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.handler = new Handler();
    }


    public boolean initBLE() {
        return false;
    }

    public void scanLeDevice(final boolean enable) {

    }




    protected void showToast(String msg) {
        Toast toast = Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
