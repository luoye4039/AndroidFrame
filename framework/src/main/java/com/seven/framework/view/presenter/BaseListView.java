package com.seven.framework.view.presenter;

import com.seven.framework.base.mvp.BaseView;

import java.util.List;

public interface BaseListView extends BaseView {

    void  onSetDatas(List  list);

    void  stopRefresh();


    void  stopLoadMore();



}
