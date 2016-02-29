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

            //obtained stored datapoints
            ArrayList<SleepData> sleepDatas = gson.fromJson(data, listType);

            //group data points
            ArrayList<SleepState> finaldata = groupData(sleepDatas);
            Log.d("Size after", "preliminary grouping " + finaldata.size());

            //group datasets removing interrupts
            ArrayList<SleepState> cumilativeData = finaldata;

            for (int i = 1; i <= 7; i++) {
                cumilativeData = cleanData(cumilativeData, 2 * i, i);
                Log.d("Size after", i + " cleanups = " + cumilativeData.size());
            }

//            cumilativeData = removeNonSleep(cumilativeData, 5 * 60);
            Adapter adapter = new Adapter(cumilativeData);
            listView.setAdapter(adapter);
        }

    }

    private ArrayList<SleepState> removeNonSleep(ArrayList<SleepState> cumilativeData, int minSleep) {
        Iterator<SleepState> iterator = cumilativeData.iterator();
        while (iterator.hasNext()) {
            SleepState tempp = iterator.next();
            if (!tempp.isSleeping() || tempp.getDuration() <= minSleep) {
                iterator.remove();
            }
        }
        return cumilativeData;
    }

    private ArrayList<SleepState> groupData(ArrayList<SleepData> sleepDatas) {
        Log.d("Size of raw points", "" + sleepDatas.size());

        ArrayList<SleepState> finalData = new ArrayList<>();
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
                finalData.add(temp);

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
            finalData.add(temp);
        }
        return finalData;
    }

    public ArrayList<SleepState> cleanData(ArrayList<SleepState> originalData, float trimInterval, int iteration) {
        Log.d("Size received", "for cleanup " + originalData.size());
        ArrayList<SleepState> processedData = new ArrayList<>();
        ArrayList<SleepState> interrupts = new ArrayList<>();
        for (int i = 0; i < originalData.size(); i++) {
            if (i == 0) {
                processedData.add(originalData.get(i));
            } else {
                int lastPos = processedData.size() - 1;
                if (originalData.get(i).isSleeping() == processedData.get(lastPos).isSleeping()) {
                    processedData.get(lastPos).setEndTime(originalData.get(i).getEndTime());
                    processedData.get(lastPos).addAccelerometerData(originalData.get(i).getAccelerometerReadings());
                    processedData.get(lastPos).addLightData(originalData.get(i).getLightReadings());
                    processedData.get(lastPos).addScreenData(originalData.get(i).getScreenStates());
                } else {
                    if (originalData.get(i).getDuration() > trimInterval) {
                        processedData.add(originalData.get(i));
                    } else {
                        interrupts.add(originalData.get(i));
                    }
                }
            }
        }
        Gson gson1 = new Gson();
        Log.d("Cleanup round", iteration + " interrupts found = " + interrupts.size() /*+
                ", interrupts = " + gson1.toJson(interrupts).toString()*/);
        return processedData;
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
