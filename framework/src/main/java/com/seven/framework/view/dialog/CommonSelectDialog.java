package com.seven.framework.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.seven.framework.R;
import com.seven.framework.base.SelectDialogBean;
import com.seven.framework.utils.DeviceInfo;
import com.seven.framework.view.adapter.SelectAdapter;
import com.seven.framework.widget.VDividerItemDecoration;
import com.seven.framework.widget.VRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommonSelectDialog extends Dialog {

    private VRecyclerView mSelectDialogVrv;
    private OnClickListener mOnClickListener;

    public OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public CommonSelectDialog(Context context) {
        this(context, R.style.TransparentBgDialog);
    }

    private CommonSelectDialog(Context context, int theme) {
        super(context, theme);
        initDialog(context);
    }

    private void initDialog(Context context) {
        // 制定主题
        View dialogView = View.inflate(context, R.layout.dialog_common_select, null);
        setContentView(dialogView);
        mSelectDialogVrv = findViewById(R.id.select_dialog_vrv);
        LayoutParams layoutParams = getWindow().getAttributes();
        // 指定高度
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        // 指定对齐方法
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = (DeviceInfo.getDisplayMetrics(context).widthPixels - DeviceInfo.dp2qx(context, 40));
        // 在点击对话框外部，可以让对话框消失
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }


    public void setData(@NonNull String[] strings) {
        ArrayList<SelectDialogBean> selectBeans = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            selectBeans.add(new SelectDialogBean(i, strings[i]));
        }
        setData(selectBeans);
    }

    public void setData(final List<SelectDialogBean> selectBeanList) {
        SelectAdapter selectFileAdapter = new SelectAdapter(R.layout.item_select_dialog, selectBeanList);
        mSelectDialogVrv.setAdapter(selectFileAdapter);
        mSelectDialogVrv.addItemDecoration(new VDividerItemDecoration(DeviceInfo.dp2qx(getContext(), 10)));
        selectFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dismiss();
                if (mOnClickListener != null)
                    mOnClickListener.onClickItem(view, position, selectBeanList.get(position));
            }
        });
    }


    public interface OnClickListener {
        void onClickItem(View view, int position, SelectDialogBean selectBean);
    }
}
