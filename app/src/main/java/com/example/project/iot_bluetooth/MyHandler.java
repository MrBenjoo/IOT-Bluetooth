package com.example.project.iot_bluetooth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Used for communication between the UI thread and other threads running in the background.
 */
public class MyHandler extends Handler {
    private Controller controller;

    public MyHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {

            case Constants.CONNECTION_SUCCESS:
                controller.setBluetoothStatus("connected to " + msg.obj);
                break;

            case Constants.WRISTBAND_DATA:
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;
                String sensorValues = new String(writeBuf);
                sensorValues = sensorValues.substring(begin, end);
                controller.LiveDataSet(sensorValues);
                Log.i("MyHandler", "message = " + sensorValues);
                break;

            case Constants.CONNECTION_FAILED:
                controller.setBluetoothStatus("No connection to " + msg.obj);
                break;
        }
    }
}
