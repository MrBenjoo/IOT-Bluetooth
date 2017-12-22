package com.example.project.iot_bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttMessageService extends Service {
    private static final String TAG = "MqttMessageService";

    public MqttMessageService() { /* Empty constructor */ }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        PahoMqttClient pahoMqttClient = new PahoMqttClient();
        MqttAndroidClient mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), MqttConstants.MQTT_BROKER_URL, MqttConstants.CLIENT_ID);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.d(TAG, "connectComplete");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.d(TAG, "connectionLost");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.d("messageArrived", "s = " + s + " mqttMessage = " + mqttMessage.toString());
                MainActivity.controller.setGesture(mqttMessage.toString());
                //setMessageNotification(s, new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d(TAG, "deliveryComplete");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
