package com.stayclose.sleepcapture;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
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

            for (SleepData currData : sleepDatas) {

                if (sleeping == null) {
                    sleeping = currData.getCase();
                }
                if (sleeping == currData.getCase()) {
                    if (start == 0) {
                        start = currData.getTime();
                    }
                } else {

                    SleepState temp = new SleepState();
                    temp.setEndTime(currData.getTime());
                    temp.setIsSleeping(sleeping);
                    temp.setStartTime(start);
                    finaldata.add(temp);

                    start = currData.getTime();

                    sleeping = currData.getCase();
                }
            }
            int size = sleepDatas.size();
            if (size > 0) {
                SleepState temp = new SleepState();

                temp.setIsSleeping(sleeping);
                temp.setStartTime(start);
                temp.setEndTime(sleepDatas.get(size > 1 ? (size - 1) : size).getTime());
                finaldata.add(temp);

            }
            Adapter adapter = new Adapter(finaldata);
            listView.setAdapter(adapter);

        }

    }


    Boolean getCase(int position, List<SleepData> sleepDatas) {
        boolean move = false;
        if (sleepDatas.get(position).getAccelerator() >= 1.1 || sleepDatas.get(position).getAccelerator() <= 0.9) {
            move = true;
        }
        if (!sleepDatas.get(position).isScreenState() && !move && (sleepDatas.get(position).getLight() < 15.0f)) {
            return true;
        } else {
            return false;
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
