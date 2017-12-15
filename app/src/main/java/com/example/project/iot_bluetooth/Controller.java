package com.example.project.iot_bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

import java.util.Set;


public class Controller {
    private MainActivity mainActivity;
    private BluetoothAdapter bluetoothAdapter;

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initBlueTooth();
        if (bluetoothAdapter != null) {
            enableBlueTooth();
            searchForBlueToothDevices();
        }
    }

    private void initBlueTooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d("Controller", "Bluetooth is not supported");
        } else {
            Log.d("Controller", "Bluetooth is supported");
        }
    }

    private void enableBlueTooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBtIntent, 1);
        }
    }

    private void searchForBlueToothDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.d("Controller", "device name: " + device.getName());
                Log.d("Controller", "device address: " + device.getAddress());

                ConnectThread connectThread = new ConnectThread(device, bluetoothAdapter);
                connectThread.start();
            }
        }
    }


}
