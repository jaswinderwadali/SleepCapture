package com.stayclose.sleepcapture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
//        TextView one = (TextView) view.findViewById(R.id.one);
//        TextView two = (TextView) view.findViewById(R.id.two);
//        TextView three = (TextView) view.findViewById(R.id.three);
//        TextView four = (TextView) view.findViewById(R.id.four);
        TextView five = (TextView) view.findViewById(R.id.five);
        five.setText("Interval :" + sleepDatas.get(position).getInterval());
        if (!sleepDatas.get(position).isSleeping()) {
            view.setBackgroundResource(R.color.colorAccent);
        }
        return view;

    }
}
