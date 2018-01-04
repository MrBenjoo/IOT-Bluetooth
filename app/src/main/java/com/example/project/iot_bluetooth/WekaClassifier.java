package com.example.project.iot_bluetooth;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import weka.classifiers.trees.*;
import weka.core.*;

/**
 * Created by Yurdaer on 2017-12-31.
 */

public class WekaClassifier {
    private Controller controller;
    private J48 classifier;
    private double[] testData = new double[181];
    private Instances data;
    private Instances test;
    private BufferedReader reader;
    private MainActivity mainActivity;
    private PreProcessing preProcessing;
    private String trainFileName = "train_data_smooth.arff";


    public WekaClassifier(Controller controller, MainActivity mainActivity) {
        this.controller = controller;
        this.mainActivity = mainActivity;
        preProcessing = new PreProcessing();
        try {
            reader = new BufferedReader(new InputStreamReader((mainActivity.getAssets().open(trainFileName))));
            Log.i("BUFFERED READER", "BUFFERED READER IS READY");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("BUFFERED READER", "ERROR WITH READ TRAIN FILE");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("BUFFERED READER", "ERROR WITH FIND THE PATH OF TRAIN FILE");
        }
        try {
            data = new Instances(reader);

            Log.i("INSTANCES", "TRAIN INSTANCES CREATED");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("INSTANCES", "ERROR WITH CREAT INSTANCES");

        }
        data.setClassIndex(data.numAttributes() - 1);
        classifier = new J48();
        try {
            classifier.buildClassifier(data);
            Log.i("CLASSIFIER", "WEKA CLASSIFIER IS READY");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CLASSIFIER", "ERROR WITH THE BUILD CLASSIFIER");

        }
        test = new Instances(data);
        test.setClassIndex(data.numAttributes() - 1);
        Log.i("DATA INSTANCES", Integer.toString(data.numInstances()));
        Log.i("DATA ATTRIBUTES", Integer.toString(data.numAttributes()));
        Log.i("TEST INSTANCES", Integer.toString(test.numInstances()));
        Log.i("TEST ATTRIBUTES", Integer.toString(test.numAttributes()));
        test.clear();
    }



    public String NewDataSet(String newData[]) {
        Log.i("LIVE DATA",Arrays.toString(newData));
        Log.i("LIVE DATA",Integer.toString(newData.length));
        for (int i = 0; i < newData.length; i++) {
            testData[i] = Integer.parseInt(newData[i]);
        }
        testData=preProcessing.MovingAverage(testData);
      //  testData=preProcessing.NormalizaData(testData);
      //  Log.i("Moving avarage", Arrays.toString(testData));
        test.add(new DenseInstance(1.0, testData));
        int classIndex = test.numAttributes() - 1;
        int numOfDistances = test.numInstances() - 1;
        double clsLabel = 0;
        try {
            clsLabel = classifier.classifyInstance(test.instance(numOfDistances));
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.instance(numOfDistances).setClassValue(clsLabel);
        Log.i("*****RESULT GESTURE****", (test.instance(numOfDistances).attribute(classIndex).value((int) clsLabel)));
        return test.instance(numOfDistances).attribute(classIndex).value((int) clsLabel);
    }
}
