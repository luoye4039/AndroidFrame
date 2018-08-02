package com.seven.framework.view.adapter;

import android.support.annotation.Nullable;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seven.framework.R;
import com.seven.framework.entity.SelectDialogBean;

import java.util.List;

public class SelectAdapter extends BaseQuickAdapter<SelectDialogBean, BaseViewHolder> {
    public SelectAdapter(int layoutResId, @Nullable List<SelectDialogBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectDialogBean item) {
        Button selectBt = helper.getView(R.id.select_bt);
        selectBt.setText(item.content);
        if (item.btBackGround > 0)
            selectBt.setBackgroundResource(item.btBackGround);
        if (item.textColor > 0)
            selectBt.setTextColor(item.textColor);
        if (item.textSize > 0)
            selectBt.setTextSize(item.textSize);
    }
}
