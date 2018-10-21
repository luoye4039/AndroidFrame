package com.seven.framework.net;


import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.entity.BaseHttpResponseBean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class HttpResponseObserable<T> implements Observer<BaseHttpResponseBean<T>> {
    private BasePresenter mBasePresenter;
    private Disposable mDisposable;
    private boolean mAutoShowLoading;

    public HttpResponseObserable() {
    }

    /**
     * 用于解绑
     *
     * @param basePresenter
     */
    public HttpResponseObserable(BasePresenter basePresenter) {
        mBasePresenter = basePresenter;
    }

    /**
     * 用于解绑 显示或隐藏loading
     *
     * @param basePresenter
     * @param autoShowLoading 是否自动显示loading
     */
    public HttpResponseObserable(BasePresenter basePresenter, boolean autoShowLoading) {
        mBasePresenter = basePresenter;
        this.mAutoShowLoading = autoShowLoading;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if (mBasePresenter != null && mAutoShowLoading && mBasePresenter.isAttachView())
            mBasePresenter.getView().creatLoading();
    }

    @Override
    public void onNext(BaseHttpResponseBean<T> baseHttpResponseBean) {
        if (mBasePresenter != null) {
            if (mBasePresenter.isAttachView()) {
                if (baseHttpResponseBean.result == BaseHttpResponseBean.SUCCESS) {
                    onSuccess(baseHttpResponseBean.data);
                } else {
                    onServiceEx(baseHttpResponseBean);
                }
            } else if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
                mDisposable = null;
            }
            if (mAutoShowLoading)
                mBasePresenter.getView().dimissLoading();
        } else {
            if (baseHttpResponseBean.result == BaseHttpResponseBean.SUCCESS) {
                onSuccess(baseHttpResponseBean.data);
            } else {
                onServiceEx(baseHttpResponseBean);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mBasePresenter != null) {
            if (mBasePresenter.isAttachView()) {
                onFail(e);
                if (mAutoShowLoading)
                    mBasePresenter.getView().dimissLoading();
            } else if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
                mDisposable = null;
            }
        } else {
            onFail(e);
        }
    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(T t);

    public abstract void onServiceEx(BaseHttpResponseBean baseHttpResponseBean);

    public abstract void onFail(Throwable e);


}
