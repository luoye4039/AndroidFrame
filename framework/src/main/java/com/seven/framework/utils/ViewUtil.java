package com.seven.framework.utils;

import android.graphics.Paint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

/**
 * View util
 * Created by wangbin on 2018/3/14.
 */

public class ViewUtil {

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

}
