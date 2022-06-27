// IPedometerService.aidl
package com.example.pacerdemo.service;
import com.example.pacerdemo.beans.IPedometerChartBean;
// Declare any non-default types here with import statements

interface IPedometerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void startCount();
    void stopCount();
    void resetCount();
    int getStepsCount();
    double getCalorie();
    double getDistance();
    void saveData();

    void setSensitivity(double sensitivity);
    double getSensitivity();
    void setInterval(int interval);

    int getInterval();

    long getStartTimeStamp();

    int getServiceStatus();

    PedometerChartBean getChartData();

}