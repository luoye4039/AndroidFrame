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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seven.framework.R;
import com.seven.framework.utils.DeviceInfo;
import com.seven.framework.utils.SystemUtil;


public class CommonDialog extends Dialog implements View.OnClickListener {


    private DialogClickListener mDialogClickListener;
    private ImageView mCommonIvContentTop;
    private TextView mCommonTvTitle;
    private TextView mCommonTvContent;
    private TextView mCommonTvDialogLeft;
    private View mCommonViewDialogLine;
    private TextView mCommonTvDialogRight;
    private boolean mIsActionDimiss = true;//点击按钮后是否直接关闭
    private RelativeLayout mCommonRlAdd;
    private TextView mCommonTvContent2;
    private LinearLayout mCommonLl;

    public boolean isActionDimiss() {
        return mIsActionDimiss;
    }

    public void setActionDimiss(boolean actionDimiss) {
        mIsActionDimiss = actionDimiss;
    }

    public CommonDialog(Context context) {
        this(context, R.style.TransparentBgDialog);
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
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        setContentView(dialogView);
        mCommonIvContentTop = (ImageView) findViewById(R.id.common_iv_content_top);
        mCommonLl = (LinearLayout) findViewById(R.id.common_ll);
        mCommonTvTitle = (TextView) findViewById(R.id.common_tv_title);
        mCommonTvContent = (TextView) findViewById(R.id.common_tv_content);
        mCommonTvContent2 = (TextView) findViewById(R.id.common_tv_content2);
        mCommonRlAdd = findViewById(R.id.common_rl_add);
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
        setCancelable(true);
    }

    public void setTopIcon(int iconId) {
        if (iconId != 0) {
            mCommonIvContentTop.setVisibility(View.VISIBLE);
            mCommonIvContentTop.setImageResource(iconId);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCommonLl.getLayoutParams();
            layoutParams.setMargins(DeviceInfo.dp2qx(getContext(), 20), DeviceInfo.dp2qx(getContext(), 80), DeviceInfo.dp2qx(getContext(), 20), 0);
            mCommonLl.setLayoutParams(layoutParams);
        }
    }

    public void setContent(String title, String content, String leftTv, String rigthTv) {
        if (TextUtils.isEmpty(title)) {
            mCommonTvTitle.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mCommonTvContent.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            mCommonTvContent.setLayoutParams(layoutParams);
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

    public void setContent2(String conten2) {
        mCommonTvContent2.setVisibility(View.VISIBLE);
        mCommonTvContent2.setText(conten2);
    }

    public void setContentTextColor(int contentTextColor) {
        mCommonTvContent.setTextColor(contentTextColor);
    }

    public void setContentTextSize(int contentTextSize) {
        mCommonTvContent.setTextSize(contentTextSize);
    }

    public void setContentView(int size, String color) {
        if (size > 0)
            mCommonTvContent.setTextSize(size);
        if (!TextUtils.isEmpty(color))
            mCommonTvContent.setTextColor(Color.parseColor(color));
    }


    public void addView(View view) {
        mCommonRlAdd.removeAllViews();
        mCommonRlAdd.addView(view);
        mCommonRlAdd.setVisibility(View.VISIBLE);
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


    public static final class Builder {
        private Context mContext;
        private String title;
        private String content;
        private String left;
        private String right;
        private int topIcon;
        private DialogClickListener mDialogClickListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(int titleId) {
            this.title = mContext.getString(titleId);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(int contentId) {
            this.content = mContext.getString(contentId);
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setLeft(String left) {
            this.left = left;
            return this;
        }

        public Builder setLeft(int leftId) {
            this.left = mContext.getString(leftId);
            return this;
        }

        public Builder setRight(int rightId) {
            this.right = mContext.getString(rightId);
            return this;
        }

        public Builder setRight(String right) {
            this.right = right;
            return this;
        }

        public Builder setTopIcon(int topIcon) {
            this.topIcon = topIcon;
            return this;
        }

        public Builder setDialogClickListener(DialogClickListener dialogClickListener) {
            mDialogClickListener = dialogClickListener;
            return this;
        }

        public CommonDialog build() {
            CommonDialog commonDialog = new CommonDialog(mContext);
            commonDialog.setTopIcon(topIcon);
            commonDialog.setContent(title, content, left, right);
            commonDialog.setDialogClickListener(mDialogClickListener);
            return commonDialog;
        }
    }
}
