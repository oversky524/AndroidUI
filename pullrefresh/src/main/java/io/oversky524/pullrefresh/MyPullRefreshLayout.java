package io.oversky524.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import io.base.ScrollTouchHelper;

/**
 * Created by gaochao on 2016/7/20.
 */
public class MyPullRefreshLayout extends FrameLayout implements ScrollTouchHelper.ScrollTouchCallback {
    private static final boolean DEBUG = true;
    private static final String TAG = MyPullRefreshLayout.class.getSimpleName();
    private static final int DEFAULT_ENDING_ANIMATION_DURATION = 500;//unit: ms

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyPullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyPullRefreshLayout(Context context) {
        super(context);
    }

    public MyPullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mTouchHelper.onInterceptTouchEvent(ev) ? true : super.onInterceptTouchEvent(ev);
        if(DEBUG) Log.v(TAG, "onInterceptTouchEvent=" + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mRefreshing || mTouchHelper.onTouchEvent(event);
        if(DEBUG) Log.v(TAG, "onTouchEvent=" + result);
        return result;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() > 1){
            throw new RuntimeException("Can only one child view!");
        }
        mTargetView = getChildAt(0);
        mTouchHelper = new ScrollTouchHelper(this, this);
    }

    private ChildCanContinuePullingListener mPullingListener;
    private ScrollTouchHelper mTouchHelper;
    private OnPullRefreshListener mRefreshingListener;
    private View mTargetView;
    private View mRefreshingView;
    private float mInitY;
    private boolean mRefreshing;//swallow events when refreshing
    private int mEndingAnimationDuration = DEFAULT_ENDING_ANIMATION_DURATION;

    public void setEndingAnimationDuration(int duration){
        mEndingAnimationDuration = duration;
    }

    public View getTargetView(){
        return mTargetView;
    }

    public void setOnPullRefreshListener(OnPullRefreshListener listener){
        mRefreshingListener = listener;

        mRefreshingView = listener.getRefreshView(this);
        addView(mRefreshingView, 0);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                View child = mRefreshingView;
                final int height = child.getHeight();
                final float y = child.getY();
                mInitY = y;
                child.setY(y - height);
                mTargetView.setY(child.getY() + height);
                return true;
            }
        });
    }

    public void setChildCanContinuePullingListener(ChildCanContinuePullingListener listener){
        mPullingListener = listener;
    }

    @Override
    public void clearForInit() {
        if(!mRefreshing) mRefreshingListener.init();
    }

    @Override
    public boolean shouldInterceptTouchEvent(int touchSlop, float dx, float dy) {
        return isEnabled() && (mRefreshing || canScrollUp(touchSlop, dx, dy));
    }

    private boolean canScrollUp(int touchSlop, float dx, float dy){
        if(DEBUG) Log.v(TAG, "canScrollUp: touchSlop=" + touchSlop + ", dy=" + dy + "," +
                "mPullingListener.canScrollUp(mTargetView)=" + mPullingListener.canScrollUp(mTargetView));
        return touchSlop <= dy && !mPullingListener.canScrollUp(mTargetView);
    }

    @Override
    public void doScroll(float dx, float totalDx, float dy, float totalDy) {
        mRefreshingListener.downInProgress(dx, totalDx, dy, totalDy);
        float y = mRefreshingView.getY();
        final int top = mRefreshingView.getTop();
        Log.v(TAG, "doScroll: y=" + y + ", dy=" + dy + ", top=" + top);
        setYs(Math.min(Math.min(y + dy, top), mRefreshingListener.getMaxPullingDownDistance()));
    }

    private void setYs(float y){
        mRefreshingView.setY(y);
        mTargetView.setY(y + mRefreshingView.getHeight());
    }

    @Override
    public void doActionUp(float velocityX, float velocityY, float totalDx, float totalDy, float dx, float dy) {
        Log.v(TAG, "doActionUp");
        final float y = mRefreshingView.getY();
        final int height = mRefreshingView.getHeight();
        final int minDistance = mRefreshingListener.getMinPullingDownDistance();
        final int paddingTop = getPaddingTop();
        final float diff = y + height - minDistance - paddingTop;
        if(diff < 0){ //don't refresh if move distance is least than a distance when a finger releases up
            startEndingAnimations();
        }else if(diff > 0){ //don't leave empty when refreshing on the top of the refreshing view
            final int duration = 200;
            mRefreshingView.animate().y(y - diff).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    refreshing();
                }
            }).start();
            mTargetView.animate().y(y - diff + height).setDuration(duration).start();
        }else{
            refreshing();
        }
    }

    private void refreshing(){
        mRefreshing = true;
        mRefreshingListener.refreshing();
        mRefreshProgressListener.onLoadNew(this);
    }

    public interface OnRefreshListener{
        void onLoadNew(MyPullRefreshLayout refreshLayout);
//        void onLoadNewDone();
    }
    private OnRefreshListener mRefreshProgressListener;
    public void setOnRefreshListener(OnRefreshListener listener){
        mRefreshProgressListener = listener;
    }

    /**
     * must be called on UI thread
     * */
    public void setLoadNewDone(){
        startEndingAnimations();
    }

    private void startEndingAnimations(){
        final int duration = mEndingAnimationDuration;
        final TimeInterpolator interpolator = new FastOutLinearInInterpolator();
        /*mRefreshingView.animate().y(mInitY - mRefreshingView.getHeight()).setInterpolator(interpolator)
                .setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                end();
            }
        }).start();*/

        mTargetView.animate().y(mInitY).setDuration(duration).setInterpolator(interpolator).start();
    }

    private Runnable mEndingAnimation;
    private long mEndingAnimationStartTime = Long.MIN_VALUE;

    private void end(){
        mRefreshing = false;
        mRefreshingListener.refreshingOver();
    }

    private class TimeRunnable implements Runnable{
        final float mFinalY, mStartY;

        public TimeRunnable(float startY, float finalY){
            mStartY = startY;
            mFinalY = finalY;
        }

        @Override
        public void run() {
            final long current = System.currentTimeMillis();
            final long elapsed = current - mEndingAnimationStartTime;
            if(mEndingAnimationDuration > elapsed){
                ViewCompat.postOnAnimation(mRefreshingView, this);
                final float percent = elapsed/(float)mEndingAnimationDuration;
                mRefreshingView.setY((mFinalY - mStartY) * percent + mStartY);
//                mRefreshingListener.downInProgress();
            }else{
                mRefreshingView.setY(mFinalY);
                mRefreshingView.removeCallbacks(this);
            }
        }
    }
}
