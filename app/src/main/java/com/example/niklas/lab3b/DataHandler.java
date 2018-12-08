package com.example.niklas.lab3b;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class DataHandler {
    private static final int maxThresTopDefault = 850;
    private static final int maxThresLowDefault = 500;
    private static final int minThresTopDefault = 650;
    private static final int minThresLowDefault = 400;
    private static final int averageThresDefault = 650;
    private static DataHandler dataHandler;
    private int bpm;
    private long lastBeat;
    private long bpmTimer;

    private double stCurrent;
    private double stPrior;
    private long counter;
    private int bpmcounter;
    private int lowest;

    private double maxThresTop;
    private double maxThresLow;
    private double minThresTop;
    private double minThresLow;
    private double averageThres;

    private ArrayList<Double> list;

    private static final double FILTER = 0.8;


    private DataHandler() {
        reset();
    }

    public void reset() {
        bpmTimer = Calendar.getInstance().getTimeInMillis();
        lastBeat = Calendar.getInstance().getTimeInMillis();
        bpmcounter = 0;
        stCurrent = 1;
        stPrior = 1;
        bpm = 0;
        list = new ArrayList<>();
        setDefaultThresholds();
    }

    public static DataHandler getInstance() {
        if (dataHandler == null)
            dataHandler = new DataHandler();
        return dataHandler;
    }

    public boolean newValue(String valueStr) {
        String[] res = valueStr.split(" ");
        int newSequenceNr = Integer.parseInt(res[0]);
        int newValueNr = Integer.parseInt(res[1]);

        stCurrent = (FILTER * stPrior) + ((1 - FILTER) * newValueNr);
        stPrior = stCurrent;

        list.add(stCurrent);

        if (list.size() > 20)
            list.remove(0);
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
//        Log.i("BPM", "divisor: " + divisor);
        Log.i("BPM", "BPM: " + bpm);
        Log.i("Stcur", "stcurrent : " + stCurrent);
//        Log.i("BPM", "bpm time : " + (Calendar.getInstance().getTimeInMillis() - bpmTimer));
//        Log.i("BPM", "lst time : " + (Calendar.getInstance().getTimeInMillis() - lastBeat));

        /* If not beat within 10 seconds, abort */
        if (Calendar.getInstance().getTimeInMillis() - lastBeat > 10000)
            return false;
        return true;
    }

    private void setDefaultThresholds() {
        maxThresTop = maxThresTopDefault;
        maxThresLow =  maxThresLowDefault;
        minThresTop = minThresTopDefault;
        minThresLow = minThresLowDefault;
        averageThres = averageThresDefault;
        bpm = 0;
    }


    private double getAverage(ArrayList<Double> list) {
        double sum = 0;
        for (Double d : list)
            sum += d;
        return (sum / list.size());
    }
}
