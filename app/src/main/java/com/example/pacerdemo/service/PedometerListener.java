package com.example.pacerdemo.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;

import com.example.pacerdemo.beans.PedometerBean;

public class PedometerListener implements SensorEventListener {
    private int currentSteps = 0;
    private float sensitivity = 30;
    private long mLimit = 300;

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setmLimit(long mLimit) {
        this.mLimit = mLimit;
    }

    private float mLastValue;
    private float mScale = -4f;

    private float offset = 240f;

    private long start = 0;
    private long end = 0;

    private float mLastDirection;
    private float mLastExtremes[][] = new float[2][1];
    private float mLastDiff;
    private int mLastMatch = -1;

    private PedometerBean data;

    public PedometerListener(PedometerBean data) {
        this.data = data;
    }

    public void setCurrentSteps(int step){
        currentSteps = step;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float sum = 0;
                for (int i = 0; i < 3; i++) {
                    float vector = offset + event.values[i] * mScale;
                    sum += vector;
                }
                float average = sum/3;
                float dir;
                if (average>mLastValue) {
                    dir = 1;
                } else if (average<mLastValue) {
                    dir = -1;
                } else {
                    dir =0;
                }
                if (dir ==-mLastDirection) {
                    int extType = (dir>0?0:1);
                    mLastExtremes[extType][0] = mLastValue;
                    float diff = Math.abs(mLastExtremes[extType][0]-mLastExtremes[1-extType][0]);
                    if (diff>sensitivity) {
                        boolean isLargeAsPrevious = diff>(mLastDiff*2/3);
                        boolean isPreviousLargeEnough = mLastDiff>(diff/3);
                        boolean isNotContra = (mLastMatch!=1-extType);
                        if (isLargeAsPrevious&&isPreviousLargeEnough&&isNotContra) {
                            end = System.currentTimeMillis();
                            if (end-start>mLimit) {
                                currentSteps++;
                                mLastMatch = extType;
                                start = end;
                                mLastDiff = diff;
                                if (data!=null) {
                                    data.setStepCount(currentSteps);
                                    data.setLastStepTime(System.currentTimeMillis());
                                }
                            } else {
                                mLastDiff = sensitivity;
                            }
                        } else{
                            mLastMatch = -1;
                            mLastDiff = sensitivity;
                        }
                    }
                }
                mLastDirection = dir;
                mLastValue = average;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
