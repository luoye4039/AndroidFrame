package com.seven.framework.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wangbin on 2016-5-16.
 */
public class HDividerItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private ColorDrawable mDivider;

    public HDividerItemDecoration(int space) {
        this.space = space;
    }

    public HDividerItemDecoration(int color, int space) {
        if (color != 0) {
            mDivider = new ColorDrawable(color);
        }
        this.space = space;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) > 0)
            outRect.right = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        if (mDivider == null || childCount == 0) {
            return;
        }
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            int left;
            int right;
            left = child.getRight();
            right = child.getRight() + space;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}