package com.seven.framework.net;


import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.entity.BaseHttpResponseBean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class HttpResponseObserable<T> implements Observer<BaseHttpResponseBean<T>> {


    private BasePresenter mBasePresenter;
    private Disposable mDisposable;

    public HttpResponseObserable() {
    }

    public HttpResponseObserable(BasePresenter basePresenter) {
        mBasePresenter = basePresenter;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
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
            }
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
            } else if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
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
