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
    private Intent serviceIntent;
    public static final int REQUEST_ENABLE_BT = 1;

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initBluetooth();
        if (bluetoothAdapter != null) {
            enableBluetooth();
            connectPairedDevices();
            initBroadcastReceiver();
        }
        initMQTT();
        /*Thread tt = new Thread(new TestClass(this));
        tt.start();*/
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            setBluetoothStatus("No bluetooth support.");
        }
    }

    /* Shows a dialog that lets the user enable bluetooth if it's disabled */
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            setBluetoothStatus("No device is connected");
        }
    }

    /* Creates a bluetooth connection with the bonded devices */
    private void connectPairedDevices() {
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
      *     - DISCOVERY FINISHED
      * */
    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mainActivity.registerReceiver(broadcastReceiver, filter);
    }

    private void initMQTT() {
        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(mainActivity.getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        serviceIntent = new Intent(mainActivity, MqttMessageService.class);
        mainActivity.startService(serviceIntent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        if (!isConnected() && !tryingToConnect()) { // no connection and no other running instance trying to establish a connection
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            connectToDevice(device);
                            break;
                        }
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        onDisconnect();
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        bluetoothStateChanged(state);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (!isConnected()) {
                            connectPairedDevices();
                        }
                }
            }
        }
    };

    private boolean tryingToConnect() {
        return connectThread != null && connectThread.isAlive();
    }

    private boolean isConnected() {
        return connectThread != null && connectThread.isConnected();
    }

    private void bluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                setBluetoothStatus("No device connected...");
                break;
            case BluetoothAdapter.STATE_OFF:
                setBluetoothStatus("Bluetooth disabled");
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
            if (connectThread != null) {
                connectThread.cancel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBluetoothStatus("No device connected...");
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
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mainActivity.stopService(serviceIntent);
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


    /**
     * -------------------------------------------------------------------------------------
     * TESTER FÖR MQTT
     * -------------------------------------------------------------------------------------
     */
    public boolean testSubscribe_topicTest() {
        try {
            pahoMqttClient.subscribe(client, "test", 1);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean testPublishMessage_ThisIsATest() {
        try {
            pahoMqttClient.publishMessage(client, "This a test", 1, "test");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean testUnsubscribeFrom_topicTest() {
        try {
            pahoMqttClient.unSubscribe(client, "test");
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean testDisconnectFromServer() {
        try {
            pahoMqttClient.disconnect(client);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }
   /* ------------------------------------------------------------------------------------- */

}
