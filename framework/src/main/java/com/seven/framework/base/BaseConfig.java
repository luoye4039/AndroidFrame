package com.seven.framework.base;

import android.os.Environment;

import com.seven.framework.utils.FileUtil;

import java.io.File;

public class BaseConfig {
    public boolean mIsInitConfig;

    public void initApp() {
        if (!mIsInitConfig) {
            mIsInitConfig = true;
            initAPPFilePath();
        }
    }

    public static void initAPPFilePath() {
        creatSaveFile();
    }

    //SD卡的路径
    public static String EXTERNALSTORAGE_PATH;
    //系统相册地址
    public static String SYSTEM_PHOTO_PATH;
    //带包名的文件路径
    public static String APP_BASE_PATH;
    //没有捕捉到的错误地址
    public static String ERROR_LOG;
    //APP图片sd卡地址
    public static String PICTURE_PATH;
    //APP音频文件地址
    public static String VOICE_PATH;
    //APP其他文件
    public static String OTHER_PATH;

    /**
     * 初始化保存文件的文件夹
     */
    public static void creatSaveFile() {
        String packageName = BaseApplication.sBaseApplication.getPackageName();
        String appName = packageName.substring(packageName.lastIndexOf(".") + 1);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //若SD卡权限允许
            EXTERNALSTORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
            SYSTEM_PHOTO_PATH = EXTERNALSTORAGE_PATH + "/DCIM/Camera/";

            File cacheDir = FileUtil.getOwnCacheDirectory(BaseApplication.sBaseApplication, appName + File.separator);
            APP_BASE_PATH = cacheDir.getAbsolutePath();
            VOICE_PATH = APP_BASE_PATH + "/audio/";
            ERROR_LOG = APP_BASE_PATH + "/log/";
            PICTURE_PATH = APP_BASE_PATH + "/image/";
            OTHER_PATH = APP_BASE_PATH + "/other/";
            creatFile();
        } else {
            //若SD卡不权限允许
            File cacheDir = FileUtil.getOwnCacheDirectory(BaseApplication.sBaseApplication, appName + File.separator);
            APP_BASE_PATH = cacheDir.getAbsolutePath();
            VOICE_PATH = APP_BASE_PATH + "/audio/";
            ERROR_LOG = APP_BASE_PATH + "/log/";
            PICTURE_PATH = APP_BASE_PATH + "/image/";
            OTHER_PATH = APP_BASE_PATH + "/other/";
            creatFile();
        }

    }

    private static void creatFile() {
        File baseFile = new File(BaseConfig.APP_BASE_PATH);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        File voiceFile = new File(BaseConfig.VOICE_PATH);
        if (!voiceFile.exists()) {
            voiceFile.mkdirs();
        }

        File errorLogFile = new File(BaseConfig.ERROR_LOG);
        if (!errorLogFile.exists()) {
            errorLogFile.mkdirs();
        }

        File photoFile = new File(BaseConfig.PICTURE_PATH);
        if (!photoFile.exists()) {
            photoFile.mkdirs();
        }

        File annexFile = new File(BaseConfig.OTHER_PATH);
        if (!annexFile.exists()) {
            annexFile.mkdirs();
        }
    }
}
