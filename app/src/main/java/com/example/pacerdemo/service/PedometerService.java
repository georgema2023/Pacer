package com.example.pacerdemo.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.example.pacerdemo.beans.PedometerChartBean;
import com.example.pacerdemo.db.DBHelper;
import com.example.pacerdemo.frame.FrameApplication;
import com.example.pacerdemo.utils.ACache;
import com.example.pacerdemo.beans.Settings;
import com.example.pacerdemo.utils.Utiles;
import com.example.pacerdemo.beans.PedometerBean;

public class PedometerService extends Service {

    private SensorManager sensorManager;
    private PedometerBean data;
    private PedometerListener listener;
    private Settings settings;
    private PedometerChartBean chartData;

    public static final long UPDATE_CHAT_INTERVAL = 60000L;
    private static Handler handler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (runStatus==STATUS_RUNNING&&handler!=null&&chartData!=null) {
                handler.removeCallbacks(timeRunnable);
                updateChartData();
                handler.postDelayed(timeRunnable, UPDATE_CHAT_INTERVAL);

            }
        }
    };

    public static final int STATUS_NOT_RUN = 0;
    public static final int STATUS_RUNNING = 1;
    private int runStatus=STATUS_NOT_RUN;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        data = new PedometerBean();
        listener = new PedometerListener(data);
        settings = new Settings(this);
        chartData = new PedometerChartBean();
    }

    private void updateChartData() {
        if (chartData.getIndex()<1440-1) {
            chartData.setIndex(chartData.getIndex()+1);
            chartData.getDataArray()[chartData.getIndex()] = data.getStepCount();
        }
    }

    private void saveChartData() {
        String jsonStr = Utiles.objToJSON(chartData);
        ACache.get(FrameApplication.getInstance()).put("JsonChartData",jsonStr);

    }

    public double getCalorieBySteps(int stepCount) {
        float stepLen = settings.getStepLength();
        float weight = settings.getWeight();
        double METRIC_WALK_FACTOR = 0.708;
        double METRIC_RUN_FACTOR = 1.02784823;
        double calories = weight*METRIC_WALK_FACTOR*stepLen*stepCount/100000.0;
        return calories;
    }

    public double getDistanceVal(int stepCount) {
        float stepLen=settings.getStepLength();
        double distance = stepCount*(long)(stepLen)/100000.0f;
        return distance;
    }

    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void startCount() throws RemoteException {

            if (sensorManager!=null&&listener!=null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                data.setStartTime(System.currentTimeMillis());
                data.setDay(Utiles.getTimeStampByDay());
                runStatus = STATUS_RUNNING;
                handler.postDelayed(timeRunnable,UPDATE_CHAT_INTERVAL);
            }
        }

        @Override
        public void stopCount() throws RemoteException {

            if (sensorManager!=null&&listener!=null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.unregisterListener(listener,sensor);
                runStatus = STATUS_NOT_RUN;
                handler.removeCallbacks(timeRunnable);
            }
        }

        @Override
        public void resetCount() throws RemoteException {

            if (data!=null) {
                data.reset();
                saveData();
            }

            if (chartData!=null) {
                chartData.reset();
                saveChartData();
            }
            if (listener!=null) {
                listener.setCurrentSteps(0);
            }
        }

        @Override
        public int getStepsCount() throws RemoteException {
            if (data!=null) {
                return data.getStepCount();
            }
            return 0;
        }



        @Override
        public double getCalorie() throws RemoteException {
            if (data!=null) {
                return getCalorieBySteps(data.getStepCount());
            }
            return 0;
        }

        @Override
        public double getDistance() throws RemoteException {
            if (data!=null){
                return getDistanceVal(data.getStepCount());
            }
            return 0;
        }

        @Override
        public void saveData() throws RemoteException {

            if (data!=null) {
                new Thread() {
                    @Override
                    public void run() {
                        DBHelper dbHelper = new DBHelper(PedometerService.this,DBHelper.DB_NAME);
                        try {
                            data.setDistance(getDistance());
                            data.setCalorie(getCalorieBySteps(data.getStepCount()));
                            long time = (data.getLastStepTime()-data.getStartTime())/1000;
                            if (time==0) {
                                data.setPace(0);
                                data.setSpeed(0);
                            } else {
                                int pace = Math.round(60*data.getStepCount()/time);
                                data.setPace(pace);
                                long speed = Math.round((data.getDistance()/1000)/(time/3600));
                                data.setSpeed(speed);
                            }
                            dbHelper.insert(data);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }

        @Override
        public void setSensitivity(double sensitivity) throws RemoteException {

            if(settings!=null) {
                settings.setSensitivity((float) sensitivity);
            }

            if(listener!=null) {
                listener.setSensitivity((float) sensitivity);
            }
        }

        @Override
        public double getSensitivity() throws RemoteException {
            if (settings!=null) {
                return settings.getSensitivity();
            }
            return 0;
        }

        @Override
        public void setInterval(int interval) throws RemoteException {

            if (settings!=null) {
                settings.setInterval(interval);
            }

            if(listener!=null) {
                listener.setmLimit(interval);
            }
        }

        @Override
        public int getInterval() throws RemoteException {
            if (settings!=null) {
                return settings.getInterval();
            }
            return 0;
        }

        @Override
        public long getStartTimeStamp() throws RemoteException {
            if (data!=null) {
                return data.getStartTime();
            }
            return 0L;
        }

        @Override
        public int getServiceStatus() throws RemoteException {
            return runStatus;
        }

        @Override
        public PedometerChartBean getChartData() throws RemoteException {
            return chartData;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iPedometerService;
    }
}
