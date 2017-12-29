package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/* used to create a bluetooth connection */
public class ConnectThread extends Thread {
    private BluetoothDevice btDevice;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private ConnectedThread mConnectedThread;
    private MyHandler handler;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public ConnectThread(BluetoothDevice btDevice, BluetoothAdapter btAdapter, MyHandler handler) {
        this.btDevice = btDevice;
        this.btAdapter = btAdapter;
        this.handler = handler;
        this.btSocket = createSocket();
    }

    private BluetoothSocket createSocket() {
        BluetoothSocket socket = null;
        try {
            socket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    /*
    * Tries to create a bluetooth connection.
    * ConnectedThread will be started if a successful connection is established.
    */
    public void run() {
        Log.d("connectThread", "connecting to: " + btDevice.getName() + " address: " + btDevice.getAddress() + " bonded: " + btDevice.getBondState());
        btAdapter.cancelDiscovery();
        try {
            Log.d("connectThread", "connecting...");
            btSocket.connect();
            Log.d("connectThread", "connected");
            onSuccessConnection();
            startConnectedThread();
        } catch (IOException connectException) {
            try {
                btSocket = (BluetoothSocket) btDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDevice, 1);
                btSocket.connect();
                onSuccessConnection();
                startConnectedThread();
            } catch (Exception exception) {
                try {
                    exception.printStackTrace();
                    handler.obtainMessage(Constants.CONNECTION_FAILED, -1, -1, btDevice.getName()).sendToTarget();
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* show which device we are connected to in the ui */
    private void onSuccessConnection() {
        Message message = new Message();
        message.what = Constants.CONNECTION_SUCCESS;
        message.obj = btDevice.getName();
        handler.sendMessage(message);
    }

    /* start ConnectedThread to read the sensor values from the wristband */
    private void startConnectedThread() {
        Log.d("connectThread", "ConnectedThread started...");
        mConnectedThread = new ConnectedThread(btSocket, handler);
        mConnectedThread.start();
    }

    public boolean isConnected() {
        return (btSocket != null && btSocket.isConnected());
    }

    public void cancel() throws IOException {
        if (mConnectedThread != null) {
            Log.d("connectThread", "closing sockets");
            btSocket.close();
            mConnectedThread.cancel();
        }
    }

}
