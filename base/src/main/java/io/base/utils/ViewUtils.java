package io.base.utils;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by gaochao on 2015/8/31.
 */
public class ViewUtils {
    private ViewUtils() {
    }

    /**
     * 根据触摸点查找子view
     * @param parent 父view
     * @param e
     * */
    public static View getChildView(ViewGroup parent, MotionEvent e){
        int x = (int)e.getX(), y = (int)e.getY();
        Rect rectF = new Rect();
        int size = parent.getChildCount();
        for(int i=0; i<size; ++i){
            View view = parent.getChildAt(i);
            view.getHitRect(rectF);
            if(rectF.contains(x, y)){
                return view;
            }
        }
        return null;
    }

    public static void setBackground(Drawable drawable, View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void removeBackgroundFromParentToTop(View view) {
        ViewParent vp = view.getParent();
        while (vp instanceof View) {
            setBackground(null, (View) vp);
            vp = ((View) vp).getParent();
        }
    }

    public static void modifyWidth(View view, int width){
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if(lp.width != width) {
            lp.width = width;
            view.setLayoutParams(lp);
        }
    }

    public static void modifyHeight(View view, int height){
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if(lp.height != height) {
            lp.height = height;
            view.setLayoutParams(lp);
        }
    }

    public static void modifyLayoutParams(View view, int width, int height){
        ViewGroup.LayoutParams mlp = view.getLayoutParams();
        if(mlp.width != width || mlp.height != height) {
            mlp.width = width;
            mlp.height = height;
            view.setLayoutParams(mlp);
        }
    }

    public static void modifyLayoutParams(View child, int width, int height, int leftMargin, int topMargin){
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)child.getLayoutParams();
        if(mlp.width != width || mlp.height != height || mlp.leftMargin != leftMargin || mlp.topMargin != topMargin) {
            mlp.width = width;
            mlp.height = height;
            mlp.leftMargin = leftMargin;
            mlp.topMargin = topMargin;
            child.setLayoutParams(mlp);
        }
    }

    /**
     * -1 no effect
     * */
    public static void modifyMargins(View view, int left, int top, int right, int bottom){
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        boolean set = false;
        if(-1 != left && mlp.leftMargin != left){
            mlp.leftMargin = left;
            set = true;
        }
        if(-1 != right && mlp.rightMargin != right) {
            mlp.leftMargin = left;
            set = true;
        }
        if(-1 != top && mlp.topMargin != top) {
            mlp.leftMargin = left;
            set = true;
        }
        if(-1 != bottom && mlp.bottomMargin != bottom) {
            mlp.leftMargin = left;
            set = true;
        }
        if(set) view.setLayoutParams(mlp);
    }

    /**
     * -1 no effect
     * */
    public static void modifyPaddings(View view, int left, int top, int right, int bottom){
        final int lp = left == -1 ? view.getPaddingLeft() : left;
        final int tp = top == -1 ? view.getPaddingTop() : top;
        final int rp = right == -1 ? view.getPaddingRight() : right;
        final int bp = bottom == -1 ? view.getPaddingBottom() : bottom;
        view.setPadding(lp, tp, rp, bp);
    }

    public static void modifyLeftMargin(View view, int leftMargin){
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        if(mlp.leftMargin != leftMargin){
            mlp.leftMargin = leftMargin;
            view.setLayoutParams(mlp);
        }
    }

    public static void modifyRightMargin(View view, int rightMargin){
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        if(mlp.rightMargin != rightMargin){
            mlp.rightMargin = rightMargin;
            view.setLayoutParams(mlp);
        }
    }

    /**
     * 在全屏模式下可能存在某些View会遮住我们的View，故需将它们隐藏
     * */
    public static void hideUselessChildren(View decorView){
        ViewGroup vg = (ViewGroup)decorView;
        int count = vg.getChildCount();
        for(int i=0; i<count; ++i){
            View child = vg.getChildAt(i);
            if(child.getClass().equals(View.class)){
                child.setVisibility(View.GONE);
            }
        }
    }

    public static void makeHeightFillParent(View view){
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if(lp.height != ViewGroup.LayoutParams.MATCH_PARENT){
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.setLayoutParams(lp);
        }
    }
}
