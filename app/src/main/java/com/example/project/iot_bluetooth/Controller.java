package com.example.project.iot_bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


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
    public static final int REQUEST_ENABLE_BT = 1;

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initBluetooth();
        if (bluetoothAdapter != null) {
            enableBluetooth();
            searchAndConnectBTDevice();
        }
        initBroadcastReceiver();
        initMQTTService();
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            mainActivity.showText("Bluetooth is not supported.");
        }
    }

    /* Shows a dialog to let the user enable bluetooth */
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void searchAndConnectBTDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                connectToDevice(device);
            }
        }
    }

    /* Initializes a broadcast listener to listen for bluetooth changes
      *     - BLUETOOTH DISABLED
      *     - BLUETOOTH ON
      *     - CONNECT
      *     - DISCONNECT
      * */
    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
    }

    private void initMQTTService() {
        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(mainActivity.getApplicationContext(), MqttConstants.MQTT_BROKER_URL, MqttConstants.CLIENT_ID);
        Intent intent = new Intent(mainActivity, MqttMessageService.class);
        mainActivity.startService(intent);
        TestThread tt = new TestThread(this);
        tt.start();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        connectToDevice(device);
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        onDisconnect();
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        bluetoothStateChanged(state);
                        break;
                }
            }

        }
    };

    private void bluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                setBluetoothStatus("Connect to a device.");
                searchAndConnectBTDevice();
                break;
            case BluetoothAdapter.STATE_OFF:
                setBluetoothStatus("Bluetooth disabled.");
                break;
        }
    }

    private void connectToDevice(BluetoothDevice device) {
        if (bluetoothAdapter != null) {
            connectThread = new ConnectThread(device, bluetoothAdapter, new MyHandler(this));
            connectThread.start();
        }
    }

    private void onDisconnect() {
        try {
            connectThread.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBluetoothStatus("Connect to a device.");
    }


    public void setBluetoothStatus(String status) {
        mainActivity.setTextBluetooth(status);
    }

    public void setGesture(String gesture) {
        mainActivity.setGesture(gesture);
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

    /* Shutdown the connection */
    public void onPause() {
        try {
            if (connectThread != null) {
                connectThread.cancel();
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void onDestroy() {
        mainActivity.unregisterReceiver(broadcastReceiver);
    }


}
