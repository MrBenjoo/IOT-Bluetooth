package com.example.project.iot_bluetooth;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView tvStatus;
    private TextView tvMqttStatus;
    private TextView tvGesture;
    static Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        controller = new Controller(this);
    }

    private void initComponents() {
        tvStatus = findViewById(R.id.activity_tv_status);
        tvMqttStatus  = findViewById(R.id.activity_tv_mqtt_status);
        tvGesture  = findViewById(R.id.activity_tv_gesture);
    }

    public void showConnectedDevice(String name) {
        String connection = "Connected to " + name;
        tvStatus.setText(connection);
    }

    public void showText(String text) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause();
    }

    public void setText(String online) {
        tvMqttStatus.setText(online);
    }

    public void setGesture(String gesture) {
        tvGesture.setText(gesture);
    }
}
