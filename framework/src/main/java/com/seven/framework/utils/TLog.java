package com.seven.framework.utils;

import android.util.Log;

public class TLog {
    public static final String LOG_TAG = "iol8.me";
    public static boolean DEBUG = true;

    public static void d(String log) {
        if (DEBUG)
            Log.d(LOG_TAG, log);
    }

    public static void e(String log) {
        if (DEBUG)
            Log.e(LOG_TAG, "" + log);
    }

    public static void i(String log) {
        if (DEBUG)
            Log.i(LOG_TAG, log);
    }

    public static void w(String log) {
        if (DEBUG)
            Log.w(LOG_TAG, log);
    }


    public static void d(String tag, String log) {
        if (DEBUG)
            Log.d(tag, log);
    }

    public static void e(String tag, String log) {
        if (DEBUG)
            Log.e(tag, "" + log);
    }

    public static void i(String tag, String log) {
        if (DEBUG)
            Log.i(tag, log);
    }

    public static void w(String tag, String log) {
        if (DEBUG)
            Log.w(tag, log);
    }
}
