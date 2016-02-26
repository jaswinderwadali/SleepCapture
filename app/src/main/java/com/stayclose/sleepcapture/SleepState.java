package com.stayclose.sleepcapture;

import java.text.SimpleDateFormat;
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
    public String getInterval(){
        String interval ="";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        Date date = new Date();

        date.setTime(getStartTime());

        interval=sdf.format(date);

        date.setTime(getEndTime());

        interval=interval+" - "+sdf.format(date);

        return interval+"\n Total time : "+getDuration()+" min";
    }

    public float getDuration(){
        return (float)(getEndTime()-getStartTime())/(float)(60*1000);

    }
    long startTime, endTime;
    boolean isSleeping;
}
