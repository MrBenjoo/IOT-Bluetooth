package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;


public class ConnectedThread extends Thread {
    private BluetoothSocket btSocket;
    private InputStream input;
    private MyHandler handler;
    private boolean reading = true;

    public ConnectedThread(BluetoothSocket btSocket, MyHandler handler) {
        this.btSocket = btSocket;
        this.handler = handler;
        InputStream tmpIn = null;
        try {
            tmpIn = btSocket.getInputStream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        input = tmpIn;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int begin = 0;
        int bytes = 0;
        while (reading) {
            try {
                bytes += input.read(buffer, bytes, buffer.length - bytes);
                for (int i = begin; i < bytes; i++) {
                    if (buffer[i] == '\n') {
                        handler.obtainMessage(Constants.WRISTBAND_DATA, begin, i, buffer).sendToTarget();
                        begin = i + 1;
                        if (i == bytes - 1) {
                            bytes = 0;
                            begin = 0;
                        }
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    public void cancel() throws IOException {
        reading = false;
        input.close();
        btSocket.close();
    }
}
