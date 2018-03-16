package com.seven.framework.utils;

import android.graphics.drawable.AnimationDrawable;

/**
 * Created by wangbin on 2018/3/16.
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
