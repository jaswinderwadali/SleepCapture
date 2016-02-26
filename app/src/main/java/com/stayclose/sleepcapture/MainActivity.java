package com.stayclose.sleepcapture;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String DATA_LIST = "SLEEP_DATA";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMonitoringAccelerometer();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(DATA_LIST)) {
            List<SleepData> sleepData = new ArrayList<>();
            Gson gson = new Gson();
            String dataList = gson.toJson(sleepData);
            sharedPreferences.edit().putString(DATA_LIST, dataList).commit();
        } else {
            String data = sharedPreferences.getString(DATA_LIST, "nothing inside");

            ListView listView = (ListView) findViewById(R.id.listview);

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<SleepData>>() {
            }.getType();

            List<SleepData> sleepDatas = gson.fromJson(data, listType);
            List<SleepState> finaldata = new ArrayList<>();

            boolean sleepingCheck = true;
            boolean NotsleepingCheck = true;

            Boolean sleeping = null;
            long start = 0;

            ArrayList<Float> accelerometerReadings = new ArrayList<>();
            ArrayList<Float> lightReadings = new ArrayList<>();
            ArrayList<Boolean> screenStates = new ArrayList<>();

            for (SleepData currData : sleepDatas) {

                if (sleeping == null) {
                    sleeping = currData.getCase();
                }
                if (sleeping == currData.getCase()) {
                    if (start == 0) {
                        start = currData.getTime();
                    }
                    accelerometerReadings.add(currData.getAccelerator());
                    lightReadings.add(currData.getLight());
                    screenStates.add(currData.getScreenState());
                } else {

                    SleepState temp = new SleepState();
                    temp.setEndTime(currData.getTime());
                    temp.setIsSleeping(sleeping);
                    temp.setStartTime(start);
                    temp.setLightReadings(lightReadings);
                    temp.setAccelerometerReadings(accelerometerReadings);
                    temp.setScreenStates(screenStates);
                    finaldata.add(temp);

                    start = currData.getTime();
                    sleeping = currData.getCase();

                    //start new ArrayLists
                    accelerometerReadings = new ArrayList<>();
                    lightReadings = new ArrayList<>();
                    screenStates = new ArrayList<>();

                    //Add first value to new ArrayLists
                    accelerometerReadings.add(currData.getAccelerator());
                    lightReadings.add(currData.getLight());
                    screenStates.add(currData.getScreenState());

                }
            }
            int size = sleepDatas.size();
            if (size > 0) {
                SleepState temp = new SleepState();

                temp.setIsSleeping(sleeping);
                temp.setStartTime(start);
                temp.setEndTime(sleepDatas.get(size > 0 ? (size - 1) : size).getTime());
                temp.setScreenStates(screenStates);
                temp.setAccelerometerReadings(accelerometerReadings);
                temp.setLightReadings(lightReadings);
                finaldata.add(temp);

            }


//            Iterator<SleepState> it = finaldata.iterator();
//            while (it.hasNext()) {
//                if (it.next().getDuration() <= 4f) {
//                    it.remove();
//                }
//            }
            ArrayList<SleepState> cumilativeData = new ArrayList<>();
            ArrayList<SleepState> interrupts = new ArrayList<>();

            for (int i = 0; i < finaldata.size(); i++) {
                if (i == 0) {
                    cumilativeData.add(finaldata.get(i));
                } else {
                    int lastPos = cumilativeData.size() - 1;
                    if (finaldata.get(i).isSleeping() == cumilativeData.get(lastPos).isSleeping()) {
                        cumilativeData.get(lastPos).setEndTime(finaldata.get(i).getEndTime());
                        cumilativeData.get(lastPos).addAccelerometerData(finaldata.get(i).getAccelerometerReadings());
                        cumilativeData.get(lastPos).addLightData(finaldata.get(i).getLightReadings());
                        cumilativeData.get(lastPos).addScreenData(finaldata.get(i).getScreenStates());
                    } else {
                        if (finaldata.get(i).getDuration() > 5) {
                            cumilativeData.add(finaldata.get(i));
                        } else {
                            interrupts.add(finaldata.get(i));
                        }
                    }
                }
            }

            Adapter adapter = new Adapter(cumilativeData);
            Gson gson1 = new Gson();
            Log.d("interrupts", "" + gson1.toJson(interrupts).toString());
            listView.setAdapter(adapter);
        }

    }

    public void startMonitoringAccelerometer() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, AccelService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 3, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Start service every 2 minutes
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 2 * 60 * 1000, pendingIntent);
    }


}
