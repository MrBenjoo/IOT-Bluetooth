package com.example.project.iot_bluetooth;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Used for 'ConnectThread' and 'ConnectedThread' threads to be able communicate with the main thread (UI thread).
 */
public class MyHandler extends Handler {
    private Controller controller;

    public MyHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {

            case ConnectThread.SET_DEVICE_NAME:
                String deviceName = (String) msg.obj;
                String connection = "connected to " + deviceName;
                controller.setBluetoothStatus(connection);
                break;

            case ConnectedThread.WRISTBAND_DATA:
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;
                String receivedData = new String(writeBuf);
                receivedData = receivedData.substring(begin, end);
                Log.d("MyHandler", "message = " + receivedData);
                break;
        }
    }

}
