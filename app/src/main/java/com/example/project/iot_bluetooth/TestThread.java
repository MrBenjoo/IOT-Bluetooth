package com.example.project.iot_bluetooth;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by Benji on 2017-12-22.
 */

public class TestThread extends Thread {
    Controller controller;

    TestThread(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            sleep(5000);
        } catch (InterruptedException e) {

        }
        try {
            controller.addTextTest();
        } catch (MqttException t) {

        }

    }
}
