package com.example.project.iot_bluetooth;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Används för att trådarna ConnectThread och ConnectedThread ska kunna kommunicera med MainThread (UI thread).
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
                String bluetoothDevice = (String) msg.obj;
                Log.d("MyHandler", bluetoothDevice);
                controller.setDeviceName(bluetoothDevice);
                break;

            case ConnectedThread.WRISTBAND_DATA:
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;

                String receivedData = new String(writeBuf);
                receivedData = receivedData.substring(begin, end);
                Log.d("MyHandler", "message = " + receivedData);


                /* NAMRAS KOD
                String inputString = (String) msg.obj;
                Log.d("MyHandler", "message = " + inputString);*/
                break;
        }
    }

}
