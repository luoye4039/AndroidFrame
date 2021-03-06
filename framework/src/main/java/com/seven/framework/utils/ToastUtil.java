/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.seven.framework.utils;

import android.widget.Toast;

import com.seven.framework.base.BaseApplication;


/**
 * Toast工具类
 */
public class ToastUtil {
    public static boolean isShow;

    /**
     * 文本toast
     *
     * @param msg 内容
     */
    public static void showMessage(String msg) {
        showMess(msg, Toast.LENGTH_SHORT);
    }

    /**
     * 文本toast
     *
     * @param msg  内容
     * @param time 显示时间
     */
    public static void showMessage(String msg, int time) {
        showMess(msg, time);
    }

    /**
     * log toast
     *
     * @param msg 内容
     */
    public static void showLog(String msg) {
        if (isShow)
            showMess(msg, Toast.LENGTH_SHORT);
    }

    private static void showMess(String msg, int time) {
        Toast.makeText(BaseApplication.sBaseApplication, msg, time).show();
    }

    /**
     * 文本toast
     *
     * @param msg 内容资源id
     */
    public static void showMessage(int msg) {
        showMess(msg, Toast.LENGTH_SHORT);
    }

    /**
     * 文本toast
     *
     * @param msg  内容资源id
     * @param time 显示时间
     */
    public static void showMessage(int msg, int time) {
        showMess(msg, time);
    }

    /**
     * log toast
     *
     * @param msg 内容
     */
    public static void showLog(int msg) {
        if (isShow)
            showMess(msg, Toast.LENGTH_SHORT);
    }

    private static void showMess(int msg, int time) {
        Toast.makeText(BaseApplication.sBaseApplication, msg, time).show();
    }
}
