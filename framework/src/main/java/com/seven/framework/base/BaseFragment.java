package com.seven.framework.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.seven.framework.R;
import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.base.mvp.BaseView;


/**
 * Created by wangbin on 2016/8/15.
 */
public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {
    public Activity mActivity;
    private boolean isViewCreated;
    private boolean isLoadData;
    public static String BUNDLE = "bundle";
    private static final String USER_LAZY_LOAD = "user_lazy_load";
    private boolean userLazyLoad;
    public View mBaseFragmentView;//fragment 基础View
    private AppBarLayout mBaseAbl;//Fragment 公共头
    private FrameLayout mBaseFlContent;//实现显示View
    private View mContentView;//实现View
    private View mDataEmptyView;//当数据为空显示View
    private View mNetExceptionView;//当网络异常显示View
    public P mPresenter;


    /**
     * 当前是否使用软加载
     *
     * @return
     */
    public boolean isUserLazyLoad() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.userLazyLoad = arguments.getBoolean(USER_LAZY_LOAD);
        }
        return userLazyLoad;
    }

    /**
     * 设置是否使用软加载
     *
     * @param userLazyLoad
     */
    public void setUserLazyLoad(boolean userLazyLoad) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            arguments = new Bundle();
            arguments.putBoolean(USER_LAZY_LOAD, userLazyLoad);
        } else {
            arguments.putBoolean(USER_LAZY_LOAD, userLazyLoad);
        }
        this.userLazyLoad = userLazyLoad;
        setArguments(arguments);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mBaseFragmentView = inflater.inflate(R.layout.fragment_base, container, false);
        mBaseAbl = mBaseFragmentView.findViewById(R.id.base_abl);
        mBaseFlContent = mBaseFragmentView.findViewById(R.id.base_fl_content);
        if (mContentView != null) {
            mBaseFlContent.addView(mContentView);
        }
        return mBaseFragmentView;
    }

    /**
     * 设置显示内容
     *
     * @param resourceId
     */
    public void setContentView(int resourceId) {
        if (mActivity == null)
            return;
        mContentView = LayoutInflater.from(mActivity.getApplicationContext()).inflate(resourceId, mBaseFlContent, false);
    }

    /**
     * 设置显示内容
     *
     * @param view
     */
    public void setContentView(View view) {
        mContentView = view;
    }


    /**
     * 添加通用title
     *
     * @param layoutResID layoutid
     */
    public void addTitleView(int layoutResID) {
        if (mActivity == null)
            return;
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(LayoutInflater.from(mActivity.getApplicationContext()).inflate(layoutResID, mBaseAbl, false));
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
     * 设置数据为空或者网络异常界面
     *
     * @param emptyResourceId
     * @param netResourceId
     */
    public void setDataEmptyView(int emptyResourceId, int netResourceId) {
        if (mActivity== null)
            return;
        mDataEmptyView = LayoutInflater.from(mActivity.getApplicationContext()).inflate(emptyResourceId, null, false);
        mNetExceptionView = LayoutInflater.from(mActivity.getApplicationContext()).inflate(netResourceId, null, false);
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        if (!isUserLazyLoad()) {
            onCreatPresenter();
            if (mPresenter != null)
                mPresenter.attachView((V) this);
        }
    }


    /**
     * 创建Presenter
     */
    public abstract void onCreatPresenter();

    /**
     * 用于初始化各种数据
     */
    public abstract void initData();

    /**
     * 用于findViewById
     */
    public abstract void initView();


    /**
     * 用于根据获得数据初始化view
     */
    public abstract void initServiceData();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isUserLazyLoad()) {
            if (getUserVisibleHint() && isViewCreated && !isLoadData) {
                isLoadData = true;
                onCreatPresenter();
                if (mPresenter != null)
                    mPresenter.attachView((V) this);
                lazyLoadData();
            }
        }
    }

    /**
     * 加载数据
     */
    public abstract void lazyLoadData();


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
     * 创建loading
     */
    public void createLoading() {

    }

    /**
     * 关闭loading
     */
    public void dimissLoading() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        if (mPresenter != null)
            mPresenter.detachView();
    }

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish) {
        Intent intent = new Intent(mActivity, cls);
        startActivity(intent);
        if (isFinish) {
            mActivity.finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param cls      跳转activity class name
     * @param isFinish 是否关闭当前activity
     */
    public void goActivity(Class<?> cls, Boolean isFinish, boolean new_task) {
        Intent intent = new Intent(mActivity, cls);
        if (new_task) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivity(intent);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            mActivity.finish();
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
        Intent intent = new Intent(mActivity, cls);
        if (new_task) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
        if (isFinish) {
            mActivity.finish();
        }
    }
}
