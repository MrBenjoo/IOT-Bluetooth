package com.example.project.iot_bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttMessageService extends Service implements MqttCallbackExtended {
    private static final String TAG = "MqttMessageService";

    public MqttMessageService() { /* Empty constructor */ }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PahoMqttClient pahoMqttClient = new PahoMqttClient();
        MqttAndroidClient mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        mqttAndroidClient.setCallback(this);
    }

    @Override
    public void connectComplete(boolean b, String s) {
        MainActivity.controller.setMqttStatus("Connected to MQTT");
        Log.d(TAG, "connectComplete s :" + s);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        MainActivity.controller.setMqttStatus("Connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        MainActivity.controller.onMessageArrived(message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(TAG, "deliveryComplete");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
