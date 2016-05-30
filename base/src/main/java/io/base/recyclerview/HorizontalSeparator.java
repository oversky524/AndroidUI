package io.base.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gaochao on 2015/11/11.
 */
public class HorizontalSeparator<T> extends RecyclerView.ItemDecoration {
    private int mSeparatorHeight;
    private int mPaddingLeft, mPaddingRight;
    private Paint mPaint = new Paint();
    private RecyclerViewAdapter<T> mAdapter;
    private DrawInterface mDrawInterface;

    public interface DrawInterface{
        void draw(Canvas canvas, int left, int top, int right, int bottom);
    }
    public void setDrawInterface(DrawInterface drawInterface){ mDrawInterface = drawInterface; }

    public HorizontalSeparator(int color, int height, RecyclerViewAdapter<T> adapter){
        this(color, height, 0, 0, adapter);
    }

    public HorizontalSeparator(int height, DrawInterface drawInterface, RecyclerViewAdapter<T> adapter){
        mSeparatorHeight = height;
        mDrawInterface = drawInterface;
        mAdapter = adapter;
    }

    public HorizontalSeparator(int color, int height, int paddingLeft, int paddingRight, RecyclerViewAdapter<T> adapter){
        mSeparatorHeight = height;
        mPaint.setColor(color);
        mPaddingLeft = paddingLeft;
        mPaddingRight = paddingRight;
        mAdapter = adapter;
    }

    public HorizontalSeparator(int color, int height, int horizontalPadding, RecyclerViewAdapter<T> adapter){
        this(color, height, horizontalPadding, horizontalPadding, adapter);
    }

    public HorizontalSeparator(int color, int height){
        this(color, height, null);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int size = parent.getChildCount() - 1;
        final int paddingLeft = mPaddingLeft, paddingRight = mPaddingRight;
        final DrawInterface drawInterface = mDrawInterface;
        final boolean draw = drawInterface != null;
        for (int i = 0; i < size; ++i) {
            View child = parent.getChildAt(i);
            RecyclerViewAdapter.ViewInfo viewInfo = mAdapter == null ? null : mAdapter.getViewInfo(child);
            if(viewInfo == null || viewInfo.mDrawDividerBelowView) {
                int left = child.getLeft(), top = child.getBottom(), right = left + child.getWidth(),
                        bottom = top + mSeparatorHeight;
                if(draw){
                    drawInterface.draw(c, left, top, right, bottom);
                }else{
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerViewAdapter.ViewInfo viewInfo = mAdapter == null ? null : mAdapter.getViewInfo(view);
        int top = 0, bottom = 0;
        if(viewInfo == null){
            bottom = mSeparatorHeight;
        }else {
            if (viewInfo.mDrawDividerBelowView) {
                bottom = mSeparatorHeight;
            }
            if (viewInfo.mDrawDividerAboveView) {
                top = mSeparatorHeight;
            }
        }
        outRect.set(0, top, 0, bottom);
    }
}
