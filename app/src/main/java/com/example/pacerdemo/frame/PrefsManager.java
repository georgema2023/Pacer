package com.example.pacerdemo.frame;

import android.content.Context;

public class PrefsManager {

    private final Context mContext;
    private static final String PREFERENCE_NAME = "pacer";


    public PrefsManager(Context context) {
        this.mContext = context;
    }

    public void clear() {
        mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit().clear().commit();

    }

    public boolean contains() {
        return mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME,
                Context.MODE_PRIVATE).contains(PrefsManager.PREFERENCE_NAME);
    }

    public boolean getBoolean(final String key) {
        return mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME,
                Context.MODE_PRIVATE).getBoolean(key, false);

    }

    public boolean getBooleanDefaultTrue(final String key) {
        return mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME,
                Context.MODE_PRIVATE).getBoolean(key, true);
    }

    public Float getFloat(final String key) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).getFloat(key, 0.0f);
    }

    public int getInt(final String key) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public long getLong(final String key) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).getLong(key, 0L);
    }

    public String getString(final String key) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    public boolean putBoolean(final String key, final boolean value) {
        return mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public boolean putFloat(final String key, final Float value) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putFloat(key, value).commit();
    }

    public boolean putInt(final String key, final int value) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public boolean putLong(final String key, final Long value) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
    }

    public boolean putString(final String key, final String value) {
        return this.mContext.getSharedPreferences(PrefsManager.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }


}
