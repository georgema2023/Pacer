package com.example.pacerdemo.frame;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

public class ErrorHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        LogWriter.LogToFile("Error: "+e.getMessage());
        LogWriter.LogToFile("Thread Name: "+t.getName()+" Thread Id: "+t.getId());

        final StackTraceElement[] trace = e.getStackTrace();
        for (final StackTraceElement element:trace) {
            LogWriter.LogToFile("Line: "+element.getLineNumber()+" : "+element.getMethodName());
        }
        e.printStackTrace();
        FrameApplication.exitApp();

    }

    private static ErrorHandler instance;

    public static ErrorHandler getInstance() {
        if (ErrorHandler.instance==null) {
            ErrorHandler.instance = new ErrorHandler();
        }
        return ErrorHandler.instance;
    }

    public ErrorHandler() {}

    public void setErrorHandler(final Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

}
