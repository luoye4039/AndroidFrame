package com.seven.framework.base.mvp;

/**
 * Created by wangbin on 2017/7/26.
 */

public interface BaseView {

    void creatLoading();

    void dimissLoading();

    void showEmptyDataView();

    void removeEmptyDataView();

    void showNetExceptionView();

    void removeNetExceptionView();
}
