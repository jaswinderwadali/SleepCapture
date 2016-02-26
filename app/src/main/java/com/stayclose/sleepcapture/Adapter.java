package com.stayclose.sleepcapture;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mantralab on 24/02/16.
 */
public class Adapter extends BaseAdapter {

    List<SleepState> sleepDatas;

    Adapter(List<SleepState> sleepDatas) {
        this.sleepDatas = sleepDatas;
    }

    @Override
    public int getCount() {
        return sleepDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
        TextView five = (TextView) view.findViewById(R.id.five);
        LineChart accelChart = (LineChart) view.findViewById(R.id.accel_graph);
        LineChart lightChart = (LineChart) view.findViewById(R.id.light_graph);
        BarChart screenChart = (BarChart) view.findViewById(R.id.screen_graph);

        SleepState current = sleepDatas.get(position);
        if (!current.isSleeping()) {
            five.setText("Awake :" + current.getInterval());
            view.setBackgroundColor(Color.parseColor("#FFFFF176"));
            five.setTextColor(Color.DKGRAY);
        } else {
            five.setText("Sleeping :" + current.getInterval());
            view.setBackgroundColor(Color.parseColor("#1c3a49"));
            five.setTextColor(Color.WHITE);
        }


        //accel chart setup
        accelChart.invalidate();
        ArrayList<Entry> accel = new ArrayList<>();
        ArrayList<Float> readings = current.getAccelerometerReadings();
        List<String> x = new ArrayList<>();
        for (int i = 0; i < readings.size(); i++) {
            accel.add(new Entry(readings.get(i), i));
            x.add("");
        }

        //chart properties
        accelChart.setDrawGridBackground(false);
        accelChart.animateY(1500);
        accelChart.setPinchZoom(true);
        accelChart.setAutoScaleMinMaxEnabled(false);
        accelChart.setScaleYEnabled(false);
        accelChart.setDescription("");

        Legend leg1 = accelChart.getLegend();
        leg1.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        leg1.setXEntrySpace(15);
        leg1.setTextSize(11);
        leg1.setFormSize(11);
        leg1.setYOffset(2f);
        leg1.setTextColor(Color.parseColor("#00bed6"));

        //setting up axes
        XAxis xAxis = accelChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = accelChart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setStartAtZero(true);

        YAxis rightAxis = accelChart.getAxisRight();
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawLabels(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setStartAtZero(true);

        LineDataSet yVals = new LineDataSet(accel, "Accel");
        yVals.setDrawCircleHole(false);
        yVals.setCircleColor(Color.parseColor("#00bed6"));
        yVals.setCircleSize(1.5f);
        yVals.setDrawValues(false);
        yVals.setLineWidth(1.5f);
        yVals.setColor(Color.parseColor("#00bed6"));

        accelChart.setData(new LineData(x, yVals));


        //light chart setup
        lightChart.invalidate();
        ArrayList<Entry> light = new ArrayList<>();
        ArrayList<Float> lightreadings = current.getLightReadings();
        List<String> x1 = new ArrayList<>();
        for (int i = 0; i < lightreadings.size(); i++) {
            light.add(new Entry(lightreadings.get(i), i));
            x1.add("");
        }

        //chart properties
        lightChart.setDrawGridBackground(false);
        lightChart.animateY(1500);
        lightChart.setPinchZoom(true);
        lightChart.setAutoScaleMinMaxEnabled(false);
        lightChart.setScaleYEnabled(false);
        lightChart.setDescription("");

        Legend leg2 = lightChart.getLegend();
        leg2.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        leg2.setXEntrySpace(15);
        leg2.setTextSize(11);
        leg2.setFormSize(11);
        leg2.setYOffset(2f);
        leg2.setTextColor(Color.parseColor("#00bed6"));

        //setting up axes
        XAxis xAxis1 = lightChart.getXAxis();
        xAxis1.setDrawGridLines(false);
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis1 = lightChart.getAxisLeft();
        leftAxis1.setDrawAxisLine(true);
        leftAxis1.setDrawGridLines(false);
        leftAxis1.setDrawLabels(true);
        leftAxis1.setStartAtZero(true);

        YAxis rightAxis1 = lightChart.getAxisRight();
        rightAxis1.setDrawAxisLine(true);
        rightAxis1.setDrawLabels(true);
        rightAxis1.setDrawGridLines(false);
        rightAxis1.setStartAtZero(true);

        LineDataSet yVals1 = new LineDataSet(light, "Light");
        yVals1.setDrawCircleHole(false);
        yVals1.setCircleColor(Color.parseColor("#00bed6"));
        yVals1.setCircleSize(1.5f);
        yVals1.setDrawValues(false);
        yVals1.setLineWidth(1.5f);
        yVals1.setColor(Color.parseColor("#00bed6"));

        lightChart.setData(new LineData(x1, yVals1));

        //screen chart setup
        screenChart.invalidate();
        ArrayList<BarEntry> screen = new ArrayList<>();
        ArrayList<Boolean> screenStates = current.getScreenStates();
        ArrayList<Integer> stateColors = new ArrayList<>();
        List<String> x2 = new ArrayList<>();
        for (int i = 0; i < screenStates.size(); i++) {
            if (screenStates.get(i) == true) {
                screen.add(new BarEntry(2f, i));
                stateColors.add(i, Color.parseColor("#27AA0B"));
            } else {
                screen.add(new BarEntry(2f, i));
                stateColors.add(i, Color.parseColor("#F32F00"));
            }
            x2.add("");
        }

        //chart properties
        screenChart.setDrawGridBackground(false);
        screenChart.animateX(1500);
        screenChart.setPinchZoom(false);
        screenChart.setTouchEnabled(false);
        screenChart.setAutoScaleMinMaxEnabled(false);
        screenChart.setScaleYEnabled(false);
        screenChart.setDescription("");

        //setting up axes
        XAxis xAxis2 = screenChart.getXAxis();
        xAxis2.setDrawGridLines(false);
        xAxis2.setDrawAxisLine(false);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis2 = screenChart.getAxisLeft();
        leftAxis2.setDrawAxisLine(false);
        leftAxis2.setDrawGridLines(false);
        leftAxis2.setDrawLabels(false);
        leftAxis2.setStartAtZero(true);

        YAxis rightAxis2 = screenChart.getAxisRight();
        rightAxis2.setDrawAxisLine(false);
        rightAxis2.setDrawLabels(false);
        rightAxis2.setDrawGridLines(false);
        rightAxis2.setStartAtZero(true);

        BarDataSet yVals2 = new BarDataSet(screen, "ScreenState");
        yVals2.setDrawValues(false);
        yVals2.setBarSpacePercent(0);
        yVals2.setColors(stateColors);

        List<Integer> colorint = new ArrayList<>();
        colorint.add(0, Color.parseColor("#27AA0B"));
        colorint.add(1, Color.parseColor("#F32F00"));

        List<String> colorval = new ArrayList<>();
        colorval.add(0, "Screen on");
        colorval.add(1, "Screen off");

        Legend leg3 = screenChart.getLegend();
        leg3.setXEntrySpace(15);
        leg3.setCustom(colorint, colorval);
        leg3.setTextSize(11);
        leg3.setFormSize(11);
        leg3.setYOffset(2f);
        leg3.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        leg3.setTextColor(Color.parseColor("#00bed6"));


        screenChart.setData(new BarData(x2, yVals2));


        return view;

    }
}
