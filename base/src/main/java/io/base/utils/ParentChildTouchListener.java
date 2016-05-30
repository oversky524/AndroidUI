package io.base.utils;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by gaochao on 2015/10/14.
 * 在一个父view上点击，如果touch point不在指定的子view上则执行父view的OnClickListener，否则
 * 执行touch point所在子view的OnClickListener
 */
public class ParentChildTouchListener implements View.OnTouchListener {
    private View mParentV;
    private View[] mChildrenVs;
    private RectF mTempRectf = new RectF();

    public ParentChildTouchListener(View parent, View[] children){
        mParentV = parent;
        mChildrenVs = children;
    }

    public ParentChildTouchListener(View parent, View child){
        mParentV = parent;
        mChildrenVs = new View[]{child};
    }

    public ParentChildTouchListener(View parent, List<View> children){
        mParentV = parent;
        mChildrenVs = children.toArray(new View[children.size()]);
    }

    private boolean mPerformLongClick;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mPerformLongClick = false;
                Observable.timer(ViewConfiguration.getLongPressTimeout(), TimeUnit.MILLISECONDS)
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                mPerformLongClick = true;
                            }
                        });
                break;

            case MotionEvent.ACTION_UP:
                if(mPerformLongClick){
                    mParentV.performLongClick();
                }else {
                    int[] location = {0, 0};
                    float rawx = event.getRawX();
                    float rawy = event.getRawY();
                    RectF rectF = mTempRectf;
                    for (View child : mChildrenVs) {
                        child.getLocationOnScreen(location);
                        float left = location[0], top = location[1],
                                right = left + child.getWidth(), bottom = top + child.getHeight();
                        rectF.set(left, top, right, bottom);
                        if (rectF.contains(rawx, rawy)) {
                            if (!child.performClick()) {
                                throw new RuntimeException("Please call setOnClickListener!");
                            }
                            return true;
                        }
                    }
                    if (!mParentV.performClick()) {
                        throw new RuntimeException("Please call parent setOnClickListener!");
                    }
                }
                break;

            default:
                return false;
        }
        return true;
    }
}
