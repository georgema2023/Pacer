package com.example.pacerdemo.biz;

import android.widget.TextView;

import com.example.pacerdemo.beans.PedometerChartBean;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class ChartBiz {
    public void updateChart(PedometerChartBean chartData, BarChart mChart, TextView mTimeTxt) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        if (chartData!=null) {
            for (int i = 1; i <= chartData.getIndex(); i++) {
                xVals.add(String.valueOf(i)+"min");
                int yVal = chartData.getDataArray()[i];
                yVals.add(new BarEntry(i,yVal));
            }
            mTimeTxt.setText(String.valueOf(chartData.getIndex())+"min");
            BarDataSet barDataSet = new BarDataSet(yVals,"Steps");
//            ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
//            barDataSets.add(barDataSet);
            BarData barData = new BarData(barDataSet);
            barData.setValueTextSize(10f);
            mChart.setData(barData);
            mChart.invalidate();

        }

    }
}
