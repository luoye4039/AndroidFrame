package com.seven.framework.utils;

import android.content.Context;

import com.seven.framework.manager.AppManager;

import java.util.Timer;
import java.util.TimerTask;


/***
 * 双击退出
 *
 * @author tiancb
 */
public class DoubleClickExitUtil {
    public static long TIME = 2 * 1000;
    private static long sStartTimeMillis;

    private static DoubleClickExitUtil sDoubleClickExitUtil;

    private DoubleClickExitUtil() {
    }

    public static DoubleClickExitUtil getInstance() {
        if (sDoubleClickExitUtil == null) {
            synchronized (DoubleClickExitUtil.class) {
                if (sDoubleClickExitUtil == null)
                    sDoubleClickExitUtil = new DoubleClickExitUtil();
            }
        }
        return sDoubleClickExitUtil;
    }

    /**
     * 退出应用并关闭进程
     *
     * @param context               context
     * @param firstTips             第一次点击提示语
     * @param onDoubleClickListener 退出前回调
     * @return
     */
    public boolean extitKillProcess(Context context, String firstTips, OnDoubleClickListener onDoubleClickListener) {
        Timer timer = new Timer();
        if (sStartTimeMillis == 0) {
            ToastUtil.showMessage(firstTips);
            sStartTimeMillis = System.currentTimeMillis();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sStartTimeMillis = 0;
                }
            }, TIME);
            return true;
        } else {
            timer.cancel();
            onDoubleClickListener.onDoubleClick();
            AppManager.getInstance().AppExit(context);
            return true;
        }
    }

    /**
     * 退出应用并关闭所有界面
     *
     * @param context               context
     * @param firstTips             第一次点击提示语
     * @param onDoubleClickListener 退出前回调
     * @return
     */
    public static boolean extitFinishAllActiviy(Context context, String firstTips, OnDoubleClickListener onDoubleClickListener) {
        Timer timer = new Timer();
        if (sStartTimeMillis == 0) {
            ToastUtil.showMessage(firstTips);
            sStartTimeMillis = System.currentTimeMillis();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sStartTimeMillis = 0;
                }
            }, TIME);
            return true;
        } else {
            timer.cancel();
            onDoubleClickListener.onDoubleClick();
            AppManager.getInstance().AppExit(context);
            return true;
        }
    }


    public interface OnDoubleClickListener {
        void onDoubleClick();
    }

}
