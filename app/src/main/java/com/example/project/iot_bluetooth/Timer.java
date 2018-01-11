package com.example.project.iot_bluetooth;

import android.util.Log;

/**
 * Created by Sebastian Hoggard on 2017-12-30.
 */

public class Timer extends Thread {
    public static long timeout = 20000;        //too long?

    private Communication mother;
    private long lastActivity;

    /**
     * Base constructor.
     * @param mother    the subject of potential disconnect
     */
    public Timer(Communication mother) {
        this.mother = mother;
        this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Resets this timer.
     */
    public void reset() {
        lastActivity = System.currentTimeMillis();
    }

    /**
     * When this timer is no longer needed, this method will prevent it from causing confusion.
     */
    public void disable() {
        mother = null;
    }

    /**
     * Thread continuously sleeps for 1 second and then determines whether mother should disconnect.
     */
    @Override
    public void run() {
        while (System.currentTimeMillis() < lastActivity + timeout) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mother != null) {
            mother.disconnect();
            Log.v("Timer", "Timer disconnected communication");
        }
    }
}
