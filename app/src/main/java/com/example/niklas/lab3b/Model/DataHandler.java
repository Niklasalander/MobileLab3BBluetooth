package com.example.niklas.lab3b.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * A Data handler used to find the BPM of data coming from a pulse meter.
 */
public class DataHandler {
    private static final int maxThresTopDefault = 850;
    private static final int maxThresLowDefault = 500;
    private static final int minThresTopDefault = 650;
    private static final int minThresLowDefault = 400;
    private static final int averageThresDefault = 650;
    private static final int LIST_SIZE = 20;
    private static final int TIMEOUT_THRESHOLD = 10000;
    private static final int TIME_SINCE_LAST_PAUSE_THRES = 16000;
    private static final int TOO_LONG_SINCE_LAST_BEAT = 2000;
    private static DataHandler dataHandler;
    private int bpm;
    private long lastBeat;
    private long bpmTimer;
    private long timeSinceLastPause;

    private double stCurrent;
    private double stPrior;
    private int bpmcounter;

    private double maxThresTop;
    private double maxThresLow;
    private double minThresTop;
    private double minThresLow;
    private double averageThres;

    private ArrayList<Double> list;
    private ArrayList<Integer> sequenceNrList;

    private static final double FILTER = 0.8;


    private DataHandler() {
        reset();
    }

    /**
     * Resets the data handler to its original state.
     */
    public void reset() {
        bpmTimer = Calendar.getInstance().getTimeInMillis();
        lastBeat = Calendar.getInstance().getTimeInMillis();
        timeSinceLastPause = Calendar.getInstance().getTimeInMillis();
        bpmcounter = 0;
        stCurrent = 1;
        stPrior = 1;
        bpm = 0;
        list = new ArrayList<>();
        sequenceNrList = new ArrayList<>();
        setDefaultThresholds();
    }

    public static DataHandler getInstance() {
        if (dataHandler == null)
            dataHandler = new DataHandler();
        return dataHandler;
    }

    /**
     * Calculates to BPM for using data from a pulse meter.
     * Returns
     * -2 if no beat has been detected within 10 seconds.
     * -3 if no beat has been detected within 2 seconds.
     * -4 if not enough packets were received.
     * -5 if the valueStr does not contain "sequenceNr value".
     * The BPM if a BPM could be established which is a number greater than 30.
     * @param valueStr The data. (sequenceNrSPACEvalue).
     * @return A negative number if no BPM could be established, else a positive BPM.
     */
    public int newValue(String valueStr) {
        String[] res = valueStr.split(" ");
        if (res.length != 2) {
            Log.i("DataHandler", "Received data does not include sequenceNr and value");
            return -5;
        }
        int newSequenceNr = Integer.parseInt(res[0]);
        int newValueNr = Integer.parseInt(res[1]);

        stCurrent = (FILTER * stPrior) + ((1 - FILTER) * newValueNr);
        stPrior = stCurrent;

        list.add(stCurrent);
        if (list.size() > LIST_SIZE)
            list.remove(0);
        sequenceNrList.add(newSequenceNr);
        if (sequenceNrList.size() > LIST_SIZE)
            sequenceNrList.remove(0);
        double maxValue = Collections.max(list);
        double minValue = Collections.min(list);
        double averageValue = getAverage(list);
        int index = list.indexOf(maxValue);

        if (maxValue < maxThresTop && maxValue > maxThresLow) {
            if (minValue < minThresTop && minValue > minThresLow) {
                if (averageValue > (averageThres - 100) && averageValue < (averageThres + 100)) {
                    if ((Calendar.getInstance().getTimeInMillis() - lastBeat) > 300) {
                        if (index >= 8 && index <=12){
                            if (Calendar.getInstance().getTimeInMillis() - lastBeat > 2300) {
                                bpmcounter = 0;
                                bpmTimer = Calendar.getInstance().getTimeInMillis();
                                setDefaultThresholds();
                            }
                            lastBeat = Calendar.getInstance().getTimeInMillis();
                            bpmcounter = bpmcounter + 1;
                            maxThresTop = (maxThresTop + maxValue) / 2 + 100;
                            maxThresLow = (maxThresLow + maxValue) / 2 - 100;
                            minThresTop = (minThresTop + minValue) / 2 + 100;
                            minThresLow = (minThresLow + minValue) / 2 - 100;
                            averageThres = (averageThres + averageValue) / 2;
                        }
                    }
                }
            }
        }


        double divisor = (Calendar.getInstance().getTimeInMillis() - bpmTimer) / 60000.0;
        bpm = (int)Math.round(bpmcounter / divisor);
        Log.i("BPM", "bpmcounter: " + bpmcounter);
        Log.i("BPM", "BPM: " + bpm);
        Log.i("Stcur", "stcurrent : " + stCurrent);


        if (tooManyDroppedPackets()) {
            Log.i("DataHandler", "Too many dropped packets, returning false");
            return -4;
        }
        if ((Calendar.getInstance().getTimeInMillis() - lastBeat) > TIMEOUT_THRESHOLD &&
                (Calendar.getInstance().getTimeInMillis() - timeSinceLastPause) > TIME_SINCE_LAST_PAUSE_THRES) {
            timeSinceLastPause = Calendar.getInstance().getTimeInMillis();
            Log.i("DataHandler", "No beat found in 10 seconds, too much noise returing false");
            return -2;
        }
        if ((Calendar.getInstance().getTimeInMillis() - lastBeat) > TOO_LONG_SINCE_LAST_BEAT) {
            Log.i("DataHandler", "Too long since last beat, can't find a bpm");
            return -3;
        }

        return bpm;
    }

    /**
     * If more than 6 packets were dropped in the current array return true.
     * @return Returns true or more than 6 packets were dropped.
     */
    private boolean tooManyDroppedPackets() {
        if (sequenceNrList.size() <= 0)
            return false;
        int counter = 0;
        int prev = sequenceNrList.get(0);
        for (int i = 1; i < sequenceNrList.size(); i++) {
            int next = sequenceNrList.get(i);
            Log.i("Counter", "Prev: " + prev + " Next: " + next);
            if (next != (prev + 1))
                counter++;
            prev = next;
        }
        if (counter >= 6)
            return true;
        return false;
    }

    /**
     * Sets the thresholdvalues to the default thresholdvalues.
     */
    private void setDefaultThresholds() {
        maxThresTop = maxThresTopDefault;
        maxThresLow =  maxThresLowDefault;
        minThresTop = minThresTopDefault;
        minThresLow = minThresLowDefault;
        averageThres = averageThresDefault;
        bpm = 0;
    }

    /**
     * Gets the average number of an array of numbers.
     * @param list The array with the numbers.
     * @return The average number of the arryay.
     */
    private double getAverage(ArrayList<Double> list) {
        double sum = 0;
        for (Double d : list)
            sum += d;
        return (sum / list.size());
    }
}
