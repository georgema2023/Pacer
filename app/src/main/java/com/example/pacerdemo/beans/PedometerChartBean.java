package com.example.pacerdemo.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class PedometerChartBean implements Parcelable {
    private int[] dataArray;
    private int index;

    public PedometerChartBean() {
        dataArray = new int[1440];
        index = 0;
    }

    protected PedometerChartBean(Parcel in) {
        dataArray = in.createIntArray();
        index = in.readInt();
    }

    public static final Creator<PedometerChartBean> CREATOR = new Creator<PedometerChartBean>() {
        @Override
        public PedometerChartBean createFromParcel(Parcel in) {
            return new PedometerChartBean(in);
        }

        @Override
        public PedometerChartBean[] newArray(int size) {
            return new PedometerChartBean[size];
        }
    };

    public int[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(int[] dataArray) {
        this.dataArray = dataArray;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(dataArray);
        dest.writeInt(index);
    }

    public void reset() {
        index = 0;
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = 0;
        }
    }
}
