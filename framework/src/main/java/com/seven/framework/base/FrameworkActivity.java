package com.seven.framework.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.githang.statusbar.StatusBarCompat;
import com.seven.framework.inter.OnBackPressedListener;

import java.util.List;

public abstract class FrameworkActivity extends AppCompatActivity {
    public static String BUNDLE = "bundle";

    /**
     * 设置状态栏颜色
     *
     * @param color          颜色值
     * @param lightStatusBar 是否高亮
     */
    public void setStatusBarColor(@ColorInt int color, boolean lightStatusBar) {
        StatusBarCompat.setStatusBarColor(this, color, lightStatusBar);
    }

    /**
     * 设置状态颜色
     *
     * @param color 颜色值
     */
    public void setStatusBarColor(@ColorInt int color) {
        StatusBarCompat.setStatusBarColor(this, color);
    }

    /**
     * 用于初始化本地数据
     */
    public abstract void initData();

    /**
     * 用于findViewById
     */
    public abstract void initView();


    /**
     * 用于初始化网络数据
     */
    public abstract void initServiceData();

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish, boolean newTask) {
        Intent intent = new Intent(this, cls);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }


    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param bundle   携带数据
     * @param isFinish 是否关闭当前activity
     */

    public void goActivity(Class<?> cls, Bundle bundle, Boolean isFinish) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls         跳转activity class name
     * @param isFinish    是否关闭当前activity
     * @param requestCode 请求码
     */
    public void goActivity(Class<?> cls, Boolean isFinish, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls         跳转activity class name
     * @param bundle      携带数据
     * @param isFinish    是否关闭当前activity
     * @param requestCode 请求码
     */
    public void goActivity(Class<?> cls, Bundle bundle, Boolean isFinish, int requestCode) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls         跳转activity class name
     * @param bundle      携带数据
     * @param isFinish    是否关闭当前activity
     * @param requestCode 请求码
     * @param newTask     是否开启新的栈
     */
    public void goActivity(Class<?> cls, Bundle bundle, Boolean isFinish, int requestCode, boolean newTask) {
        Intent intent = new Intent(this, cls);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 返回跳转携带的数据Bundle
     *
     * @return
     */
    public Bundle getExtraBundle() {
        return getIntent().getBundleExtra(BUNDLE);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof OnBackPressedListener)
                    ((OnBackPressedListener) fragment).onBackPressed();
            }
        }
        super.onBackPressed();
    }
}
