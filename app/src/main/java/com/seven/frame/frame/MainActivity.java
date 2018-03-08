package com.seven.frame.frame;

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
        LoadingFdialog loadingFdialog = new LoadingFdialog();
        loadingFdialog.setCancelable(false);
        loadingFdialog.setLoaingStytle(new PacmanIndicator());
        loadingFdialog.show(getFragmentManager(),"Floading");
    }
}
