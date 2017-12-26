package com.example.project.iot_bluetooth;


import android.util.Log;

/**
 * Kan tas bort sen. Användes för att testa MQTT.
 */
public class TestClass implements Runnable {
    private Controller controller;

    public TestClass(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        goSleep(2000);
        testSubscribe_topicTest();
        testPublishMessage_ThisIsATest();
        goSleep(5000);
        testUnsubscribeFrom_topicTest();
        testDisconnectFromServer();
    }

    private void testDisconnectFromServer() {
        boolean disconnected = controller.testDisconnectFromServer();
        if(disconnected) {
            Log.d("TestClass", "TEST: testDisconnectFromServer() " + " PASSED: true");
        } else {
            Log.d("TestClass", "TEST: testDisconnectFromServer() " + " PASSED: false");
        }
    }

    private void testUnsubscribeFrom_topicTest() {
        boolean unsubscribed = controller.testUnsubscribeFrom_topicTest();
        if(unsubscribed) {
            Log.d("TestClass", "TEST: testUnsubscribeFrom_topicTest() " + " PASSED: true");
        } else {
            Log.d("TestClass", "TEST: testUnsubscribeFrom_topicTest() " + " PASSED: false");
        }
    }

    private void testPublishMessage_ThisIsATest() {
        boolean published = controller.testPublishMessage_ThisIsATest();
        if(published) {
            Log.d("TestClass", "TEST: testPublishMessage_ThisIsATest() " + " PASSED: true");
        } else {
            Log.d("TestClass", "TEST: testPublishMessage_ThisIsATest() " + " PASSED: false");
        }
    }

    private void testSubscribe_topicTest() {
        boolean subscribed = controller.testSubscribe_topicTest();
        if(subscribed) {
            Log.d("TestClass", "TEST: testSubscribe_topicTest() " + " PASSED: true");
        } else {
            Log.d("TestClass", "TEST: testSubscribe_topicTest() " + " PASSED: false");
        }
    }

    private void goSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
