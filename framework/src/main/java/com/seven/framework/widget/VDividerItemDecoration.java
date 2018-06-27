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
            outRect.top = space;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mDivider == null ||  parent.getLayoutManager().getChildCount() == 0) {
            return;
        }
        int left;
        int right;
        int top;
        int bottom;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //将有颜色的分割线处于中间位置
            float center = (parent.getLayoutManager().getTopDecorationHeight(child) - space) / 2;
            //计算下边的
            left = parent.getLayoutManager().getLeftDecorationWidth(child);
            right = parent.getWidth() - parent.getLayoutManager().getLeftDecorationWidth(child);
            top = (int) (child.getBottom() + params.bottomMargin + center);
            bottom = top + space;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }

    }
}