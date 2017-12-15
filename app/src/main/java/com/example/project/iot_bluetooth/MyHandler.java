package com.example.project.iot_bluetooth;


import android.os.Handler;
import android.os.Message;



public class MyHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        byte[] writeBuf = (byte[]) msg.obj;
        int begin = (int)msg.arg1;
        int end = (int)msg.arg2;

        switch(msg.what) {
            case 1:
                String writeMessage = new String(writeBuf);
                writeMessage = writeMessage.substring(begin, end);
                break;
        }
    }

}
