package com.seven.framework.utils;

import android.util.Log;

import com.seven.framework.BuildConfig;

public class LogUtils {
    public static final String LOG_TAG = "";

    public static void d(String log) {
        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, log);
    }

    public static void e(String log) {
        if (BuildConfig.DEBUG)
            Log.e(LOG_TAG, "" + log);
    }

    public static void i(String log) {
        if (BuildConfig.DEBUG)
            Log.i(LOG_TAG, log);
    }

    public static void w(String log) {
        if (BuildConfig.DEBUG)
            Log.w(LOG_TAG, log);
    }


    public static void d(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.d(tag, log);
    }

    public static void e(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.e(tag, "" + log);
    }

    public static void i(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.i(tag, log);
    }

    public static void w(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.w(tag, log);
    }
}
