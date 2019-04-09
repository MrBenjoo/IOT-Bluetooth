package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import static com.example.project.iot_bluetooth.Constants.PUBLISH_TOPIC;
import static com.example.project.iot_bluetooth.Constants.REQUEST_ENABLE_BT;


public class Controller extends BroadcastReceiver {
    private MainActivity mainActivity;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;
    private Intent serviceIntent;
    private WekaClassifier classifier;
    private String[] liveData = new String[180];
    private final int windowSize = 30;
    private boolean timer = false;
    private int counter = 0;


    public Controller(MainActivity mainActivity, boolean phoneRotation) {

        this.mainActivity = mainActivity;
        initBtAdapter();
        if (bluetoothAdapter != null) {
            enableBluetooth();
            connectPairedDevices();
            initBroadcastReceiver();
        }
        initMQTT(phoneRotation);
        this.classifier = new WekaClassifier(this, mainActivity);

    }
    private void ResetDataSet(){
        if(timer) {
            counter = 0;
            liveData = new String[180];
            timer=false;
            Log.i("***Thread","reset data set ");
        }
    }

    public void LiveDataSet(String stringArr) {

        if (!timer) {
            Thread timerThread = new Thread(new ResetData());
            timerThread.start();
            timer = true;
        }
        String[] newDataArr = stringArr.split(",");
        if (newDataArr.length >= 7 && newDataArr[0].equals("h")) { // we have 7 elements
            //    Log.i("NEW DATA","COUNTER "+counter);

            liveData[6 * (counter)] = newDataArr[1];
            liveData[6 * (counter) + 1] = newDataArr[2];
            liveData[6 * (counter) + 2] = newDataArr[3];
            liveData[6 * (counter) + 3] = newDataArr[4];
            liveData[6 * (counter) + 4] = newDataArr[5];
            liveData[6 * (counter) + 5] = newDataArr[6];
            counter++;
        }
        if (counter > windowSize - 1) {
            timer = false;
            String gesture = classifier.NewDataSet(liveData);
            Toast toast = Toast.makeText(mainActivity.getApplicationContext(), gesture, Toast.LENGTH_SHORT);
            toast.show();
            counter = 0;
            liveData = new String[180];
            publishMqttMessage(PUBLISH_TOPIC, gesture);
        }

    }

    /* control if the phone supports bluetooth */
    private void initBtAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            setBluetoothStatus("No bluetooth support");
        }
    }

    /* Shows a dialog that lets the user enable bluetooth */
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            mainActivity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            setBluetoothStatus("No device is connected");
        }
    }

    /* Creates a bluetooth connection with the paired devices */
    private void connectPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                connectToDevice(device);
            }
        }
    }

    /* register a BroadcastReceiver to listen for bluetooth changes.
      *     - CONNECT
      *     - DISCONNECT
      *     - BLUETOOTH ON
      *     - BLUETOOTH DISABLED
      *     - DISCOVERY FINISHED
      * */
    private void initBroadcastReceiver() {
        mainActivity.registerReceiver(this, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        mainActivity.registerReceiver(this, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        mainActivity.registerReceiver(this, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        mainActivity.registerReceiver(this, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    /* Connect to server and start service.
     * on phone rotation don't start a new service because the old one is already running */
    private void initMQTT(boolean phoneRotation) {
        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(mainActivity.getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        serviceIntent = new Intent(mainActivity, MqttMessageService.class);
        if (!phoneRotation) {
            mainActivity.startService(serviceIntent);
        }
    }

    /* BroadcastReceiver */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    onBTConnection(intent);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    onBTDisconnect();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    bluetoothStateChanged(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    onDiscoveryFinished();
            }
        }
    }

    /* connect to device if not connected and no other thread is already trying to establish a connection */
    private void onBTConnection(Intent intent) {
        if (!isConnected() && !tryingToConnect()) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            connectToDevice(device);
        }
    }

    private void onBTDisconnect() {
        try {
            if (connectThread != null) {
                connectThread.cancel();
            }
            setBluetoothStatus("No device is connected");
        } catch (IOException e) {
            e.printStackTrace();
            setBluetoothStatus("Error when disconnecting");
        }
    }

    private void bluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                setBluetoothStatus("No device is connected");
                break;
            case BluetoothAdapter.STATE_OFF:
                bluetoothAdapter.disable();
                setBluetoothStatus("Bluetooth disabled");
                break;
        }
    }

    private void onDiscoveryFinished() {
        if (!isConnected()) {
            connectPairedDevices();
        }
    }

    /*
    * When discovery is finished (after enabling bluetooth), onResume() and ACTION_DISCOVERY_FINISHED will be called.
    * They will both try to connect, to prevent it !tryingToConnect() is used.
    * */
    private void connectToDevice(BluetoothDevice device) {
        if (bluetoothAdapter != null && !tryingToConnect()) {
            connectThread = new ConnectThread(device, bluetoothAdapter, new MyHandler(this));
            connectThread.start();
        }
    }

    /* Trying to reconnect after pressing the home button and coming back */
    public void onResume() {
        if (!isConnected()) {
            connectPairedDevices();
        }
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

    /*
    * unregister BroadcastReceiver and
    * if the activity is being destroyed for good (onBackPressed) then disconnect the client from the server and stop the service.
    * */
    public void onDestroy() {
        mainActivity.unregisterReceiver(this);
        if (mainActivity.isFinishing()) {
            try {
                if (client != null && client.isConnected()) {
                    client.disconnect();
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mainActivity.stopService(serviceIntent);
        }
    }

    /* Called when a new server message is received*/
    public void onMessageArrived(final String message) {
        mainActivity.setMessage(message);
    }

    public void setBluetoothStatus(final String status) {
        mainActivity.setBluetoothStatus(status);
    }

    public void setMqttStatus(final String status) {
        mainActivity.setMqttStatus(status);
    }

    private boolean tryingToConnect() {
        return connectThread != null && connectThread.isAlive();
    }

    private boolean isConnected() {
        return connectThread != null && connectThread.isConnected();
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

    public boolean publishMqttMessage(String topic, String message) {
        try {
            pahoMqttClient.publishMessage(client, message, 1, topic);
            return true;
        } catch (Exception e) {
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


private class ResetData implements Runnable{

    @Override
    public void run() {
        Log.i("***Thread","start ");
        try {
            Thread.sleep(2800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            ResetDataSet();
            Log.i("***Thread","finish ");
        }

    }
}

   /* ------------------------------------------------------------------------------------- */

}
