package com.seven.framework.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.seven.framework.manager.AppManager;
import com.seven.framework.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Base Application
 */

public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static AppManager sAppManager;
    public static Locale sAppLanguage = Locale.SIMPLIFIED_CHINESE;//app语言
    public static BaseApplication sBaseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        sAppManager = AppManager.getInstance();
        sBaseApplication = this;
        //注册所有activity生命周期监听
        registerActivityLifecycleCallbacks(this);
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

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        AppManager.getInstance().addActivity(new WeakReference<Activity>(activity));
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.getInstance().removeActivity(activity);
    }

    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ToastUtil.showLog("APP异常，即将关闭");
            AppManager.getInstance().AppExit(getApplicationContext());
        }
    }


}
