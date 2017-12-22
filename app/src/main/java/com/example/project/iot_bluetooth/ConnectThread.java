package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private final BluetoothDevice btDevice;
    private final BluetoothAdapter btAdapter;
    private final BluetoothSocket btSocket;
    private MyHandler handler;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final int SET_DEVICE_NAME = 0;

    public ConnectThread(BluetoothDevice btDevice, BluetoothAdapter btAdapter, MyHandler handler) {
        this.btDevice = btDevice;
        this.btAdapter = btAdapter;
        this.handler = handler;
        BluetoothSocket tmp = null;
        try {
            tmp = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btSocket = tmp;
    }

    public void run() {
        btAdapter.cancelDiscovery();
        try {
            btSocket.connect();
            sendDeviceNameToUI();
        } catch (IOException connectException) {
            try {
                btSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
        ConnectedThread mConnectedThread = new ConnectedThread(btSocket, handler);
        mConnectedThread.start();
    }

    private void sendDeviceNameToUI() {
        Message message = new Message();
        message.what = SET_DEVICE_NAME;
        message.obj = btDevice.getName();
        handler.sendMessage(message);
    }

    public void cancel() throws IOException {
        btSocket.close();
    }

}
