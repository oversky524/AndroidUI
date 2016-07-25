package io.oversky524.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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
    private static final int LONG_WIDTH = 2;

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
        boolean result = (mDownRefreshing || mUpRefreshing) || mTouchHelper.onTouchEvent(event);
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
    private View mRefreshingUpView;
    private float mInitY, mInitYForUp;
    private boolean mPullingDown, mPullingUp;
    private boolean mDownRefreshing, mUpRefreshing;//swallow events when refreshing
    private int mEndingAnimationDuration = DEFAULT_ENDING_ANIMATION_DURATION;

    public void setEndingAnimationDuration(int duration){
        mEndingAnimationDuration = duration;
    }

    public View getTargetView(){
        return mTargetView;
    }

    public void setOnPullRefreshListener(OnPullRefreshListener listener){
        mRefreshingListener = listener;

        mRefreshingView = listener.getPullingDownView(this);
        if(mRefreshingView != null) addView(mRefreshingView, 0);

        mRefreshingUpView = listener.getPullingUpView(this);
        if(mRefreshingUpView != null){
            addView(mRefreshingUpView);
            FrameLayout.LayoutParams mlp = (FrameLayout.LayoutParams)mRefreshingUpView.getLayoutParams();
            mlp.gravity |= Gravity.BOTTOM;
            mRefreshingUpView.setLayoutParams(mlp);
        }
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                View child = mRefreshingView;
                if(child != null) {
                    final int height = child.getHeight();
                    final float y = child.getY();
                    mInitY = y;
                    child.setY(y - height);
                }
                child = mRefreshingUpView;
                if(child != null){
                    final int height = child.getHeight();
                    final float y = child.getY();
                    mInitYForUp = y;
                    child.setY(y + height);
                }
                return true;
            }
        });
    }

    public void setChildCanContinuePullingListener(ChildCanContinuePullingListener listener){
        mPullingListener = listener;
    }

    @Override
    public void clearForInit() {
        if(mUpRefreshing){
            mRefreshingListener.initForPullingUp();
        }else if(mDownRefreshing){
            mRefreshingListener.initForPullingDown();
        }
    }

    @Override
    public boolean shouldInterceptTouchEvent(int touchSlop, float dx, float dy) {
        if(!isEnabled()) return false;
        if(mDownRefreshing || mUpRefreshing) return true;

        if(canScrollUp(touchSlop, dx, dy)){
            mPullingDown = true;
            return true;
        }

        if(canScrollDown(touchSlop, dx, dy)){
            mPullingUp = true;
            return true;
        }

        return false;
    }

    private boolean canScrollUp(int touchSlop, float dx, float dy){
        if(DEBUG) Log.v(TAG, "canScrollUp: touchSlop=" + touchSlop + ", dx=" + dx + ", dy=" + dy + "," +
                "mPullingListener.canScrollUp(mTargetView)=" + mPullingListener.canScrollUp(mTargetView));
        return LONG_WIDTH * Math.abs(dx) <= Math.abs(dy) && touchSlop <= dy && !mPullingListener.canScrollUp(mTargetView);
    }

    private boolean canScrollDown(int touchSlop, float dx, float dy){
        if(DEBUG) Log.v(TAG, "canScrollDown: touchSlop=" + touchSlop + ", dx=" + dx + ", dy=" + dy + "," +
                "mPullingListener.canScrollDown(mTargetView)=" + mPullingListener.canScrollDown(mTargetView));
        return LONG_WIDTH * Math.abs(dx) <= Math.abs(dy) && touchSlop <= -dy && !mPullingListener.canScrollDown(mTargetView);
    }

    @Override
    public void doScroll(float dx, float totalDx, float dy, float totalDy) {
        if(mPullingDown) {
            mRefreshingListener.downInProgress(dx, totalDx, dy, totalDy);
            float y = mRefreshingView.getY();
            y = (Math.min(Math.min(y + dy, mRefreshingView.getTop()), mRefreshingListener.getMaxPullingDownDistance()));
            mRefreshingView.setY(y);
            mTargetView.setY(y + mRefreshingView.getHeight());
        }else if(mPullingUp){
            mRefreshingListener.upInProgress(dx, totalDx, dy, totalDy);
            final float y = mRefreshingUpView.getY();
            final float yMin = getHeight() - getPaddingBottom() - mRefreshingListener.getMaxPullingUpDistance();
            mRefreshingUpView.setY(Math.max(y + dy, yMin));
            mTargetView.setY(mRefreshingUpView.getY() - mTargetView.getHeight());
        }
    }

    @Override
    public void doActionUp(float velocityX, float velocityY, float totalDx, float totalDy, float dx, float dy) {
        Log.v(TAG, "doActionUp");
        if(mPullingDown) {
            final float y = mRefreshingView.getY();
            final int height = mRefreshingView.getHeight();
            final float diff = y + height - mRefreshingListener.getMinPullingDownDistance() - getPaddingTop();
            if (diff < 0) { //don't refresh if move distance is least than a distance when a finger releases up
                startPullingDownEndingAnimations();
            } else if (diff > 0) { //don't leave empty when refreshing on the top of the refreshing view
                final int duration = 200;
                final TimeInterpolator interpolator = new FastOutLinearInInterpolator();
                mRefreshingView.animate().y(y - diff).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        refreshingForDown();
                    }
                }).setInterpolator(interpolator).start();
                mTargetView.animate().y(y - diff + height).setInterpolator(interpolator).setDuration(duration).start();
            } else {
                refreshingForDown();
            }
        }else if(mPullingUp){
            final float y = mRefreshingUpView.getY();
            final int height = getHeight();
            final int minDistance = mRefreshingListener.getMinPullingUpDistance();
            final int paddingBottom = getPaddingBottom();
            final float diff = height - paddingBottom - y - minDistance;
            if(diff < 0){
                startPullingUpEndingAnimations();
            }else if(diff == 0){
                refreshingForUp();
            }else{
                final int duration = 200;
                final TimeInterpolator interpolator = new FastOutLinearInInterpolator();
                mRefreshingUpView.animate().y(y + diff).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        refreshingForUp();
                    }
                }).setInterpolator(interpolator).start();
                mTargetView.animate().y(y + diff - mTargetView.getHeight()).setDuration(duration).setInterpolator(interpolator).start();
            }
        }
    }

    private void refreshingForDown(){
        mDownRefreshing = true;
        mRefreshingListener.refreshingForPullingDown();
        mRefreshProgressListener.onLoadNew(this);
    }

    private void refreshingForUp(){
        mUpRefreshing = true;
        mRefreshingListener.refreshingForPullingUp();
        mRefreshProgressListener.onLoadMore(this);
    }

    public interface OnRefreshListener{
        void onLoadNew(MyPullRefreshLayout refreshLayout);
        void onLoadMore(MyPullRefreshLayout refreshLayout);
    }
    private OnRefreshListener mRefreshProgressListener;
    public void setOnRefreshListener(OnRefreshListener listener){
        mRefreshProgressListener = listener;
    }

    /**
     * must be called on UI thread
     * */
    public void setLoadNewDone(){
        startPullingDownEndingAnimations();
    }

    /**
     * must be called on UI thread
     * */
    public void setLoadMoreDone(){
        startPullingUpEndingAnimations();
    }

    private void startPullingDownEndingAnimations(){
        final int duration = mEndingAnimationDuration;
        final TimeInterpolator interpolator = new FastOutLinearInInterpolator();
        mRefreshingView.animate().y(mInitY - mRefreshingView.getHeight()).setInterpolator(interpolator)
                .setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                end();
            }
        }).start();
        mTargetView.animate().y(mInitY).setDuration(duration).setInterpolator(interpolator).start();
    }

    private void startPullingUpEndingAnimations(){
        final int duration = mEndingAnimationDuration;
        final TimeInterpolator interpolator = new FastOutLinearInInterpolator();
        mRefreshingUpView.animate().y(mInitYForUp + mRefreshingUpView.getHeight()).setInterpolator(interpolator)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        end();
                    }
                })
                .setDuration(duration).start();
        mTargetView.animate().y(mInitY).setDuration(duration).setInterpolator(interpolator).start();
    }

    private void end(){
        if(mDownRefreshing){
            mPullingDown = false;
            mDownRefreshing = false;
            mRefreshingListener.refreshingOverForPullingDown();
        }else if(mUpRefreshing){
            mPullingUp = false;
            mUpRefreshing = false;
            mRefreshingListener.refreshingOverForPullingUp();
        }
    }
}
