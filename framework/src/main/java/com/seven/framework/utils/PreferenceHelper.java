package com.seven.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangbin on 2016/9/6.
 */
public class PreferenceHelper {
    public static void write(Context context, String fileName, String k, int v) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(k, v);
        editor.apply();
    }

    public static void write(Context context, String fileName, String k, boolean v) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(k, v);
        editor.apply();
    }

    public static void write(Context context, String fileName, String k, String v) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(k, v);
        editor.apply();
    }

    public static int readInt(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getInt(k, 0);
    }

    public static int readInt(Context context, String fileName, String k, int defv) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getInt(k, defv);
    }

    public static boolean readBoolean(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getBoolean(k, false);
    }

    public static boolean readBoolean(Context context, String fileName, String k, boolean defBool) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getBoolean(k, defBool);
    }

    public static String readString(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getString(k, (String) null);
    }

    public static String readString(Context context, String fileName, String k, String defV) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getString(k, defV);
    }

    public static void remove(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.remove(k);
        editor.apply();
    }

    public static void clean(Context cxt, String fileName) {
        SharedPreferences preference = cxt.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.apply();
    }

    public static void write(Context context, String fileName, String k, long v) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.putLong(k, v);
        editor.apply();
    }

    public static long readLong(Context context, String fileName, String k) {
        SharedPreferences preference = context.getSharedPreferences(fileName, 0);
        return preference.getLong(k, 0);
    }
}
