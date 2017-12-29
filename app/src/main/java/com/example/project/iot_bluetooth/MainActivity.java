package com.example.project.iot_bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.project.iot_bluetooth.Constants.REQUEST_ENABLE_BT;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvBtStatus, tvMqttStatus, tvMqttMsg;
    static Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initController(savedInstanceState);
    }

    private void initComponents() {
        tvBtStatus = findViewById(R.id.activity_tv_bt_status);
        tvMqttStatus = findViewById(R.id.activity_tv_mqtt_status);
        tvMqttMsg = findViewById(R.id.activity_tv_mqtt_msg);

        /* ANVÄNDS ENDAST FÖR TEST */
        Button btnUnsub = findViewById(R.id.btn_unsubscribe);
        Button btnSub = findViewById(R.id.btn_subscribe);
        Button btnPub = findViewById(R.id.btn_publish);
        btnUnsub.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnPub.setOnClickListener(this);
    }

    private void initController(Bundle savedInstanceState) {
        boolean phoneRotation = false;
        if(savedInstanceState != null) {
            phoneRotation = true;
        }
        controller = new Controller(this, phoneRotation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                setBluetoothStatus("No device is connected");
            } else {
                setBluetoothStatus("Bluetooth is disabled");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!tvMqttStatus.getText().toString().isEmpty()) {
            outState.putString("tvMQTT", tvMqttStatus.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!= null) {
            tvMqttStatus.setText(savedInstanceState.getString("tvMQTT"));
        }
    }

    public void setBluetoothStatus(String btStatus) {
        tvBtStatus.setText(btStatus);
    }

    public void setMqttStatus(String mqttStatus) {
        tvMqttStatus.setText(mqttStatus);
    }

    public void setMessage(String message) {
        tvMqttMsg.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume();
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
