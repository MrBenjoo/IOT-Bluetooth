package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private BluetoothDevice btDevice;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private ConnectedThread mConnectedThread;
    private MyHandler handler;
    private boolean socketError = false;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public ConnectThread(BluetoothDevice btDevice, BluetoothAdapter btAdapter, MyHandler handler) {
        this.btDevice = btDevice;
        this.btAdapter = btAdapter;
        this.handler = handler;
        btSocket = createSocket();
    }

    private BluetoothSocket createSocket() {
        BluetoothSocket socket = null;
        try {
            socket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            socketError = true;
        }
        return socket;
    }

    public void run() {
        Log.d("connectThread", "connecting to: " + btDevice.getName() + " address: " + btDevice.getAddress() + " bonded: " + btDevice.getBondState());
        btAdapter.cancelDiscovery();
        try {
            btSocket.connect();
            onSuccessConnection();
        } catch (IOException connectException) {
            try {
                btSocket = (BluetoothSocket) btDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDevice, 1);
                btSocket.connect();
                onSuccessConnection();
            } catch (Exception exception) {
                socketError = true;
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.obtainMessage(Constants.CONNECTION_FAILED, -1, -1, btDevice.getName()).sendToTarget();
                exception.printStackTrace();
            }
        }

        if (!socketError) {
            Log.d("connectThread", "ConnectedThread started...");
            mConnectedThread = new ConnectedThread(btSocket, handler);
            mConnectedThread.start();
        } else {
            Log.d("connectThread", "socketError occurred");
        }
    }

    private void onSuccessConnection() {
        Message message = new Message();
        message.what = Constants.CONNECTION_SUCCESS;
        message.obj = btDevice.getName();
        handler.sendMessage(message);
    }

    public boolean isConnected() {
        return (btSocket != null && btSocket.isConnected());
    }

    public void cancel() throws IOException {
        btSocket.close();
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
        }
    }

}
