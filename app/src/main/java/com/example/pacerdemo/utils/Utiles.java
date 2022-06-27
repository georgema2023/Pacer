package com.example.pacerdemo.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Utiles {

    public static String objToJSON(Object object) {

        Gson gson = new Gson();
        return gson.toJson(object);
    }
    public static long getTimeStampByDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String dateStr = sdf.format(d);
        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }



    public static String getFormatVal(double val) {

        return getFormatVal(val,"0.00");
    }

    public static String getFormatVal(double val,String formatStr) {
        DecimalFormat decimalFormat = new DecimalFormat(formatStr);
        return decimalFormat.format(val);
    }

    public static boolean isServiceRuning(Context context,String serviceName){

        boolean isRunning = false;
        if (context==null||serviceName==null) {
            return isRunning;
        }
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List serviceList = manager.getRunningServices(Integer.MAX_VALUE);
        Iterator iterator = serviceList.iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) iterator.next();
            if (serviceName.trim().equals(runningServiceInfo.service.getClassName())) {

                isRunning = true;
                return isRunning;
            }
        }
        return isRunning;

    }
}
