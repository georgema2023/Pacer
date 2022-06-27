package com.example.pacerdemo.frame;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;

public class FrameApplication extends Application {
    private static LinkedList<Activity> activities = new LinkedList<Activity>();
    public LinkedList<Activity> getActivityList() {
        return activities;
    }
    public static void addToActivityList(final Activity activity) {

        if (activity!=null) {
            activities.add(activity);
        }
    }

    public static void removeFromActivityList(final Activity activity) {
        if (activities!=null&&activities.size()>0&&activities.indexOf(activity)!=-1) {
            activities.remove(activity);
        }
    }

    public static void clearActivities() {
        for (int i = activities.size(); i >= 0 ; i--) {

            final Activity activity = activities.get(i);
            if (activity!=null) {
                activity.finish();
            }
        }
    }

    public static void exitApp() {
        try{
            clearActivities();
        } catch (final Exception e) {

        } finally {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private PrefsManager prefsManager;

    private static FrameApplication instance;

    public static FrameApplication getInstance() {
        return instance;
    }

    public PrefsManager getPrefsManager() {
        return prefsManager;
    }


    private ErrorHandler errorHandler;
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefsManager = new PrefsManager(this);

        errorHandler = ErrorHandler.getInstance();
    }
}
