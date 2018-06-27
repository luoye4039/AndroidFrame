package com.seven.framework.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by wangbin on 2018/3/14.
 * 判断手机品牌
 */

public class DeviceManufacturerUtil {
    public static final String SYS_EMUI = "sys_emui";
    public static final String SYS_MIUI = "sys_miui";
    public static final String SYS_FLYME = "sys_flyme";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";


    /**
     * 判断手机是否小米手机
     *
     * @return
     */
    public static boolean deviceIsMIUI() {
        String deviceManufacturer = getDeviceManufacturer();
        if (TextUtils.isEmpty(deviceManufacturer)) {
            return false;
        } else if (SYS_MIUI.equals(deviceManufacturer)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断手机是否魅族手机
     *
     * @return
     */
    public static boolean deviceIsFlyme() {
        String deviceManufacturer = getDeviceManufacturer();
        if (TextUtils.isEmpty(deviceManufacturer)) {
            return false;
        } else if (SYS_FLYME.equals(deviceManufacturer)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断手机是否华为，小米或魅族
     *
     * @return
     */
    private static String getDeviceManufacturer() {
        String SYS = null;
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                SYS = SYS_MIUI;//小米
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    || prop.getProperty(KEY_EMUI_VERSION, null) != null
                    || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                SYS = SYS_EMUI;//华为
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                SYS = SYS_FLYME;//魅族
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SYS;
    }

    /**
     * 获取魅族手机标签
     *
     * @return
     */
    private static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    /**
     * 获取系统数据
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 判断改手机是否是模拟器
     *
     * @param context
     * @return
     */
    public static boolean mayOnEmulator(Context context) {
        if (mayOnEmulatorViaBuild()) {
            return true;
        }
        if (mayOnEmulatorViaTelephonyDeviceId(context)) {
            return true;
        }
        if (mayOnEmulatorViaQEMU(context)) {
            return true;
        }
        return false;
    }

    private static boolean mayOnEmulatorViaBuild() {
        /**
         * ro.product.model likes sdk
         */
        if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().contains("sdk")) {
            return true;
        }

        /**
         * ro.product.manufacturer likes unknown
         */
        if (!TextUtils.isEmpty(Build.MANUFACTURER) && Build.MANUFACTURER.toLowerCase().contains("unknown")) {
            return true;
        }

        /**
         * ro.product.device likes generic
         */
        if (!TextUtils.isEmpty(Build.DEVICE) && Build.DEVICE.toLowerCase().contains("generic")) {
            return true;
        }

        return false;
    }

    private static boolean mayOnEmulatorViaTelephonyDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return false;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            return false;
        }
        /**
         * device id of telephony likes '0*'
         */
        for (int i = 0; i < deviceId.length(); i++) {
            if (deviceId.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    private static boolean mayOnEmulatorViaQEMU(Context context) {
        String qemu = getProp(context, "ro.kernel.qemu");
        return "1".equals(qemu);
    }

    private static String getProp(Context context, String property) {
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class);
            Object[] params = new Object[1];
            params[0] = property;
            return (String) method.invoke(SystemProperties, params);
        } catch (Exception e) {
            return null;
        }
    }
}
