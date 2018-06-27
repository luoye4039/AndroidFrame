package com.seven.framework.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.seven.framework.R;
import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.base.mvp.BaseView;
import com.seven.framework.manager.AppManager;

import java.lang.ref.WeakReference;

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {
    public static String BUNDLE = "bundle";
    private WeakReference<Activity> mActivityWeakReference;
    public P mPresenter;
    private FrameLayout mBaseFlContent;
    private AppBarLayout mBaseAbl;
    private TitleView mTitleView;
    private View mDataEmptyView;//当数据为空显示View
    private View mNetExceptionView;//当网络异常显示View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityWeakReference = new WeakReference<Activity>(this);
        AppManager.getInstance().addActivity(mActivityWeakReference);
        onCreatPresenter();
        if (mPresenter != null)
            mPresenter.attachView((V) this);
    }

    private void findBaseView() {
        mBaseAbl = findViewById(R.id.base_abl);
        mBaseFlContent = findViewById(R.id.base_fl_content);
    }

    /**
     * 创建Presenter
     */
    public abstract void onCreatPresenter();


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        findBaseView();
        addTitleView(R.layout.include_title);
        findTitleView();
        addContentView(layoutResID);
    }

    /**
     * 添加通用title
     *
     * @param layoutResID layoutid
     */
    public void addTitleView(int layoutResID) {
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(LayoutInflater.from(getApplicationContext()).inflate(layoutResID, mBaseAbl, false));
    }

    /**
     * 添加通用title
     *
     * @param view view
     */
    public void addTitleView(View view) {
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(view);
    }

    /**
     * 获取默认Title的View
     */
    public void findTitleView() {
        mTitleView = new TitleView();
        mTitleView.titleLeftIvLeft = findViewById(R.id.title_left_iv_left);
        mTitleView.titleCenterTv = findViewById(R.id.title_center_tv);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (mTitleView != null && mTitleView.titleCenterTv != null) {
            mTitleView.titleCenterTv.setText(title);
        } else {
            findTitleView();
            mTitleView.titleCenterTv.setText(title);
        }
    }

    /**
     * 设置标题
     *
     * @param resourceId
     */
    public void setTitle(int resourceId) {
        if (mTitleView != null && mTitleView.titleCenterTv != null) {
            mTitleView.titleCenterTv.setText(resourceId);
        } else {
            findTitleView();
            mTitleView.titleCenterTv.setText(resourceId);
        }
    }


    /**
     * 隐藏title
     */
    public void hideTitleView() {
        mBaseAbl.setVisibility(View.GONE);
    }

    /**
     * 显示title
     */
    public void showTitleView() {
        mBaseAbl.setVisibility(View.VISIBLE);
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
     * 显示数据为空View
     */
    public void showEmptyDataView() {
        if (mDataEmptyView == null)
            return;
        if (mBaseFlContent.getChildCount() > 1) {
            for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                mBaseFlContent.removeViewAt(i);
            }
        }
        mBaseFlContent.addView(mDataEmptyView);
    }

    /**
     * 移除数据为空View
     */
    public void removeEmptyDataView() {
        if (mBaseFlContent.getChildCount() > 1) {
            for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                mBaseFlContent.removeViewAt(i);
            }
        }
    }


    /**
     * 显示网络异常View
     */
    public void showNetExceptionView() {
        if (mNetExceptionView == null)
            return;
        if (mBaseFlContent.getChildCount() > 1) {
            for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                mBaseFlContent.removeViewAt(i);
            }
        }
        mBaseFlContent.addView(mNetExceptionView);
    }

    /**
     * 移除数据为空View
     */
    public void removeNetExceptionView() {
        if (mBaseFlContent.getChildCount() > 1) {
            for (int i = 1; i < mBaseFlContent.getChildCount(); i++) {
                mBaseFlContent.removeViewAt(i);
            }
        }
    }


    /**
     * 用于初始化本地数据
     */
    public abstract void initData();

    /**
     * 用于findViewById
     */
    public abstract void initView();


    /**
     * 用于初始化网络数据
     */
    public void initServiceData() {

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

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish, boolean new_task) {
        Intent intent = new Intent(this, cls);
        if (new_task) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        if (isFinish) {
            finish();
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
        Intent intent = new Intent(this, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivity(intent);
        if (isFinish) {
            finish();
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
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            finish();
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
        Intent intent = new Intent(this, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            finish();
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
        Intent intent = new Intent(this, cls);
        if (new_task) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 返回跳转携带的数据Bundle
     *
     * @return
     */
    public Bundle getExtraBundle() {
        return getIntent().getBundleExtra(BUNDLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(mActivityWeakReference);
        if (mPresenter != null)
            mPresenter.detachView();
    }


    public static final class TitleView {
        public ImageView titleLeftIvLeft;
        public TextView titleCenterTv;

    }
}
