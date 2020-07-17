package com.encapital.io.copybackground;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static SharedPreferences mSharedPref;
    public static final String PERIOD_KEY = "PERIOD";
    public static final String SCRIPT_COPY = "SCRIPT_COPY";

    private MySharedPreferences()
    {

    }


    public static int getPeriod() {
        return read(MySharedPreferences.PERIOD_KEY, 5);
    }

    public static void setPeriod(int period) {
        write(MySharedPreferences.PERIOD_KEY, period);
    }

    public static String  getScriptCopy() {
        return read(MySharedPreferences.SCRIPT_COPY, "[]");
    }

    public static void setScriptCopy(String scriptCopy) {
        write(MySharedPreferences.SCRIPT_COPY, scriptCopy);
    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}

