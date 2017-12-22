package com.example.project.iot_bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

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
        initMQTTService();
    }

    private void initMQTTService() {
        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(mainActivity.getApplicationContext(), MqttConstants.MQTT_BROKER_URL, MqttConstants.CLIENT_ID);
        Intent intent = new Intent(mainActivity, MqttMessageService.class);
        mainActivity.startService(intent);
        TestThread tt = new TestThread(this);
        tt.start();
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
        try {
            connectThread.cancel();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }


    public void addText(final String text) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setText(text);
            }
        });
    }

    public void addTextTest() throws MqttException {
        pahoMqttClient.subscribe(client, "test", 1);
        addText("test subscribe");
    }

    public void setGesture(String gesture) {
        mainActivity.setGesture(gesture);
    }
}
