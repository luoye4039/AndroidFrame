package com.seven.framework.widget.floatnotifycation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.seven.framework.R;
import com.seven.framework.utils.DeviceInfo;
import com.seven.framework.utils.ToastUtil;


/**
 * Created by wangbin on 2017/8/31.
 */

public class FloatNotifycation {
    public final static int REQUEST_FOLAT_PREMISSION = 0x25;
    private static FloatNotifycation sFloatNotifycation;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private WindowManager windowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private boolean isShowing;
    private boolean hasCountDowmTimer;
    private ShowTimeRunnable mShowTimeRunnable;

    public boolean isShowing() {
        return isShowing;
    }

    private FloatNotifycation() {
    }

    public static FloatNotifycation getInstance() {
        if (sFloatNotifycation == null) {
            synchronized (FloatNotifycation.class) {
                if (sFloatNotifycation == null) {
                    sFloatNotifycation = new FloatNotifycation();
                }
            }
        }
        return sFloatNotifycation;
    }


    private void init(Context context) {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //设置图片格式，效果为背景透明
            mLayoutParams.format = PixelFormat.RGBA_8888;
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 悬浮窗开始在左上角显示
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        }
    }

    /**
     * @param context
     */
    public void showNotifycation(Context context, View view, int showTime) {
        init(context);
        if (checkPermission(context)) {
            mView = view;
            if (showTime > 0) {
                hasCountDowmTimer = true;
                mShowTimeRunnable = new ShowTimeRunnable();
                mHandler.postDelayed(mShowTimeRunnable, showTime * 1000);
                addView();
            } else {
                addView();
                hasCountDowmTimer = false;
            }

            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

        } else {
            ToastUtil.showMessage(R.string.please_open_float_view_permission);
        }
    }

    /**
     * @param context
     */
    public void showNotifycation(Context context, View view, int showTime, int width, int height) {
        init(context);
        if (checkPermission(context)) {
            mView = view;
            mLayoutParams.height = height;
            mLayoutParams.width = width;
            if (showTime > 0) {
                hasCountDowmTimer = true;
                mShowTimeRunnable = new ShowTimeRunnable();
                mHandler.postDelayed(mShowTimeRunnable, showTime * 1000);
                addView();
            } else {
                addView();
                hasCountDowmTimer = false;
            }
        } else {
            ToastUtil.showMessage(R.string.please_open_float_view_permission);
        }
    }

    /**
     * 检测是否有权限
     *
     * @param context
     * @return
     */
    public static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            if (DeviceInfo.deviceIsMIUI() || DeviceInfo.deviceIsFlyme()) {
                return MeizuMiuiFloatViewPermission.isMiuiFloatWindowOpAllowed(context);
            } else {
                return true;
            }
        }
    }

    /**
     * 打开悬浮框权限
     *
     * @param activity
     */
    public static void openFloatPermmiss(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_FOLAT_PREMISSION);
        } else {
            if (DeviceInfo.deviceIsMIUI()) {
                MeizuMiuiFloatViewPermission.openMiuiPermissionActivity(activity, REQUEST_FOLAT_PREMISSION);
            } else {
                try {
                    Intent intent = new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(),
                            null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                } catch (Exception e) {
                    ToastUtil.showMessage(R.string.please_open_float_view_permission);
                }
            }
        }
    }

    /**
     * 添加view
     */
    private void addView() {
        if (mView != null) {
            try {
                windowManager.addView(mView, mLayoutParams);
                isShowing = true;
            } catch (Exception e) {
                mView = null;
                if (hasCountDowmTimer && mShowTimeRunnable != null) {
                    mHandler.removeCallbacks(mShowTimeRunnable);
                    hasCountDowmTimer = false;
                }
                ToastUtil.showMessage(R.string.please_open_float_view_permission);
            }
        }
    }


    public void removeView() {
        if (mView != null && windowManager != null) {
            isShowing = false;
            if (hasCountDowmTimer && mShowTimeRunnable != null) {
                mHandler.removeCallbacks(mShowTimeRunnable);
                mShowTimeRunnable = null;
            }
            try {
                windowManager.removeView(mView);
                mView = null;
            } catch (Exception e) {

            }
        }
    }


    private class ShowTimeRunnable implements Runnable {
        @Override
        public void run() {
            mShowTimeRunnable = null;
            hasCountDowmTimer = false;
            removeView();
        }
    }
}
