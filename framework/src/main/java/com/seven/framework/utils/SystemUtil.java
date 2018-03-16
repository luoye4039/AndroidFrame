package com.seven.framework.utils;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.iol8.framework.R;
import com.iol8.framework.base.BaseApplication;
import com.iol8.framework.bean.UserAddressListBean;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by wangbin on 2016/11/22.
 */

public class SystemUtil {

    /**
     * 检查包是否存在
     *
     * @param context context
     * @param pckName 包名
     * @return 是否存在
     */
    public static boolean isPackageExist(Context context, String pckName) {
        try {
            PackageInfo pckInfo = context.getPackageManager()
                    .getPackageInfo(pckName, 0);
            if (pckInfo != null)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            TLog.e(e.getMessage());
        }
        return false;
    }

    /**
     * 获得安装apk的intent
     *
     * @param file apk文件path
     * @return intent
     */
    public static Intent getInstallApkIntent(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 检查手机是否安装有app市场
     *
     * @param context context
     * @return 是否有
     */
    public static boolean isHaveMarket(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    /**
     * 打开指定报名app
     *
     * @param context     context
     * @param packageName 包名
     */
    public static void openApp(Context context, String packageName) {
        Intent mainIntent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (mainIntent == null) {
            mainIntent = new Intent(packageName);
        } else {
            TLog.i("Action:" + mainIntent.getAction());
        }
        context.startActivity(mainIntent);
    }

    /**
     * 打开指定报名app
     *
     * @param context      context
     * @param packageName  包名
     * @param activityName activity名（包含package）
     */
    public static boolean openAppActivity(Context context, String packageName,
                                          String activityName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, activityName);
        intent.setComponent(cn);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2qx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 让Gallery上能马上看到该图片
     */
    public static void scanPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.contains("IOLchannel")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String channleName;
        if (ret.contains("_")) {
            String[] split = ret.split("_");
            if (split.length >= 2) {
                channleName = ret.substring(split[0].length() + 1);
            } else {
                channleName = SystemUtil.getMeta(context, "TD_CHANNEL_ID");
            }
        } else {
            channleName = SystemUtil.getMeta(context, "TD_CHANNEL_ID");
        }
        return channleName;
    }

    public static String getMeta(Context context, String key) {
        return getMetaData(context, key);
    }

    private static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getString(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static String getProcessName(Context context) {
        String processName = null;
        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取当前进程ID
     *
     * @param context
     * @return 进程ID
     */
    public static int getProcessID(Context context) {
        return android.os.Process.myPid();
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     */
    public static void closeKey(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }


    /**
     * 隐藏软键盘
     *
     * @param context
     */
    public static void openKey(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 获得IP地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        } else {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {

            }
        }
        return null;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    public static void requestAudioFocus(Context context) {
        final AudioManager mAudioMgr = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = null;
        //Build.VERSION.SDK_INT表示当前SDK的版本，Build.VERSION_CODES.ECLAIR_MR1为SDK 7版本 ，
        //因为AudioManager.OnAudioFocusChangeListener在SDK8版本开始才有。
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //失去焦点之后的操作

                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        //获得焦点之后的操作
                    }
                    mAudioMgr.abandonAudioFocus(this);
                }
            };
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }

        if (mAudioMgr != null) {
            mAudioMgr.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }

    }

    /**
     * 获得当前系统语言
     *
     * @param context
     * @return
     */
    public static String getSystemLanguage(Context context) {
        Resources resources = context.getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        Locale systemLocale = config.locale;
        String local = systemLocale.toString();
        if (local.contains("zh")) {
            local = "zh_CN";
        } else if (local.contains("en")) {
            local = "en_US";
        }
        return local;
    }

    /**
     * 修改显示语言
     *
     * @param context
     */
    public static void switchLanguage(Context context) {
        BaseApplication baseApplication = (BaseApplication) context.getApplicationContext();
        String appLanguage = baseApplication.getAppLanguage();
        Resources resources = context.getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        if (appLanguage.contains("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
        } else if (appLanguage.contains("en")) {
            config.locale = Locale.US; //英语
        }
        resources.updateConfiguration(config, dm);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    public static void wakeAndUnlock(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();

    }

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    /**
     * 得到手机通讯录联系人信息
     **/
    public static ArrayList<UserAddressListBean> getPhoneContacts(Context context) {
        ArrayList<UserAddressListBean> userAddressListBeanArrayList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                phoneNumber = phoneNumber.replace(" ", "");
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                String fileByUri;
                if (photoid > 0) {
                   /* Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    FileUtil.writeToFile(AppConfig.PHOTO_PATH+);*/
                    fileByUri = "";
                } else {
                    fileByUri = "";
                }

                userAddressListBeanArrayList.add(new UserAddressListBean(contactid, contactName, phoneNumber, fileByUri));
            }
            phoneCursor.close();
            return userAddressListBeanArrayList;
        } else {
            return null;
        }
    }

    /**
     * 复制文本
     * @param context
     * @param text
     */
    public static void copyTextToClip(Context context, String text){
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip;
        myClip = ClipData.newPlainText("text", text);
        clipboardManager.setPrimaryClip(myClip);
        ToastUtil.showMessage(R.string.common_copy_tips);
    }

    /**
     * 得到手机SIM卡联系人人信息
     **/
    public static ArrayList<UserAddressListBean> getSIMContacts(Context context) {
        ArrayList<UserAddressListBean> userAddressListBeanArrayList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);

                //Sim卡中没有联系人头像
                userAddressListBeanArrayList.add(new UserAddressListBean(0, contactName, phoneNumber, ""));
            }
            phoneCursor.close();
            return userAddressListBeanArrayList;
        } else {
            return null;
        }
    }

    public static String[] getPhoneContacts(Context context, Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = context.getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone != null) {
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }


    /**
     * 当前界面是否为横屏显示
     * */
    public static boolean isLandspaceScreen(Context context){
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向
        if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
            //横屏
            return true;
        }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
            //竖屏
            return false;
        }
        return false;
    }

}
