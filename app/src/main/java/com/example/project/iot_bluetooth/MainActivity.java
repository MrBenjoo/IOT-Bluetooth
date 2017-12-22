package com.example.project.iot_bluetooth;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        new Controller(this);
    }

    private void initComponents() {
        tvStatus = findViewById(R.id.activity_tv_status);
    }

    public void showConnectedDevice(String name) {
        tvStatus.setText("Connected to " + name);
    }

    public void showText(String text) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
    }

}
