package com.example.project.iot_bluetooth;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by Sebastian Hoggard on 2017-12-30.
 */

public class Communication {
    private static String topicInitiate = "INITIATE";
    private static String myIdentity = "någotkul";
    private static String messageDisconnect = "DISCONNECT";

    public static String gestures[] = new String[]{"UP", "DOWN", "RIGHT", "LEFT", "CW", "CCW"};
    public enum Protocol {SEBASTIAN, YURDAER};

    private Controller controller;
    private Protocol protocol;
    private String myTopic = null;          // for incoming messages
    private String deviceTopic = null;      // for outgoing messages
    private String tempTopic = null;
    private boolean fConnected = false;      // Arduino device has initiated connection
    private boolean fPaired = false;         // connection to Arduino device is confirmed
    private Timer timer = null;
    private Random random = new Random();

    private PahoMqttClient listener = null;
    private MqttAndroidClient client = null;

    private String lastGesture = null;

    /**
     * Base constructor.
     * @param controller
     * @param protocol      which protocol to communicate with Arduino device over
     * @param listener      from controller
     * @param client        from controller
     */
    public Communication(Controller controller, Protocol protocol, PahoMqttClient listener, MqttAndroidClient client) {
        this.controller = controller;
        this.protocol = protocol;
        this.listener = listener;
        this.client = client;
        try {
            listener.subscribe(client, topicInitiate, 1);
            switch (protocol) {
                case SEBASTIAN:
                    this.myTopic = String.valueOf(random.nextLong());
                    this.myTopic = "basicTopic";            //topicTest
                    break;
                case YURDAER:
                    this.myTopic = myIdentity;
                    break;
            }
            listener.subscribe(client, myTopic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        this.timer = new Timer(this);
        Log.v("Communication", "Created with " + protocol.toString());
    }

    /**
     * Handles incoming messages, including for pairing.
     * @param topic     from MqttMessageService
     * @param message   from MqttMessageService
     */
    public void messageArrived(String topic, String message) {      // anropas från MqttCallbackExtended
        Log.v("Communication", "Message: " + topic + ", " + message);
        if (topic.equals(topicInitiate)) {
            // perform handshake if not already fConnected
            if (!fConnected) {
                switch (protocol) {
                    case SEBASTIAN:
                        tempTopic = message;
                        // generate new incoming topic and re-subscribe
                        try {
                            listener.unSubscribe(client, myTopic);
                            myTopic = String.valueOf(random.nextLong());
                            listener.subscribe(client, myTopic, 1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    case YURDAER:
                        deviceTopic = message;
                        break;
                }
                try {
                    if (!topic.split("#")[1].equals(lastGesture)) {
                        return;
                    }
                    message = message.split("#")[0];
                    listener.publishMessage(client, myTopic, 1, message);
                    Log.v("Communication", "Handshake: " + message + ", " + myTopic);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                fConnected = true;
                timer = new Timer(this);
                timer.start();
                controller.setMqttStatus("Initiating");
            }
        }
        else if (topic.equals(myTopic)) {
            if (fConnected && !fPaired) {
                // finish handshake
                switch (protocol) {
                    case SEBASTIAN:
                        tempTopic = null;
                        deviceTopic = message;
                }
                fPaired = true;
                controller.setMqttStatus("Paired");
            }
            if (message.equals(messageDisconnect)) {
                disconnect();
                controller.setMqttStatus("Disconnected");
                // timer will be disabled, and then reset :)
            }
            //controller.setMqttStatus(message);        // too much printing
            timer.reset();
        }
    }

    /**
     * Sends one of 6 possible gestures in String format to a fConnected device, if there is one.
     * @param nGesture  0-5, else it is ignored
     */
    public void sendGesture(int nGesture) {
        lastGesture = gestures[nGesture];
        if (nGesture < 0 || nGesture > 5) {
            Log.v("Communication", "Incorrect gesture: " + nGesture);
            return;
        }
        Log.v("Communication", "Gesture: " + nGesture);
        if (fPaired) {   //device != null
            try {
                listener.publishMessage(client, gestures[nGesture], 1, deviceTopic);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends any message to any topic, or the connected device. Good for troubleshooting.
     * @param topic     any topic, or null to use topic of fConnected device
     * @param message   any message
     */
    public void sendMessage(String topic, String message) {
        if (topic == null) {
            if (fPaired) {
                topic = deviceTopic;
            }
            else {
                Log.v("Communicate", "Tried to send message, but has no topic...");
                return;
            }
        }
        Log.v("Communication", "Message: " + topic + ", " + message);
        try {
            listener.publishMessage(client, message, 1, topic);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Breaks connection of any stage with Arduino device.
     */
    void disconnect() {
        if (fConnected) {
            Log.v("Communication", "Disconnect");
            try {
                listener.unSubscribe(client, deviceTopic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            fConnected = false;
            fPaired = false;
            timer.disable();
            //controller.setMqttStatus("Disconnected");     // suddenly, this method crashes
        }
    }
}
