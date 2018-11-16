package com.seven.component.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.seven.component.R;
import com.seven.framework.base.BaseApplication;
import com.seven.framework.base.FrameworkFragment;
import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.base.mvp.BaseView;


/**
 * Created by wangbin on 2016/8/15.
 */
public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends FrameworkFragment implements BaseView {
    private boolean isViewCreated;
    private boolean isLoadData;
    public View mBaseFragmentView;//fragment 基础View
    private AppBarLayout mBaseAbl;//Fragment 公共头
    private FrameLayout mBaseFlContent;//实现显示View
    private View mContentView;//实现View
    private View mDataEmptyView;//当数据为空显示View
    private View mNetExceptionView;//当网络异常显示View
    public P mPresenter;


    /**
     * 当前是否使用软加载
     * 默认为开启状态，关闭复写此方法
     *
     * @return
     */
    protected boolean isUserLazyLoad() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
}
