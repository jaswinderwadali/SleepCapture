package com.stayclose.sleepcapture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mantralab on 25/02/16.
 */
public class SleepState {

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public void setIsSleeping(boolean isSleeping) {
        this.isSleeping = isSleeping;
    }

    public String getInterval() {
        String interval = "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        Date date = new Date();

        date.setTime(getStartTime());

        interval = sdf.format(date);

        date.setTime(getEndTime());

        interval = interval + " - " + sdf.format(date);

        return interval + "\nTotal time : " + getDuration() + " min";
    }

    public float getDuration() {
        return (float) (getEndTime() - getStartTime()) / (float) (60 * 1000);

    }

    public ArrayList<Float> getLightReadings() {
        return lightReadings;
    }

    public ArrayList<Float> getAccelerometerReadings() {
        return accelerometerReadings;
    }

    public void setLightReadings(ArrayList<Float> lightReadings) {
        this.lightReadings = lightReadings;
    }

    public void setAccelerometerReadings(ArrayList<Float> accelerometerReadings) {
        this.accelerometerReadings = accelerometerReadings;
    }

    public void setScreenStates(ArrayList<Boolean> screenStates) {
        this.screenStates = screenStates;
    }

    public ArrayList<Boolean> getScreenStates() {
        return screenStates;
    }

    long startTime, endTime;
    boolean isSleeping;
    ArrayList<Float> lightReadings, accelerometerReadings;
    ArrayList<Boolean> screenStates;
}
