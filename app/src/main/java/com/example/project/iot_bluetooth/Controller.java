package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
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

import static com.example.project.iot_bluetooth.Constants.REQUEST_ENABLE_BT;


public class Controller extends BroadcastReceiver {
    private MainActivity mainActivity;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;
    private Intent serviceIntent;
    private WekaClassifier classifier;
    private String[] liveData= new  String[180];
    private final int windowSize = 30;
    private final String topicName = "inTopic";

    private int counter=0;


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
       // String tess ="-119,-52,-876,4207,32865,-1951,-116,-113,-453,43811,-7744,-5213,-98,-115,-97,72073,-15813,-13841,-42,-136,57,65152,-19223,-15213,44,-129,206,52146,-13781,-9610,55,-64,468,25134,-19537,500,44,-15,363,-22378,5329,5646,51,-4,152,-69146,18195,13731,8,46,-14,-89219,28158,20012,-66,92,-234,-92548,19439,16354,-55,40,-388,-63109,24170,5305,-20,47,-305,-19634,2341,378,-8,74,-184,18158,-8000,-4390,11,70,-76,38402,-14561,-7597,11,42,24,44146,-8707,-6829,19,39,102,40097,-12878,-5817,24,15,119,25915,-4317,-4805,16,1,88,8829,-793,-1317,8,-6,33,-561,1902,12,3,-5,-2,-2390,2768,-37,0,-1,-13,-1914,1427,36,-4,-2,-1,-829,-293,622,-3,-1,-8,-390,134,85,5,-1,7,-49,-293,-415,-2,1,-1,-963,329,219,1,-4,-11,-647,317,48,-2,3,5,805,-427,85,-2,0,2,24,110,280,-2,0,-1,366,24,12,6,0,3,707,-244,-122";
     //   String tess ="-793,-25,-52,5243,16402,-9451,224,-121,152,12560,-6585,-25548,399,-218,-169,-22012,10243,-26951,-23,-78,-168,-38536,-9268,-6158,792,-315,198,3353,-40853,-90243,547,46,126,21463,-21036,7073,220,93,-215,5182,43475,76768,-319,-67,-41,25304,33475,93780,-473,-63,142,13963,-11646,65182,-537,-54,-9,2439,-3109,24573,-537,345,6,-24146,-7865,3536,-178,390,24,-14817,6341,-40792,147,20,-56,11219,12073,-38597,176,11,27,1402,-25609,-12621,124,2,-27,-2195,13536,-18475,164,23,38,4939,-5000,-9939,111,-33,-14,-5731,-2073,4451,32,0,14,-1707,-8353,-2987,-3,-26,19,1707,4756,3780,-86,53,-66,-4878,243,4451,22,-20,12,3292,1707,-1646,4,-7,5,-121,-2195,-304,10,-2,0,-1646,-6036,2621,-25,31,14,5914,9451,-3109,30,-6,30,-1768,-792,853,-36,11,-20,-1951,1707,2926,-15,5,-19,609,-365,-3292,40,-17,21,2378,1524,-2865,-41,19,16,-2987,-5975,2987,40,-12,-19,1158,4268,-2560";
     //   classifier.NewDataSet(tess.split(","));
      //  tess ="-793,-25,-52,5243,16402,-9451,-285,-73,50,8902,4909,-17500,-57,-121,-23,-1403,6687,-20650,-48,-111,-59,-10686,2698,-17027,120,-151,-8,-7878,-6012,-31670,388,-137,28,-4634,-13500,-28365,387,-94,-46,-6110,-3488,-7902,243,-64,-20,3353,1159,16244,153,-61,42,13853,683,30512,-112,-9,1,13670,8232,53475,-329,51,-23,4548,10866,52768,-409,110,24,549,3439,29256,-316,128,21,-2268,-841,2780,-186,142,-2,-4781,-3634,-12780,-54,154,-5,-5707,-305,-21390,87,89,1,110,268,-24085,144,5,-6,1927,-1415,-15036,121,1,8,-658,-5500,-7914,86,-7,6,-597,573,-4634,44,3,-2,-1134,-2085,-49,15,-5,-7,-1463,-744,1610,-6,0,-3,-341,-768,659,-11,0,-6,-329,-305,1780,-15,11,-7,512,634,403,8,-1,12,1134,427,-317,-3,5,6,86,427,597,-7,8,1,232,793,0,-1,5,5,1036,2305,-1097,-4,2,6,-744,-780,122,-2,1,-4,-159,232,-561";
      //  classifier.NewDataSet(tess.split(","));
      //  Toast toast = Toast.makeText(mainActivity.getApplicationContext(),classifier.NewDataSet(tess.split(",")),Toast.LENGTH_LONG);
      //  toast.show();
    }
    public void LiveDataSet(String stringArr){
        String[] newDataArr = stringArr.split(",");
        if (newDataArr.length >= 7 && newDataArr[0].equals("h")) { // we have 7 elements
        //    Log.i("NEW DATA","COUNTER "+counter);

                 liveData[6 * (counter)] = newDataArr[1];
                 liveData[6 * (counter) + 1] = newDataArr[2];
                 liveData[6 * (counter) + 2] =newDataArr[3];
                 liveData[6 * (counter) + 3] = newDataArr[4];
                 liveData[6 * (counter) + 4] = newDataArr[5];
                 liveData[6 * (counter) + 5] = newDataArr[6];

            counter++;
        }
        if (counter > windowSize-1){
            String gesture =classifier.NewDataSet(liveData);
            Toast toast = Toast.makeText(mainActivity.getApplicationContext(),gesture,Toast.LENGTH_SHORT);
            toast.show();
            counter=0;
            liveData= new  String[180];
            publishMqttMessage(topicName,gesture);
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
    public boolean publishMqttMessage(String topic, String message){
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




   /* ------------------------------------------------------------------------------------- */

}
