package com.example.project.iot_bluetooth;

/**
 * Created by Yurdaer on 2018-01-02.
 */

public class PreProcessing {

    private final int oldAccMin = -2512;
    private final int oldAccMax = 2474;
    private final int oldGyroMin = -980061;
    private final int oldGyroMax = 100;
    private final int newMinimum = 0;
    private final int newMaximum = 200;

    public double[] NormalizaData(double[] arr) {
        double[] newArr = new double[arr.length];
        for (int i = 0; i < 30; i++) {
            newArr[6 * (i)] = ((arr[6 * (i)] - oldAccMin) / (oldAccMax - oldAccMin)) * (newMaximum - newMinimum) + newMinimum;
            newArr[6 * (i) + 1] = ((arr[6 * (i) + 1] - oldAccMin) / (oldAccMax - oldAccMin)) * (newMaximum - newMinimum) + newMinimum;
            newArr[6 * (i) + 2] = ((arr[6 * (i) + 2] - oldAccMin) / (oldAccMax - oldAccMin)) * (newMaximum - newMinimum) + newMinimum;
            newArr[6 * (i) + 3] = ((arr[6 * (i) + 3] - oldGyroMin) / (oldGyroMax - oldGyroMin)) * (newMaximum - newMinimum) + newMinimum;
            newArr[6 * (i) + 4] = ((arr[6 * (i) + 4] - oldGyroMin) / (oldGyroMax - oldGyroMin)) * (newMaximum - newMinimum) + newMinimum;
            newArr[6 * (i) + 5] = ((arr[6 * (i) + 5] - oldGyroMin) / (oldGyroMax - oldGyroMin)) * (newMaximum - newMinimum) + newMinimum;
        }
        return newArr;
    }

    public double[] MovingAverage(double[] arr) {
        double[] newArr = new double[arr.length];
        for (int i = 0; i < 30; i++) {

            if (i == 0) {
                newArr[6 * (i)] = arr[6 * (i)];
                newArr[6 * (i) + 1] = arr[6 * (i) + 1];
                newArr[6 * (i) + 2] = arr[6 * (i) + 2];
                newArr[6 * (i) + 3] = arr[6 * (i) + 3];
                newArr[6 * (i) + 4] = arr[6 * (i) + 4];
                newArr[6 * (i) + 5] = arr[6 * (i) + 5];
            }
            if (i == 1) {
                newArr[6 * (i)] = (arr[6 * (i)] + arr[6 * (i - 1)]) / 2;
                newArr[6 * (i) + 1] = (arr[6 * (i) + 1] + arr[6 * (i - 1) + 1]) / 2;
                newArr[6 * (i) + 2] = (arr[6 * (i) + 2] + arr[6 * (i - 1) + 2]) / 2;
                newArr[6 * (i) + 3] = (arr[6 * (i) + 3] + arr[6 * (i - 1) + 3]) / 2;
                newArr[6 * (i) + 4] = (arr[6 * (i) + 4] + arr[6 * (i - 1) + 4]) / 2;
                newArr[6 * (i) + 5] = (arr[6 * (i) + 5] + arr[6 * (i - 1) + 5]) / 2;
            }
            if (i == 2) {
                newArr[6 * (i)] = (arr[6 * (i)] + arr[6 * (i - 1)] + arr[6 * (i - 2)]) / 3;
                newArr[6 * (i) + 1] = (arr[6 * (i) + 1] + arr[6 * (i - 1) + 1] + arr[6 * (i - 2) + 1]) / 3;
                newArr[6 * (i) + 2] = (arr[6 * (i) + 2] + arr[6 * (i - 1) + 2] + arr[6 * (i - 2) + 2]) / 3;
                newArr[6 * (i) + 3] = (arr[6 * (i) + 3] + arr[6 * (i - 1) + 3] + arr[6 * (i - 2) + 3]) / 3;
                newArr[6 * (i) + 4] = (arr[6 * (i) + 4] + arr[6 * (i - 1) + 4] + arr[6 * (i - 2) + 4]) / 3;
                newArr[6 * (i) + 5] = (arr[6 * (i) + 5] + arr[6 * (i - 1) + 5] + arr[6 * (i - 2) + 5]) / 3;
            }
            if (i == 3) {
                newArr[6 * (i)] = (arr[6 * (i)] + arr[6 * (i - 1)] + arr[6 * (i - 2)] + arr[6 * (i - 3)]) / 4;
                newArr[6 * (i) + 1] = (arr[6 * (i) + 1] + arr[6 * (i - 1) + 1] + arr[6 * (i - 2) + 1] + arr[6 * (i - 3) + 1]) / 4;
                newArr[6 * (i) + 2] = (arr[6 * (i) + 2] + arr[6 * (i - 1) + 2] + arr[6 * (i - 2) + 2] + arr[6 * (i - 3) + 2]) / 4;
                newArr[6 * (i) + 3] = (arr[6 * (i) + 3] + arr[6 * (i - 1) + 3] + arr[6 * (i - 2) + 3] + arr[6 * (i - 3) + 3]) / 4;
                newArr[6 * (i) + 4] = (arr[6 * (i) + 4] + arr[6 * (i - 1) + 4] + arr[6 * (i - 2) + 4] + arr[6 * (i - 3) + 4]) / 4;
                newArr[6 * (i) + 5] = (arr[6 * (i) + 5] + arr[6 * (i - 1) + 5] + arr[6 * (i - 2) + 5] + arr[6 * (i - 3) + 5]) / 4;
            }
            if (i >= 4) {
                newArr[6 * (i)] = (arr[6 * (i)] + arr[6 * (i - 1)] + arr[6 * (i - 2)] + arr[6 * (i - 3)] + arr[6 * (i - 4)]) / 5;
                newArr[6 * (i) + 1] = (arr[6 * (i) + 1] + arr[6 * (i - 1) + 1] + arr[6 * (i - 2) + 1] + arr[6 * (i - 3) + 1] + arr[6 * (i - 4) + 1]) / 5;
                newArr[6 * (i) + 2] = (arr[6 * (i) + 2] + arr[6 * (i - 1) + 2] + arr[6 * (i - 2) + 2] + arr[6 * (i - 3) + 2] + arr[6 * (i - 4) + 2]) / 5;
                newArr[6 * (i) + 3] = (arr[6 * (i) + 3] + arr[6 * (i - 1) + 3] + arr[6 * (i - 2) + 3] + arr[6 * (i - 3) + 3] + arr[6 * (i - 4) + 3]) / 5;
                newArr[6 * (i) + 4] = (arr[6 * (i) + 4] + arr[6 * (i - 1) + 4] + arr[6 * (i - 2) + 4] + arr[6 * (i - 3) + 4] + arr[6 * (i - 4) + 4]) / 5;
                newArr[6 * (i) + 5] = (arr[6 * (i) + 5] + arr[6 * (i - 1) + 5] + arr[6 * (i - 2) + 5] + arr[6 * (i - 3) + 5] + arr[6 * (i - 4) + 5]) / 5;
            }

        }


        return newArr;
    }
}
