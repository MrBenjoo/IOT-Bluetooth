package com.example.project.iot_bluetooth;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;


import org.eclipse.paho.android.service.MqttAndroidClient;

import java.io.IOException;
import java.util.Set;


public class Controller {
    private MainActivity mainActivity;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initBlueTooth();
        if (bluetoothAdapter != null) {
            enableBlueTooth();
            searchAndConnectBTDevice();
        }
        initMqttCloud();
    }

    private void initMqttCloud() {
        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(mainActivity.getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        Intent intent = new Intent(mainActivity, MqttMessageService.class);
        mainActivity.startService(intent);
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
                connectThread = new ConnectThread(device, bluetoothAdapter, new MyHandler(this));
                connectThread.start();
            }
        }
    }

    public void setDeviceName(String deviceName) {
        mainActivity.showConnectedDevice(deviceName);
    }

    public void onPause() {
        //Don't leave Bluetooth sockets open when leaving activity
        try {
            connectThread.cancel();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void onResume() {
       // TODO
    }

    public void addText(String text) {
        mainActivity.setText(text);
    }
}
