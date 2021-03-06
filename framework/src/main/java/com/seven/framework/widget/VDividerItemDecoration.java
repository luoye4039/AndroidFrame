package com.seven.framework.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wangbin on 2016-5-16.
 */
public class VDividerItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private ColorDrawable mDivider;

    public VDividerItemDecoration(int space) {
        this.space = space;
    }

    public VDividerItemDecoration(int color, int space) {
        if (color != 0) {
            mDivider = new ColorDrawable(color);
        }
        this.space = space;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) > 0)
            outRect.bottom = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        if (mDivider == null || childCount == 0) {
            return;
        }
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            int top = child.getBottom();
            int bottom = child.getBottom() + space;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}