package com.seven.framework.utils;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.transition.TransitionManager;
import android.view.ViewGroup;

/**
 * 动画工具类
 */

public class AnimationUtil {
    /**
     * 获得动画总共时间
     *
     * @param animationDrawable
     * @return
     */
    public static int getTotalDuration(AnimationDrawable animationDrawable) {
        int iDuration = 0;
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
            iDuration += animationDrawable.getDuration(i);
        }
        return iDuration;
    }



}
