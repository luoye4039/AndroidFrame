package com.seven.framework.base;

import android.support.multidex.MultiDexApplication;

import com.seven.framework.manager.AppManager;
import com.seven.framework.utils.DateUtil;
import com.seven.framework.utils.ToastUtil;

import java.io.File;
import java.io.PrintStream;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Allen on 2017/12/13.
 */

public class BaseApplication extends MultiDexApplication {
    private static final int LAST_EXCEPTION_FILE_NUM = 100;//本地最大错误log存储数量
    private static final String UNCAUGHT_EXCEPTION_FILE_NAME_SUFFIX = ".log";//本地错误log存储格式
    public static AppManager sAppManager;
    public static Locale sAppLanguage =Locale.SIMPLIFIED_CHINESE;//app语言
    public static BaseApplication sBaseApplication;
    public static String sChannelName;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppManager = AppManager.getInstance();
        sBaseApplication = this;
    }

    /**
     * 设置是否捕获异常
     *
     * @param isOpenUncaughtException
     */
    public void setUncaughtExceptionHandler(boolean isOpenUncaughtException) {
        if (isOpenUncaughtException) {
            clearExceptionFileData();
            Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        }
    }

    /**
     * 清理本地错误日志
     */
    public void clearExceptionFileData() {
        File file = new File(BaseConfig.ERROR_LOG);
        File[] files = file.listFiles();
        if (files != null && files.length > LAST_EXCEPTION_FILE_NUM) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    File file = new File(BaseConfig.ERROR_LOG);
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File exceptionFile : files) {
                            exceptionFile.delete();
                        }
                    }
                    e.onNext(true);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        //当有未捕获的异常的调用这个方法
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ToastUtil.showLog("APP异常，即将关闭");
            BaseApplication.this.uncaughtException(ex);
            Observable.just(ex).map(new Function<Throwable, String>() {
                @Override
                public String apply(Throwable throwable) throws Exception {
                    File file = new File(BaseConfig.ERROR_LOG + File.separator + DateUtil.formLocalTime("yyyy_MM_ddHH_mm_ss", System.currentTimeMillis()) + UNCAUGHT_EXCEPTION_FILE_NAME_SUFFIX);
                    if (!file.exists())
                        throwable.printStackTrace(new PrintStream(file));
                    return file.getAbsolutePath();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            AppManager.getInstance().AppExit(getApplicationContext());
                        }
                    });
        }
    }


    public void uncaughtException(Throwable throwable) {

    }

}
