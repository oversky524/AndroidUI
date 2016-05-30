package io.base.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by gaochao on 2016/2/1.
 */
public class HorizontalDrawInterface implements HorizontalSeparator.DrawInterface {
    private int mPadding;
    private Paint mWhitePaint, mBgPaint;

    public HorizontalDrawInterface(int whiteColor, int bgColor, int padding){
        mPadding = padding;
        Paint paint = new Paint();
        paint.setColor(whiteColor);
        mWhitePaint = paint;
        paint = new Paint();
        paint.setColor(bgColor);
        mBgPaint = paint;
    }

    @Override
    public void draw(Canvas canvas, int left, int top, int right, int bottom) {
        canvas.drawRect(left, top, left + mPadding, bottom, mWhitePaint);
        canvas.drawRect(right - mPadding, top, right, bottom, mWhitePaint);
        canvas.drawRect(left + mPadding, top, right - mPadding, bottom, mBgPaint);
    }
}
