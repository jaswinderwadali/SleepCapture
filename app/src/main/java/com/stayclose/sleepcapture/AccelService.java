package com.stayclose.sleepcapture;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Shubhankar on 28-10-2015.
 */
public class AccelService extends Service implements SensorEventListener {
    private SensorManager mSensMan;
    private Sensor accel, light;
    boolean moving;
    boolean walk;
    long startservice;
    SharedPreferences preference;
    String walkkey = "com.touchKin.touchkinapp.Dashboards.services.lastwalktime";
    String movekey = "com.touchKin.touchkinapp.Dashboards.services.lastmovetime";
    long lastwalktime;
    long lastmovetime;
    float relgrav;
    //some notification parameters
    NotificationManager notificationManager;

    SleepData sleepData;
    List<SleepData> sleepDataList;


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        sleepData = new SleepData();
        String data = preference.getString(MainActivity.DATA_LIST, "");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<SleepData>>() {
        }.getType();
        sleepDataList = gson.fromJson(data, listType);

        screenState();

        Log.v("Accel servicestarted", "AccelserviceStarted");
        //initialize sensormanager
        mSensMan = (SensorManager) getSystemService(SENSOR_SERVICE);

        //initialize accelerometer
        accel = mSensMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = mSensMan.getDefaultSensor(Sensor.TYPE_LIGHT);
        //get current time
        Calendar cal = Calendar.getInstance();
        startservice = cal.getTimeInMillis();
        Log.v("Curent time", startservice + "");

        //obtain last walk time stored in shared pref, if null, store present time in shared pref
//        lastwalktime = (preference.getLong(walkkey, startservice));
//
//        if (lastwalktime == startservice) {
//            preference.edit().putLong(walkkey, startservice).apply();
//        }
//        //obtain last move time
//        lastmovetime = (preference.getLong(movekey, startservice));
//
//        if (lastmovetime == startservice) {
//            preference.edit().putLong(movekey, startservice).apply();
//        }


//        Log.v("Walk time", lastwalktime + "");

        //stop service if time since last walk detected is less than 15 mins
//        if ((startservice - lastwalktime < 15 * 60 * 1000)) {
//            Log.v("Service stopped", "Walk detected within 15 min of now");
//            stopSelf();
//        } else
//            //or if movement was there in the past 5 min that didnt qualify as walk
//            if (startservice - lastmovetime <= 5 * 60 * 1000) {
//                Log.v("Service stopped", "non walk movement detected within 5 min of now");
//                stopSelf();
//            } else {
        //register accelerometer listener
        mSensMan.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        mSensMan.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
//            }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context ctx = getApplicationContext();
        preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        walk = false;
        moving = false;
        //initialize notifications
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.d("Event", "Fire");


        synchronized (this) {
            //monitor only for accelerometer changes
            int type = sensorEvent.sensor.getType();
            if (type == android.hardware.Sensor.TYPE_ACCELEROMETER) {

                //get times of when sensor is being changed
                Calendar cal = Calendar.getInstance();
                long now = cal.getTimeInMillis();
                Log.d("NOW", "" + now);

                //monitor raw data for first 2 seconds, can be skipped as soon as a movement is detected
                if ((!(now - startservice >= 2 * 1000) || relgrav == 0.0f) && !moving) {
                    //initialize values for sensor event
                    float[] value = sensorEvent.values;
                    float x = Float.valueOf(value[0]);
                    float y = Float.valueOf(value[1]);
                    float z = Float.valueOf(value[2]);
                    //check the current accelerometer val relative to earths gravity
                    relgrav = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                    //user is moving if rg more than 1.1 or less than 0.9
                    Log.d("Relgraph", "" + relgrav);
                    if (relgrav >= 1.1 || relgrav <= 0.9) {
                        //movement is happening
//                        Log.d("Moving", "Some movement is present" + relgrav);
                        moving = true;
                    } else {
                        //user is not moving
//                        Log.d("Not moving", "No movement" + relgrav);
                    }

                } else {
                    stopService();
                }
            }
            if (type == android.hardware.Sensor.TYPE_LIGHT) {
                sleepData.setLight(sensorEvent.values[0]);
                Log.d("Light_event", "Fire");
            }
        }
    }

    private void stopService() {
        mSensMan.unregisterListener(this, accel);
        mSensMan.unregisterListener(this, light);
        stopSelf();

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void screenState() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean screenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }
        if (screenOn) {
            sleepData.setScreenState(true);
        } else {
            sleepData.setScreenState(false);
        }
    }


    @Override
    public void onDestroy() {
        if (sleepDataList == null)
            sleepDataList = new ArrayList<>();
        sleepData.setTime(System.currentTimeMillis());
        sleepData.setAccelerator(relgrav);
        Gson gsonq = new Gson();
        String json12 = gsonq.toJson(sleepData);
        Log.d("finaly_data", json12);
        sleepDataList.add(sleepData);
        Gson gson = new Gson();
        String json = gson.toJson(sleepDataList);
        preference.edit().putString(MainActivity.DATA_LIST, json).commit();
        super.onDestroy();
        Log.v("AccelService", "AccelService stopped");
    }


}
