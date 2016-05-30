package io.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by gaochao on 2016/2/18.
 */
public class TouchHideInputMethod {
    private Activity mActivity;
    private View[] mChildViews;
    private int[] mTempLocation = {0, 0};
    private RectF mTempRect = new RectF();

    public TouchHideInputMethod(Activity activity, View...children){
        mActivity = activity;
        mChildViews = children;
    }

    public void hideInputMethod(MotionEvent motionEvent){
        View[] childViews = mChildViews;
        int len = childViews.length;
        float rawx = motionEvent.getRawX();
        float rawy = motionEvent.getRawY();
        int[] tempLocation = mTempLocation;
        RectF tempRectf = mTempRect;
        boolean hide = true;
        for(int i=0; i<len; ++i){
            View child = childViews[i];
            child.getLocationOnScreen(tempLocation);
            int width = child.getWidth(), height = child.getHeight();
            int left = tempLocation[0], top = tempLocation[1], right = left + width, bottom = top + height;
            tempRectf.set(left, top, right, bottom);
            if(tempRectf.contains(rawx, rawy)){
                hide = false;
                break;
            }
        }
        if(hide){
            Activity activity = mActivity;
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View focus = activity.getCurrentFocus();
            if(focus == null) focus = activity.getWindow().getDecorView();
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }
}
