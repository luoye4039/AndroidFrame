package com.seven.framework.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 获取设备信息
 * Created by wangbin on 2016/11/21.
 */

public class DeviceInfo {

    /**
     * 获得手机屏幕宽高和dp比例
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    /**
     * app在屏幕中的宽高
     *
     * @param context
     * @return
     */
    public static int[] getAppScreenSize(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    /**
     * 获得屏幕的真实尺寸
     *
     * @param context
     * @return
     */
    public static int[] getRealScreenSize(Context context) {
        int[] size = new int[2];
        int screenWidth = 0, screenHeight = 0;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Display defaultDisplay = ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();
        defaultDisplay.getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth")
                        .invoke(defaultDisplay);
                screenHeight = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(defaultDisplay);
            } catch (Exception ignored) {
            }
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(defaultDisplay,
                        realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
            }
        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;
    }

    /**
     * 获得Statusbar的高度
     *
     * @param context
     * @return
     */

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context.getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 得到设备唯一识别码
     *
     * @param context
     * @return
     */
    public static String getUniqueNumber(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获得手机型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获得系统版本号
     *
     * @return
     */
    public static String getDeviceVersionCode() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 得到设备IMEI值
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "please apply permission";
        }
        return tm != null ? tm.getDeviceId() : "";
    }

    /**
     * 得到设备序列号
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getSimSerialNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "please apply permission";
        }
        return tm != null ? tm.getSimSerialNumber() : null;
    }

    /**
     * 得到手机号码
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "please apply permission";
        }
        return tm != null ? tm.getLine1Number() : null;
    }

    /**
     * 获取设备本身网卡的MAC地址
     *
     * @param context
     * @return
     */
    public static String getWifiMACAddress(Context context) {
        String macAddress = "";
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        if (info != null) {
            macAddress = info.getMacAddress();
        } else {
            macAddress = "No Wifi Device";
        }
        return macAddress;
    }


    /**
     * 判断手机是否有相机
     *
     * @param context
     * @return
     */
    public static boolean hasCamera(Context context) {
        Boolean _hasCamera;
        PackageManager pckMgr = context
                .getPackageManager();
        boolean flag = pckMgr
                .hasSystemFeature("android.hardware.camera.front");
        boolean flag1 = pckMgr.hasSystemFeature("android.hardware.camera");
        boolean flag2;
        if (flag || flag1)
            flag2 = true;
        else
            flag2 = false;
        _hasCamera = flag2;
        return _hasCamera;
    }

    /**
     * 判断是否有物理菜单键
     *
     * @param context
     * @return
     */
    public static boolean hasHardwareMenuKey(Context context) {
        boolean GTE_ICS = Build.VERSION.SDK_INT >= 14;
        boolean PRE_HC = Build.VERSION.SDK_INT < 11;
        boolean flag;
        if (PRE_HC)
            flag = true;
        else if (GTE_ICS) {
            flag = ViewConfiguration.get(context).hasPermanentMenuKey();
        } else
            flag = false;
        return flag;
    }

}
