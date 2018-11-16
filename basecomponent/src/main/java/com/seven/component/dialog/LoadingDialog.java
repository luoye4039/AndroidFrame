package com.seven.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.seven.component.R;


public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        this(context, R.style.TransparentBgDialog);
    }

    // 动画效果，出现和消失 位移动画
    // 布局，全屏宽度、包裹内容高度、顶部对齐
    // 背景：灰的，点击背景对话框消失
    private LoadingDialog(Context context, boolean cancelable) {
        this(context, R.style.TransparentBgDialog);
        setCancelable(cancelable);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        initDialog(context);
    }

    // 动画效果，出现和消失 位移动画
    // 布局，全屏宽度、包裹内容高度、顶部对齐
    // 背景：灰的，点击背景对话框消失
    public LoadingDialog(Context context, boolean cancelable,
                          OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initDialog(context);
    }

    private void initDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        setContentView(dialogView);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        // 指定高度
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 指定对齐方法
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // 在点击对话框外部，可以让对话框消失
        setCanceledOnTouchOutside(false);
    }
}
