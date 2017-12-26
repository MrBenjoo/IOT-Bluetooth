package com.example.project.iot_bluetooth;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.project.iot_bluetooth.Controller.REQUEST_ENABLE_BT;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvStatus, tvMqttStatus, tvGesture;
    private Button btnUnsub, btnSub, btnPub; // Används endast för test
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
        tvMqttStatus = findViewById(R.id.activity_tv_mqtt_status);
        tvGesture = findViewById(R.id.activity_tv_gesture);

        /* ANVÄNDS ENDAST FÖR TEST */
        btnUnsub = findViewById(R.id.btn_unsubscribe);
        btnSub = findViewById(R.id.btn_subscribe);
        btnPub = findViewById(R.id.btn_publish);
        btnUnsub.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnPub.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) { // The user chose to enable Bluetooth in the dialog
                setTextBluetooth("No device connected...");
            } else {
                setTextBluetooth("Bluetooth disabled.");
            }
        }
    }

    public void setTextBluetooth(String btStatus) {
        tvStatus.setText(btStatus);
    }

    public void setText(String mqttStatus) {
        tvMqttStatus.setText(mqttStatus);
    }

    public void setGesture(String gesture) {
        tvGesture.setText(gesture);
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_subscribe:
                controller.testSubscribe_topicTest();
                break;
            case R.id.btn_unsubscribe:
                controller.testUnsubscribeFrom_topicTest();
                break;
            case R.id.btn_publish:
                controller.testPublishMessage_ThisIsATest();
                break;
        }
    }
}
