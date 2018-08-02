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
     * 设置是否使用软加载  在onCreateView 内使用
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
        return mBaseFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        if (!isUserLazyLoad()) {
            onCreatPresenter();
            if (mPresenter != null)
                mPresenter.attachView((V) this);
            initData();
            initView();
            initServiceData();
        }
    }

    /**
     * 创建Presenter
     */
    public abstract void onCreatPresenter();

    /**
     * 设置显示内容  onViewCreated中使甩
     *
     * @param resourceId
     */
    public void setContentView(int resourceId) {
        mContentView = LayoutInflater.from(BaseApplication.sBaseApplication).inflate(resourceId, mBaseFlContent, false);
        if (mBaseFlContent != null)
            mBaseFlContent.addView(mContentView);
    }

    /**
     * 设置显示内容 onViewCreated中使甩
     *
     * @param view
     */
    public void setContentView(View view) {
        mContentView = view;
        if (mBaseFlContent != null)
            mBaseFlContent.addView(mContentView);
    }


    /**
     * 添加通用title onViewCreated中使甩
     *
     * @param layoutResID layoutid
     */
    public void addTitleView(int layoutResID) {
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(LayoutInflater.from(BaseApplication.sBaseApplication).inflate(layoutResID, mBaseAbl, false));
    }

    /**
     * 添加通用title onViewCreated中使甩
     *
     * @param view view
     */
    public void addTitleView(View view) {
        if (mBaseAbl.getChildCount() > 0)
            mBaseAbl.removeAllViews();
        mBaseAbl.addView(view);
    }

    /**
     * 设置数据为空或者网络异常界面 onViewCreated中使甩
     *
     * @param emptyResourceId
     * @param netResourceId
     */
    public void setDataEmptyView(int emptyResourceId, int netResourceId) {
        mDataEmptyView = LayoutInflater.from(BaseApplication.sBaseApplication).inflate(emptyResourceId, null, false);
        mNetExceptionView = LayoutInflater.from(BaseApplication.sBaseApplication).inflate(netResourceId, null, false);
    }

    /**
     * 设置数据为空或者网络异常界面 onViewCreated中使甩
     *
     * @param emptyView
     * @param netExceptionView
     */
    public void setEmptyOrNetExceptionView(View emptyView, View netExceptionView) {
        mDataEmptyView = emptyView;
        mNetExceptionView = netExceptionView;
    }


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
                initData();
                initView();
                initServiceData();
            }
        }
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
