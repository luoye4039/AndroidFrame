package com.seven.framework.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取设备信息
 * Created by wangbin on 2016/11/21.
 */

public class DeviceInfo {
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dp2qx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
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
     * 得到设备MD5唯一识别码
     *
     * @param context
     * @return
     */
    public static String getUniqueNumMd5(Context context) {
        String udid = FrameUtils.getStrToMD5(Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return TextUtils.isEmpty(udid) ? "" : udid;
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
    public static String getDeviceSystemVersion() {
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
     * 得到SIM序列号
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
    @SuppressLint("HardwareIds")
    public static String getWifiMACAddress(Context context) {
        String macAddress = "No Wifi Device";
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            WifiInfo connectionInfo = wm.getConnectionInfo();
            if (connectionInfo != null)
                macAddress = connectionInfo.getMacAddress();
        }
        return macAddress;
    }


    /**
     * 获得IP地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                return FrameUtils.intToIp(ipAddress);
            } else {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface
                            .getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf
                                .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException ex) {
                    LogUtils.e(ex.toString());
                }
            }
        }
        return "";
    }


    /**
     * 获得手机屏幕宽高和dp比例
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
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
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Display defaultDisplay = ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();
        defaultDisplay.getRealMetrics(displaymetrics);
        size[0] = displaymetrics.widthPixels;
        size[1] = displaymetrics.heightPixels;
        return size;
    }

    /**
     * 判断是否有物理菜单键
     *
     * @param context
     * @return
     */
    public static boolean hasHardwareMenuKey(Context context) {
        return ViewConfiguration.get(context).hasPermanentMenuKey();
    }

    /**
     * 获得Statusbar的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取底部 navigation bar 高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }


    /**
     * 判断手机是否有相机
     *
     * @param context
     * @return
     */
    public static boolean hasCamera(Context context) {
        boolean _hasCamera;
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
}
