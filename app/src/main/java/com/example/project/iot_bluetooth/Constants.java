package com.example.project.iot_bluetooth;

public class Constants {

    /* ----------- Constants for MQTT ----------- */
    public static final String MQTT_BROKER_URL = "tcp://grupp15app:grupp15app@m14.cloudmqtt.com:10099"; // tcp://user:pass@host:port

    public static final String PUBLISH_TOPIC = "IoT/G16";

    public static final String CLIENT_ID = "com.example.benji2";

    /* ----------- Constants for MyHandler ----------- */
    public static final int CONNECTION_SUCCESS = 0;

    public static final int WRISTBAND_DATA = 1;

    public static final int CONNECTION_FAILED = 2;

    /* ----------- Constant for onActivityResult() ----------- */
    public static final int REQUEST_ENABLE_BT = 1;

}
