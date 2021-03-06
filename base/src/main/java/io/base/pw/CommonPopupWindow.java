package io.base.pw;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import io.base.utils.AndroidUtils;
import io.base.utils.CheckUtils;
import io.base.utils.ResourcesUtils;

/**
 * Created by gaochao on 2016/3/17.
 */
public class CommonPopupWindow implements View.OnTouchListener, View.OnKeyListener {
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mOnDismissListener == null){
                mPopupWindow.dismiss();
            }else{
                mOnDismissListener.onDismiss(mPopupWindow, true);
            }
            return true;
        }
        return false;
    }

    /**
     * 应该取消窗口时触发，但是并没有调用PopupWindow.dismiss
     * */
    public interface OnDismissListener{
        /**
         * @param clickingBackKey 表示点击了Back键
         * */
        void onDismiss(PopupWindow popupWindow, boolean clickingBackKey);
    }

    public void setOnDismissListener(OnDismissListener listener){ mOnDismissListener = listener; }

    private OnDismissListener mOnDismissListener;
    private PopupWindow mPopupWindow;
    private View mContentView;

    private CommonPopupWindow(Builder builder){
        Context context = builder.context;
        CheckUtils.checkNull(context, "Context has to be set");
        FrameLayout root = new FrameLayout(context);
        View content = builder.contentView;
        if(content == null){
            CheckUtils.checkState(builder.layoutId == 0, "one of ContentView and layoutId has to be set");
            content = LayoutInflater.from(context).inflate(builder.layoutId, root, false);
        }
        mContentView = content;

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        boolean focusable = builder.dismissClickingBackKey;
        int height = builder.height;
        if(height == 0) {
            height = dm.heightPixels;
            if (builder.considerStatusBar) height -= AndroidUtils.getStatusBarHeight();
        }
        int width = builder.width;
        if(width < 1) width = dm.widthPixels;
        PopupWindow popupWindow = new PopupWindow(root, width, height, focusable);
        mPopupWindow = popupWindow;

        popupWindow.setAnimationStyle(builder.animationStyle);
        popupWindow.setClippingEnabled(builder.clippingEnabled);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            popupWindow.setAttachedInDecor(builder.attachedInDecor);
        }

        root.addView(content);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)content.getLayoutParams();
        lp.gravity = builder.gravity;

        boolean dismissTouchingOutsideContent = builder.dismissTouchingOutsideContent;
        if(dismissTouchingOutsideContent){
            popupWindow.setTouchable(true);
            popupWindow.setTouchInterceptor(this);
        }

        if(focusable){
            popupWindow.setFocusable(true);
            content.setOnKeyListener(this);
        }

        Drawable bgDrawable = builder.bgDrawable;
        if(bgDrawable == null && (focusable || dismissTouchingOutsideContent)){
            bgDrawable = new ColorDrawable(Color.TRANSPARENT);
        }
        popupWindow.setBackgroundDrawable(bgDrawable);
    }

    public void show(View parent){
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void showAsDropDown(View anchorView){
        mPopupWindow.showAsDropDown(anchorView);
    }

    public View contentView(){ return mContentView; }

    public void dismiss(){ mPopupWindow.dismiss(); }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX(), y = event.getRawY();
        RectF rectF = new RectF();
        int location[] = {0, 0};
        View child = mContentView;
        child.getLocationOnScreen(location);
        int left = location[0], top = location[1], right = left + child.getWidth(),
                bottom = top + child.getHeight();
        rectF.set(left, top, right, bottom);
        if(!rectF.contains(x, y)){
            if(mOnDismissListener == null){
                mPopupWindow.dismiss();
            }else{
                mOnDismissListener.onDismiss(mPopupWindow, false);
            }
            return true;
        }
        return false;
    }

    public static class Builder{
        private int gravity = Gravity.CENTER;
        private int width, height;
        private int bgResId;//drawable/mipmap/color res id
        private Drawable bgDrawable;
        private boolean dismissTouchingOutsideContent = true;
        private boolean dismissClickingBackKey = true;
        private boolean clippingEnabled;
        private boolean attachedInDecor;
        private View contentView;
        private int layoutId;
        private int animationStyle;
        private Context context;
        private boolean considerStatusBar;

        private void setContext(Context context){
            this.context = context;
            if(bgResId != 0) bgDrawable = ResourcesUtils.getDrawable(context.getResources(), bgResId);
        }

        public Builder clippingEnabled(boolean enabled){
            clippingEnabled = enabled;
            return this;
        }

        public Builder animationStyle(int animationStyle){
            this.animationStyle = animationStyle;
            return this;
        }

        public Builder attachedInDecor(boolean enabled){
            attachedInDecor = enabled;
            return this;
        }

        public Builder contentView(View content){
            contentView = content;
            setContext(content.getContext());
            return this;
        }

        public Builder contentView(int layoutId, Context context){
            this.layoutId = layoutId;
            setContext(context);
            return this;
        }

        public Builder gravity(int gravity){
            this.gravity = gravity;
            return this;
        }

        public Builder width(int width){
            this.width = width;
            return this;
        }

        public Builder height(int height){
            this.height = height;
            return this;
        }

        public Builder size(int width, int height){
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder background(int bgResId){
            this.bgResId = bgResId;
            if(context != null) bgDrawable = ResourcesUtils.getDrawable(context.getResources(), bgResId);
            return this;
        }

        public Builder background(Drawable drawable){
            bgDrawable = drawable;
            return this;
        }

        public Builder dismissClickingBackKey(boolean dismiss){
            dismissClickingBackKey = dismiss;
            return this;
        }

        public Builder dismissTouchingOutsideContent(boolean dismiss){
            dismissTouchingOutsideContent = dismiss;
            return this;
        }

        public Builder considerStatusBar(boolean consider){
            considerStatusBar = consider;
            return this;
        }

        public CommonPopupWindow build(){
            return new CommonPopupWindow(this);
        }
    }
}
