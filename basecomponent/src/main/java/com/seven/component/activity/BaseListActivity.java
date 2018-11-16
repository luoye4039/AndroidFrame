package com.seven.component.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.seven.component.R;
import com.seven.component.activity.presenter.BaseListPresenter;
import com.seven.component.activity.presenter.BaseListView;
import com.seven.framework.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListActivity<D> extends BaseActivity<BaseListView, BaseListPresenter<D>> implements BaseListView {
    private List<D> mDatas = new ArrayList<>();
    public SmartRefreshLayout mBaseListSrl;
    public RecyclerView mBaseListRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        mBaseListSrl = findViewById(R.id.base_list_srl);
        mBaseListRv = findViewById(R.id.base_list_rv);
        mBaseListRv.setAdapter(getAdapter());
        mBaseListSrl.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadMoreData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refreshData();
            }
        });
        mBaseListSrl.autoRefresh();
    }

    @Override
    public void initServiceData() {

    }

    public abstract BaseQuickAdapter getAdapter();

    @Override
    public void onSetDatas(List list) {
        mDatas.addAll(list);
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public void stopRefresh() {
        mBaseListSrl.finishRefresh();
    }

    @Override
    public void stopLoadMore() {
        mBaseListSrl.finishLoadMore();
    }
}
