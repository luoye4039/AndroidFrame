package com.seven.frame.frame;

import android.os.Bundle;
import android.view.View;

import com.seven.component.activity.BaseActivity;
import com.seven.component.dialog.CommonDialogFragment;
import com.seven.framework.base.FrameworkActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onCreatPresenter() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        View bt_1 = findViewById(R.id.bt_1);
        bt_1.setOnClickListener(this);
    }

    @Override
    public void initServiceData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1:
                CommonDialogFragment commonDialogFragment = new CommonDialogFragment();
                commonDialogFragment.show(getFragmentManager(), "test");
                break;

        }
    }
}
