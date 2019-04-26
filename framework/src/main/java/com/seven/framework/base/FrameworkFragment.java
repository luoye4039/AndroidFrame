package com.seven.framework.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.seven.framework.inter.OnBackPressedListener;


/**
 * Created by wangbin on 2016/8/15.
 */
public abstract class FrameworkFragment extends Fragment implements OnBackPressedListener {
    public Activity mActivity;
    public static String BUNDLE = "bundle";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    /**
     * 用于初始化各种数据
     */
    public abstract void initData();

    /**
     * 用于findViewById
     */
    public abstract void initView();

    /**
     * 用于根据获得数据初始化view
     */
    public abstract void initServiceData();

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish) {
        Intent intent = new Intent(mActivity, cls);
        startActivity(intent);
        if (isFinish) {
            mActivity.finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish, boolean new_task) {
        Intent intent = new Intent(mActivity, cls);
        if (new_task) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivity(intent);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            mActivity.finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls         跳转activity class name
     * @param bundle      携带数据
     * @param isFinish    是否关闭当前activity
     * @param requestCode 请求码
     * @param new_task    是否开启新的栈
     */
    public void goActivity(Class<?> cls, Bundle bundle, Boolean isFinish, int requestCode, boolean new_task) {
        Intent intent = new Intent(mActivity, cls);
        if (new_task) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            mActivity.finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
