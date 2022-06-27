package com.example.pacerdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pacerdemo.beans.PedometerBean;

import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DB_NAME ="PedeometerDB";
    public static final String TABLE_NAME = "pacer";
    public static final String[] COLUMNS = {
            "_id",
            "stepCount",
            "calorie",
            "distance",
            "pace",
            "speed",
            "startTime",
            "lastStepTime",
            "day",
    };

    public DBHelper(Context context,String name) {
        this(context,name,VERSION);
    }

    public DBHelper(Context context,String name, int version) {
        this(context,name,null, version);
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "(_id Integer PRIMARY KEY AUTOINCREMENT DEFAULT NULL," +
                "stepCount Integer," +
                "calorie Double," +
                "distance Double DEFAULT NULL," +
                "pace Integer," +
                "speed Double," +
                "startTime Timestamp DEFAULT NULL," +
                "lastStepTime Timestamp DEFAULT NULL," +
                "day Timestamp DEFAULT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(PedometerBean data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stepCount", data.getStepCount());
        values.put("calorie", data.getCalorie());
        values.put("distance", data.getDistance());
        values.put("pace", data.getPace());
        values.put("speed", data.getSpeed());
        values.put("startTime", data.getStartTime());
        values.put("lastStepTime", data.getLastStepTime());
        values.put("day", data.getDay());
        db.insert(DBHelper.TABLE_NAME, null, values);
        db.close();
    }

    public PedometerBean queryByDaytime(long dayTime) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery(
                "select * from " + DBHelper.TABLE_NAME
                        + " where day = " + String.valueOf(dayTime), null);

        PedometerBean data = new PedometerBean();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                data = cursor2PedometerBean(cursor);
            }
        }
        cursor.close();
        db.close();
        return data;
    }


    public ArrayList<PedometerBean> queryAll(int offVal) {
        int pageSize = 20;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.query(TABLE_NAME, null, null, null, null, null,
                "day desc limit " + String.valueOf(pageSize) + " offset " + String.valueOf(offVal), null);

        ArrayList<PedometerBean> dataList = new ArrayList<PedometerBean>();
        if (cursor!=null&&cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                PedometerBean data = new PedometerBean();
                data = cursor2PedometerBean(cursor);
                dataList.add(data);
            }
        }
        cursor.close();
        db.close();
        return dataList;
    }

    public void update(ContentValues values,long dayTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME,values,"day=?",new String[]{String.valueOf(dayTime)});
        db.close();
    }

    private PedometerBean cursor2PedometerBean(Cursor cursor) {
        PedometerBean data = new PedometerBean();
        int _id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
        int stepCount = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
        double calorie = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
        double distance = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
        int pace = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
        double speed = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
        long startTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
        long lastStepTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
        long day = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[8]));

        data.set_id(_id);
        data.setStepCount(stepCount);
        data.setCalorie(calorie);
        data.setDistance(distance);
        data.setPace(pace);
        data.setSpeed(speed);
        data.setStartTime(startTime);
        data.setLastStepTime(lastStepTime);
        data.setDay(day);
        return data;
    }
}
