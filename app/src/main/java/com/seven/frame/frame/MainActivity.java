package com.seven.frame.frame;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.seven.component.activity.BaseActivity;
import com.seven.component.dialog.CommonDialogFragment;
import com.seven.framework.base.FrameworkActivity;
import com.seven.framework.manager.AppNotifycationManager;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppNotifycationManager.getInstance().init(getApplication());
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
        View bt_2 = findViewById(R.id.bt_2);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
    }

    @Override
    public void initServiceData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1:
                goActivity(Main2Activity.class, false);
                break;
            case R.id.bt_2:
                TextView textView = new TextView(this);
                textView.setText("测试下哈哈");
                textView.setTextSize(50);
                textView.setBackgroundColor(Color.BLUE);
                AppNotifycationManager.getInstance().addShowView(textView);
                break;

        }
    }
}
