package com.seven.framework.view.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.seven.framework.R;
import com.seven.framework.widget.loadingindicatorview.AVLoadingIndicatorView;
import com.seven.framework.widget.loadingindicatorview.indicator.BaseIndicatorController;

/**
 * Created by wangbin on 2018/3/2.
 * 创建一个loading DialogFragment
 */

public class LoadingFdialog extends DialogFragment {

    private AVLoadingIndicatorView mMDialogLodingAliv;
    private BaseIndicatorController mBaseIndicatorController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.dialog_loading_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setWindowConfig();
        mMDialogLodingAliv = view.findViewById(R.id.dialog_loding_aliv);
        mMDialogLodingAliv.setIndicatorController(mBaseIndicatorController);
    }

    /**
     * 设置WindowConfig
     */
    private void setWindowConfig() {
        Window window = getDialog().getWindow();
        if (window != null)
            window.setBackgroundDrawableResource(R.color.transparent);
    }

    /**
     * 设置loading样式
     *
     * @param indicatorController 样式值
     */
    public void setLoaingStytle(BaseIndicatorController indicatorController) {
        mBaseIndicatorController = indicatorController;
    }
}
