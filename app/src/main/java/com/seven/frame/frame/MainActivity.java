package com.seven.frame.frame;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.seven.framework.view.dialog.LoadingFdialog;
import com.seven.framework.widget.loadingindicatorview.indicator.BallGridPulseIndicator;
import com.seven.framework.widget.loadingindicatorview.indicator.PacmanIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configuration cf=getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
        int ori = cf.orientation ; //获取屏幕方向
        if(ori == cf.ORIENTATION_LANDSCAPE){
            //横屏

        }else if(ori == cf.ORIENTATION_PORTRAIT){
            //竖屏
        }
       /* LoadingFdialog loadingFdialog = new LoadingFdialog();
        loadingFdialog.setCancelable(false);
        loadingFdialog.setLoaingStytle(new PacmanIndicator());
        loadingFdialog.show(getFragmentManager(),"Floading");*/
    }
}
