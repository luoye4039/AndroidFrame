package com.seven.framework.manager.uploadordown;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class OnDownFileObserver implements Observer<DownFileBean> {


    public abstract void onSuccess(DownFileBean t);

    public abstract void onDownProgress(long readLength, long contentLength, int percent);

    public abstract void onFail(Throwable throwable);


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(DownFileBean downFileBean) {
        if (downFileBean.isCompleted) {
            onSuccess(downFileBean);
        } else {
            onDownProgress(downFileBean.downLength, downFileBean.totalLength, downFileBean.downPercent);
        }
    }

    @Override
    public void onError(Throwable e) {
        onFail(e);
    }

    @Override
    public void onComplete() {

    }
}
