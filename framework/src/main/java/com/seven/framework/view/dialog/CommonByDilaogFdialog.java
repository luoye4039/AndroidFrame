package com.seven.framework.view.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by wangbin on 2018/3/2.
 * 创建一个loading DialogFragment
 */

public class CommonByDilaogFdialog extends DialogFragment {
    private Dialog mDialog;

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog == null ? super.onCreateDialog(savedInstanceState) : mDialog;
    }
}
