package com.seven.component.activity;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.seven.component.R;
import com.seven.framework.base.FrameworkActivity;
import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.base.mvp.BaseView;

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends FrameworkActivity implements BaseView {
    public P mPresenter;
    private FrameLayout mBaseFlContent;
    private AppBarLayout mBaseAbl;
    protected TitleView mTitleView;
    private View mDataEmptyView;//当数据为空显示View
    private View mNetExceptionView;//当网络异常显示View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreatPresenter();
        if (mPresenter != null)
            mPresenter.attachView((V) this);
    }

    /**
     * 创建Presenter
     */
    public abstract void onCreatPresenter();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        findBaseView();
        setTitleView(R.layout.include_title);
        addContentView(layoutResID);
    }

    /**
     * 设置状态栏颜色
     * @param color 颜色值
     * @param lightStatusBar  是否高亮
     */
    public void setStatusBarColor(@ColorInt int color, boolean lightStatusBar) {
        StatusBarCompat.setStatusBarColor(this, color, lightStatusBar);
    }

    /**
     * 设置状态颜色
     * @param color  颜色值
     */
    public void setStatusBarColor(@ColorInt int color) {
        StatusBarCompat.setStatusBarColor(this, color);
    }


    /**
     * base根View
     */
    private void findBaseView() {
        mBaseAbl = findViewById(R.id.base_abl);
        mBaseFlContent = findViewById(R.id.base_fl_content);
    }


    /**
     * 添加通用title
     *
     * @param layoutResID layoutid
     */
    public void setTitleView(int layoutResID) {
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(LayoutInflater.from(getApplicationContext()).inflate(layoutResID, mBaseAbl, false));
        if (R.layout.include_title == layoutResID) {
            findTitleView();
        }
    }

    /**
     * 添加通用title
     *
     * @param view view
     */
    public void setTitleView(View view) {
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(view);
        if (mTitleView != null) {
            mTitleView.titleLeftIvLeft = null;
            mTitleView.titleLeftTv = null;
            mTitleView.titleLeftIvRight = null;
            mTitleView.titleCenterIvLeft = null;
            mTitleView.titleCenterTv = null;
            mTitleView.titleCenterIvRight = null;
            mTitleView.titleRightIvLeft = null;
            mTitleView.titleRightTv = null;
            mTitleView.titleRightIvRight = null;
        }
        mTitleView = null;
    }

    /**
     * 获取默认Title的View
     */
    private void findTitleView() {
        mTitleView = new TitleView();
        mTitleView.titleLeftIvLeft = findViewById(R.id.title_left_iv_left);
        mTitleView.titleLeftIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTitleView.titleLeftTv = findViewById(R.id.title_left_tv);
        mTitleView.titleLeftIvRight = findViewById(R.id.title_left_iv_right);

        mTitleView.titleCenterIvLeft = findViewById(R.id.title_center_iv_left);
        mTitleView.titleCenterTv = findViewById(R.id.title_center_tv);
        mTitleView.titleCenterIvRight = findViewById(R.id.title_center_iv_right);

        mTitleView.titleRightIvLeft = findViewById(R.id.title_right_iv_left);
        mTitleView.titleRightTv = findViewById(R.id.title_right_tv);
        mTitleView.titleRightIvRight = findViewById(R.id.title_right_iv_right);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (mTitleView != null && mTitleView.titleCenterTv != null)
            mTitleView.titleCenterTv.setText(title);
    }

    /**
     * 设置标题
     *
     * @param resourceId
     */
    public void setTitle(int resourceId) {
        if (mTitleView != null && mTitleView.titleCenterTv != null)
            mTitleView.titleCenterTv.setText(resourceId);
    }

    /**
     * 设置title是否可见
     *
     * @param visibility
     */
    public void setTitleViewVisiable(int visibility) {
        mBaseAbl.setVisibility(visibility);
    }

    /**
     * 添加内容
     *
     * @param layoutResID
     */
    private void addContentView(int layoutResID) {
        mBaseFlContent.addView(LayoutInflater.from(getApplicationContext()).inflate(layoutResID, mBaseFlContent, false));
    }

    /**
     * 设置数据为空或者网络异常界面
     *
     * @param emptyResourceId
     * @param netResourceId
     */
    public void setDataEmptyView(int emptyResourceId, int netResourceId) {
        mDataEmptyView = LayoutInflater.from(getApplicationContext()).inflate(emptyResourceId, null, false);
        mNetExceptionView = LayoutInflater.from(getApplicationContext()).inflate(netResourceId, null, false);
    }

    /**
     * 设置数据为空或者网络异常界面
     *
     * @param emptyView
     * @param netExceptionView
     */
    public void setEmptyOrNetExceptionView(View emptyView, View netExceptionView) {
        mDataEmptyView = emptyView;
        mNetExceptionView = netExceptionView;
    }

    /**
     * 设置是否显示空界面
     */
    @Override
    public void setEmptyDataViewVisiable(boolean visiable) {
        if (mDataEmptyView == null)
            return;
        if (visiable) {
            if (mBaseFlContent.getChildCount() > 1) {
                for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                    mBaseFlContent.removeViewAt(i);
                }
            }
            mBaseFlContent.addView(mDataEmptyView);
        } else {
            if (mBaseFlContent.getChildCount() > 1) {
                for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                    mBaseFlContent.removeViewAt(i);
                }
            }
        }
    }

    /**
     * 设置是否显示网络异常界面
     */
    @Override
    public void setNetExceptionViewVisiable(boolean visiable) {
        if (mNetExceptionView == null)
            return;
        if (visiable) {
            if (mBaseFlContent.getChildCount() > 1) {
                for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                    mBaseFlContent.removeViewAt(i);
                }
            }
            mBaseFlContent.addView(mNetExceptionView);
        } else {
            if (mBaseFlContent.getChildCount() > 1) {
                for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                    mBaseFlContent.removeViewAt(i);
                }
            }
        }
    }


    /**
     * 创建loading
     */
    @Override
    public void creatLoading() {

    }

    /**
     * 隐藏loading
     */
    @Override
    public void dimissLoading() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
    }

    public static final class TitleView {
        public ImageView titleLeftIvLeft;
        public TextView titleLeftTv;
        public ImageView titleLeftIvRight;

        public ImageView titleCenterIvLeft;
        public TextView titleCenterTv;
        public ImageView titleCenterIvRight;

        public ImageView titleRightIvLeft;
        public TextView titleRightTv;
        public ImageView titleRightIvRight;
    }
}
