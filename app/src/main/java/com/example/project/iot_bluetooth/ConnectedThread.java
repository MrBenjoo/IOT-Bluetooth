package com.example.project.iot_bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;


public class ConnectedThread extends Thread {
    private final BluetoothSocket btSocket;
    private final InputStream input;
    private MyHandler handler;
    public static final int WRISTBAND_DATA = 1;

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
        while (true) {
            try {
                bytes += input.read(buffer, bytes, buffer.length - bytes);
                for (int i = begin; i < bytes; i++) {
                    if (i == 20) {
                        handler.obtainMessage(WRISTBAND_DATA, begin, i, buffer).sendToTarget();
                        begin = i;
                        if (i == bytes - 1) {
                            bytes = 0;
                            begin = 0;
                        }
                    }
                }

                // KOPIERAT KOD FRÅN EXEMPLET
                /*bytes += input.read(buffer, bytes, buffer.length - bytes);
                for (int i = begin; i < bytes; i++) {

                    if (buffer[i] == "#".getBytes()[0]) {
                        handler.obtainMessage(1, begin, i, buffer).sendToTarget();
                        begin = i + 1;
                        if (i == bytes - 1) {
                            bytes = 0;
                            begin = 0;
                        }
                    }
                }*/

            } catch (IOException e) {
                break;
            }
        }
    }


    public void cancel() throws IOException {
        btSocket.close();
    }
}
