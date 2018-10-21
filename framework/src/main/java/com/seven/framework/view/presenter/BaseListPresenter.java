package com.seven.framework.view.presenter;

import com.seven.framework.base.mvp.BasePresenter;
import com.seven.framework.entity.BaseHttpResponseBean;
import com.seven.framework.entity.BaseListDataBean;
import com.seven.framework.net.HttpResponseObserable;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseListPresenter<D> extends BasePresenter<BaseListView> {

    public int pages;

    public abstract Observable<BaseHttpResponseBean<BaseListDataBean<D>>> getListData();

    /**
     * 刷新数据
     */
    public void refreshData() {
        getListData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResponseObserable<BaseListDataBean<D>>(BaseListPresenter.this) {
                    @Override
                    public void onSuccess(BaseListDataBean<D> tBaseListDataBean) {
                        List list = tBaseListDataBean.data.list;
                        if (list != null && list.size() > 0) {
                            getView().onSetDatas(list);
                        } else {
                            getView().setEmptyDataViewVisiable(true);
                        }
                        getView().stopRefresh();

                    }

                    @Override
                    public void onServiceEx(BaseHttpResponseBean baseHttpResponseBean) {

                    }

                    @Override
                    public void onFail(Throwable e) {

                    }
                });
    }

    /**
     * 加载更多数据
     */
    public void loadMoreData() {
        getListData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResponseObserable<BaseListDataBean<D>>(BaseListPresenter.this) {
                    @Override
                    public void onSuccess(BaseListDataBean<D> tBaseListDataBean) {
                        List list = tBaseListDataBean.data.list;
                        if (list != null && list.size() > 0) {
                            getView().onSetDatas(list);
                        }
                        getView().stopLoadMore();

                    }

                    @Override
                    public void onServiceEx(BaseHttpResponseBean baseHttpResponseBean) {

                    }

                    @Override
                    public void onFail(Throwable e) {

                    }
                });

    }


}
