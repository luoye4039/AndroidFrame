package com.seven.framework.base;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.seven.framework.manager.AppManager;
import com.seven.framework.utils.ToastUtil;

import java.util.Locale;

/**
 * Base Application
 */

public class BaseApplication extends Application {
    public static AppManager sAppManager;
    public static Locale sAppLanguage = Locale.SIMPLIFIED_CHINESE;//app语言
    public static BaseApplication sBaseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        sAppManager = AppManager.getInstance();
        sBaseApplication = this;
    }

    /**
     * 设置是否捕获异常
     *
     * @param isOpenUncaughtException true 开启，false 关闭
     */
    public void setUncaughtExceptionCloseAPP(boolean isOpenUncaughtException) {
        if (isOpenUncaughtException) {
            Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        }
    }

    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ToastUtil.showLog("APP异常，即将关闭");
            AppManager.getInstance().AppExit(getApplicationContext());
        }
    }


}
