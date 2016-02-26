package com.stayclose.sleepcapture;

/**
 * Created by mantralab on 24/02/16.
 */
public class SleepData {

    boolean screenState;
    float accelerator, light, noise;
    long time;
    long endTime;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean getScreenState() {
        return screenState;
    }

    public void setScreenState(boolean screenState) {
        this.screenState = screenState;
    }

    public float getAccelerator() {
        return accelerator;
    }

    public void setAccelerator(float accelerator) {
        this.accelerator = accelerator;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public float getNoise() {
        return noise;
    }

    public void setNoise(float noise) {
        this.noise = noise;
    }


    Boolean getCase() {
        boolean move = false;
        if (getAccelerator() >= 1.1 || getAccelerator() <= 0.8) {
            move = true;
        }
        if (!getScreenState() && !move && getLight() < 15.0f) {
            return true;
        } else {
            return false;
        }
    }


}
