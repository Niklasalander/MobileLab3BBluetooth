package com.example.niklas.lab3b;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niklas.lab3b.Model.StateHandler;


/**
 *  * Code is based on https://github.com/anderslmatkthdotse/MicrobitUART
 * This is where we manage the BLE device and the corresponding services, characteristics et c.
 * BluetoothGattCallback.onCharacteristicChanged receives some text data from the Micro:bit
 * and displays it.
 * <p>
 * NB! In this simple example there is no other way to turn off notifications than to
 * leave the activity (the BluetoothGatt is disconnected and closed in activity.onStop).
 * The code should also be refactored into a set of suitable classes.
 * This is left for the student to implement.
 */
public class DeviceActivity extends AppCompatActivity {

    /**
     * Initiate connection to device.
     */
    @Override
    protected void onStart() {
        super.onStart();
        bpmTextView.setText(getString(R.string.no_bpm_available));
        StateHandler.connect(this);
    }

    /**
     * Disconnect from the device.
     */
    @Override
    protected void onStop() {
        super.onStop();
        StateHandler.disconnectDevice();
        finish();
    }

    private TextView mDeviceView;
    private TextView mDataView;
    private TextView bpmTextView;
    private Button startTransferButton;
    private Button stopTransferButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mDeviceView = findViewById(R.id.deviceView);
        mDataView = findViewById(R.id.dataView);
        bpmTextView = findViewById(R.id.bpmView);
        startTransferButton = findViewById(R.id.startTransferButton);
        startTransferButton.setOnClickListener(event -> handleStartTransfer());
        stopTransferButton = findViewById(R.id.stopTransferButton);
        stopTransferButton.setOnClickListener(event -> handleStopTransfer());
    }

    /**
     * Start transfer again when the user presses the start button.
     */
    private void handleStartTransfer() {
        StateHandler.startTransfer();
    }

    /**
     * Stop transfer when the user presses the stop button.
     */
    private void handleStopTransfer() {
        StateHandler.stopTransfer();
    }

    protected void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setmDeviceText(String s) {
        mDeviceView.setText(s);
    }

    public void setmDataText(String s) {
        mDataView.setText(s);
    }

    public void setBpmTextView(String s) {
        bpmTextView.setText(s);
    }
}