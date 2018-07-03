package com.seven.framework.base.mvp;

/**
 * Created by wangbin on 2017/7/26.
 */

public class BasePresenter<V extends BaseView> {
    private V mView;

    public V getView() {
        return mView;
    }

    public void setView(V view) {
        mView = view;
    }

    public boolean isAttachView() {
        return mView != null;
    }

    public void attachView(V view) {
        mView = view;
    }

    public void detachView() {
        mView = null;
    }
}
