package com.seven.framework.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class VRecyclerView extends RecyclerView {

    public LinearLayoutManager linearLayoutManager;


    public VRecyclerView(Context context) {
        this(context, null);
    }

    public VRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(linearLayoutManager);
    }
}
