package com.seven.framework.utils;

import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 常用View处理工具
 */

public class ViewUtils {
    /**
     * textView 设置view
     */
    public static void setTextViewDrawableLeft(TextView view,
                                               int resId, int paddingPx) {
        Drawable drawable = ContextCompat.getDrawable(view.getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        view.setCompoundDrawables(drawable, null, null, null);
        view.setCompoundDrawablePadding(paddingPx);
    }
    /**
     * textView 设置view
     */
    public static void setTextViewDrawableTop(TextView view,
                                              int resId, int paddingPx) {
        Drawable drawable = ContextCompat.getDrawable(view.getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        view.setCompoundDrawables(null, drawable, null, null);
        view.setCompoundDrawablePadding(paddingPx);
    }
    /**
     * textView 设置view
     */
    public static void setTextViewDrawableRight(TextView view,
                                                int resId, int paddingPx) {
        Drawable drawable = ContextCompat.getDrawable(view.getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        view.setCompoundDrawables(null, null, drawable, null);
        view.setCompoundDrawablePadding(paddingPx);
    }
    /**
     * textView 设置view
     */
    public static void setTextViewDrawableBottom(TextView view,
                                                 int resId, int paddingPx) {
        Drawable drawable = ContextCompat.getDrawable(view.getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        view.setCompoundDrawables(null, null, null, drawable);
        view.setCompoundDrawablePadding(paddingPx);
    }
    /**
     * textView 设置没有View
     */
    public static void setTextViewNoDrawable(TextView view) {
        view.setCompoundDrawables(null, null, null, null);
    }

    public static void setTextViewShadeTopToBottom(TextView tv, int startColor, int endColor) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, 0,
                tv.getTextSize(),
                startColor,
                endColor,
                Shader.TileMode.CLAMP);
        tv.getPaint().setShader(mLinearGradient);
    }

    /**
     * 测量能够画在指定宽度内字符串的字体大小
     *
     * @param text        文本内容
     * @param width       指定的宽度
     * @param maxTextSize 文本字体最大
     * @return
     */

    public static float measureTextSize(String text, int width, float maxTextSize) {
        if (TextUtils.isEmpty(text)) {
            return maxTextSize;
        }
        float textSize = maxTextSize;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        float textWidth = paint.measureText(text);

        while (textWidth > width) {
            textSize--;
            paint.setTextSize(textSize);
            textWidth = paint.measureText(text);
        }
        return textSize;
    }

    /**
     * 密码明文显示开关
     *
     * @param nEditText
     * @param isChecked
     */
    public static void EditPwdShow_Switch(EditText nEditText, boolean isChecked) {
        if (isChecked) {
            nEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            nEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        int len = nEditText.getText().toString().length();
        if (len != 0) {
            nEditText.setSelection(len);
        }
    }

    private static long lastClickTime;
    private static final int MIN_CLICK_DELAY_TIME = 1000;

    /**
     * 检查是否双击
     *
     * @return
     */
    public synchronized static boolean isDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = time;
            return false;
        } else {
            return true;
        }
    }
}
