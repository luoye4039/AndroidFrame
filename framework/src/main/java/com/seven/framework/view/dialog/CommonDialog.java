package com.seven.framework.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;



public class CommonDialog extends Dialog implements View.OnClickListener {


    private DialogClickListener mDialogClickListener;
    private ImageView mCommonIvContentTop;
    private TextView mCommonTvTitle;
    private TextView mCommonTvContent;
    private TextView mCommonTvDialogLeft;
    private View mCommonViewDialogLine;
    private TextView mCommonTvDialogRight;
    private boolean mIsActionDimiss = true;//点击按钮后是否直接关闭

    public boolean isActionDimiss() {
        return mIsActionDimiss;
    }

    public void setActionDimiss(boolean actionDimiss) {
        mIsActionDimiss = actionDimiss;
    }

    public CommonDialog(Context context) {
        this(context, R.style.middleDialog);
    }

    public CommonDialog(Context context, int theme) {
        super(context, theme);
        initDialog(context);
    }

    // 动画效果，出现和消失 位移动画
    // 布局，全屏宽度、包裹内容高度、顶部对齐
    // 背景：灰的，点击背景对话框消失
    private CommonDialog(Context context, boolean cancelable,
                         OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_me_common, null);
        setContentView(dialogView);
        mCommonIvContentTop = (ImageView) findViewById(R.id.common_iv_content_top);
        mCommonTvTitle = (TextView) findViewById(R.id.common_tv_title);
        mCommonTvContent = (TextView) findViewById(R.id.common_tv_content);
        mCommonTvDialogLeft = (TextView) findViewById(R.id.common_tv_dialog_left);
        mCommonTvDialogLeft.setOnClickListener(this);
        mCommonViewDialogLine = (View) findViewById(R.id.common_view_dialog_line);
        mCommonTvDialogRight = (TextView) findViewById(R.id.common_tv_dialog_right);
        mCommonTvDialogRight.setOnClickListener(this);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        // 指定高度
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 指定对齐方法
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = (int) (DeviceInfo.getDisplayMetrics(context).widthPixels * 0.75);

        // 在点击对话框外部，可以让对话框消失
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setTopIcon(int iconId) {
        mCommonIvContentTop.setImageResource(iconId);
    }

    public void setContent(String title, String content, String leftTv, String rigthTv) {
        if (TextUtils.isEmpty(title)) {
            mCommonTvTitle.setVisibility(View.GONE);
        } else {
            mCommonTvTitle.setText(title);
        }

        if (TextUtils.isEmpty(content)) {
            mCommonTvContent.setVisibility(View.GONE);
        } else {
            mCommonTvContent.setText(content);
        }

        if (TextUtils.isEmpty(leftTv)) {
            mCommonTvDialogLeft.setVisibility(View.GONE);
            mCommonViewDialogLine.setVisibility(View.GONE);
        } else {
            mCommonTvDialogLeft.setText(leftTv);
        }
        if (TextUtils.isEmpty(rigthTv)) {
            mCommonTvDialogRight.setVisibility(View.GONE);
            mCommonViewDialogLine.setVisibility(View.GONE);
        } else {
            mCommonTvDialogRight.setText(rigthTv);
        }
    }

    public void setContentView(int size, String color) {
        if (size > 0)
            mCommonTvContent.setTextSize(size);
        if (!TextUtils.isEmpty(color))
            mCommonTvContent.setTextColor(Color.parseColor(color));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.common_tv_dialog_left) {
            if (mIsActionDimiss)
                dismiss();
            if (mDialogClickListener != null)
                mDialogClickListener.clickLeft(this);
        } else if (id == R.id.common_tv_dialog_right) {
            if (mIsActionDimiss)
                dismiss();
            if (mDialogClickListener != null)
                mDialogClickListener.clickRight(this);
        }
    }

    public interface DialogClickListener {
        void clickLeft(Dialog dialog);

        void clickRight(Dialog dialog);
    }

    public void setDialogClickListener(DialogClickListener dialogClickListener) {
        mDialogClickListener = dialogClickListener;
    }

}
