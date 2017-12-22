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
            searchAndConnectBTDevice();
        }
    }

    private void initBlueTooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            mainActivity.showText("Bluetooth is not supported.");
        }
    }

    private void enableBlueTooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBtIntent, 1);
        }
    }

    private void searchAndConnectBTDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                ConnectThread connectThread = new ConnectThread(device, bluetoothAdapter, new MyHandler(this));
                connectThread.start();
            }
        }
    }

    public void setDeviceName(String deviceName) {
        mainActivity.showConnectedDevice(deviceName);
    }
}
