package io.oversky524.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import io.base.utils.ScrollTouchHelper;

/**
 * Created by gaochao on 2016/7/20.
 */
public class PullLayout extends FrameLayout implements ScrollTouchHelper.ScrollTouchCallback {
    private static final boolean DEBUG = true;
    private static final String TAG = PullLayout.class.getSimpleName();
    private static final int DEFAULT_ENDING_ANIMATION_DURATION = 100;//unit: ms
    private static final int LONG_WIDTH = 2;

    private EdgeEffectCompat mTopGlow, mBottomGlow;
    private float mEdgeEffectWidth, mEdgeEffectHeight;

    private OnChildPullListener mPullingListener = new OnChildPullListener.Default();
    private ScrollTouchHelper mTouchHelper;
    private OnPullListener mRefreshingListener;
    private ViewGroup mTargetView;
    private View mRefreshingView;
    private View mRefreshingUpView;
    private float mInitY, mInitYForUp;
    private boolean mPullingDown, mPullingUp;
    private boolean mDownRefreshing, mUpRefreshing;//swallow events when refreshing
    private int mEndingAnimationDuration = DEFAULT_ENDING_ANIMATION_DURATION;
    private boolean mClipToPadding;
    private boolean mRefreshingForDown;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PullLayout(Context context) {
        super(context);
    }

    public PullLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mTouchHelper.onInterceptTouchEvent(ev) ? true : super.onInterceptTouchEvent(ev);
        if(DEBUG) Log.v(TAG, "onInterceptTouchEvent=" + result);
        return result;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
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
        mTargetView = (ViewGroup) getChildAt(0);
        mTargetView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTouchHelper = new ScrollTouchHelper(this, this);
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        mClipToPadding = clipToPadding;
        super.setClipToPadding(clipToPadding);
        ensureEdgeEffects();
    }

    public void setEndingAnimationDuration(int duration){
        mEndingAnimationDuration = duration;
    }

    public View getTargetView(){
        return mTargetView;
    }

    public void setOnPullRefreshListener(OnPullListener listener){
        mRefreshingListener = listener;

        mRefreshingView = listener.getDownView(this);
        if(mRefreshingView != null) addView(mRefreshingView, 0);

        mRefreshingUpView = listener.getUpView(this);
        if(mRefreshingUpView != null){
            addView(mRefreshingUpView);
            FrameLayout.LayoutParams mlp = (FrameLayout.LayoutParams)mRefreshingUpView.getLayoutParams();
            mlp.gravity = Gravity.BOTTOM;
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
                refreshingWhenEntering();
                return true;
            }
        });
    }

    public void setChildCanContinuePullingListener(OnChildPullListener listener){
        mPullingListener = listener;
    }

    @Override
    public void clearForInit() {
        if(mUpRefreshing){
            mRefreshingListener.initForUp();
        }else if(mDownRefreshing){
            mRefreshingListener.initForDown();
        }
    }

    @Override
    public boolean shouldInterceptTouchEvent(int touchSlop, float dx, float dy) {
        if(!isEnabled()) return false;
        if(mDownRefreshing || mUpRefreshing) return true;

        if(canChildScrollUp(touchSlop, dx, dy)){
            mPullingDown = mRefreshingView != null;
            return true;
        }

        if(canChildScrollDown(touchSlop, dx, dy)){
            mPullingUp = mRefreshingUpView != null;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldInterceptTouchMoveEvent(int touchSlop, float dx, float dy) {
        return shouldInterceptTouchEvent(touchSlop, dx, dy);
    }

    private boolean canChildScrollUp(int touchSlop, float dx, float dy){
        if(DEBUG) Log.v(TAG, "canScrollUp: touchSlop=" + touchSlop + ", dx=" + dx + ", dy=" + dy + "," +
                "mPullingListener.canScrollUp(mTargetView)=" + mPullingListener.canScrollUp(mTargetView));
        return LONG_WIDTH * Math.abs(dx) <= Math.abs(dy) && touchSlop <= dy && !mPullingListener.canScrollUp(mTargetView);
    }

    private boolean canChildScrollDown(int touchSlop, float dx, float dy){
        if(DEBUG) Log.v(TAG, "canScrollDown: touchSlop=" + touchSlop + ", dx=" + dx + ", dy=" + dy + "," +
                "mPullingListener.canScrollDown(mTargetView)=" + mPullingListener.canScrollDown(mTargetView));
        return LONG_WIDTH * Math.abs(dx) <= Math.abs(dy) && touchSlop <= -dy && !mPullingListener.canScrollDown(mTargetView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ensureEdgeEffects();
    }

    @Override
    public void setOverScrollMode(int overScrollMode) {
        super.setOverScrollMode(overScrollMode);
        ensureEdgeEffects();
    }

    private void ensureEdgeEffects(){
        if(getOverScrollMode() == OVER_SCROLL_NEVER){
            mBottomGlow = mTopGlow = null;
            return;
        }
        int width, height;
        if (mClipToPadding) {
            width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        } else {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
        mEdgeEffectHeight = height;
        mEdgeEffectWidth = width;
        if(mRefreshingView == null){
            if (mTopGlow == null) {
                mTopGlow = new EdgeEffectCompat(getContext());
            }
            mTopGlow.setSize(width, height);
        }else{
            mTopGlow = null;
        }
        if(mRefreshingUpView == null){
            if (mBottomGlow == null) {
                mBottomGlow = new EdgeEffectCompat(getContext());
            }
            mBottomGlow.setSize(width, height);
        }else{
            mBottomGlow = null;
        }
    }

    @Override
    public void doActionMove(float dx, float totalDx, float dy, float totalDy) {
        final View targetView = mTargetView, upView = mRefreshingUpView, downView = mRefreshingView;
        final OnPullListener refreshListener = mRefreshingListener;
        if(mPullingDown) {
            refreshListener.downInProgress(dx, totalDx, dy, totalDy);
            float y = downView.getY();
            y = (Math.min(Math.min(y + dy, downView.getTop()), refreshListener.getMaxDownDistance()));
            downView.setY(y);
            targetView.setY(y + downView.getHeight());
        }else if(mPullingUp){
            refreshListener.upInProgress(dx, totalDx, dy, totalDy);
            final float y = upView.getY();
            final float yMin = getHeight() - getPaddingBottom() - refreshListener.getMaxUpDistance();
            upView.setY(Math.max(y + dy, yMin));
            targetView.setY(upView.getY() - targetView.getHeight());
        }else{
            final float ty = targetView.getY();
            if(downView == null && mTopGlow != null && ty + dy > 0){
                mTopGlow.onPull(-dy / mEdgeEffectHeight, .5f);
                if(!mTopGlow.isFinished()) postInvalidateOnAnimation();
            }
            if(upView == null && mBottomGlow != null && ty + dy < 0){
                mBottomGlow.onPull(-dy / mEdgeEffectHeight, .5f);
                if(!mBottomGlow.isFinished()) postInvalidateOnAnimation();
            }
        }
    }

    @Override
    public void doActionCancel() {
        releaseEdgeEffects();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        boolean needsInvalidate = false;
        if(mBottomGlow != null && !mBottomGlow.isFinished()){
            final int sc = canvas.save();
            canvas.rotate(180);
            if (mClipToPadding) {
                canvas.translate(-getWidth() + getPaddingRight(), -getHeight() + getPaddingBottom());
            } else {
                canvas.translate(-getWidth(), -getHeight());
            }
            needsInvalidate |= mBottomGlow.draw(canvas);
            canvas.restoreToCount(sc);
        }
        if(mTopGlow != null && !mTopGlow.isFinished()){
            final int sc = canvas.save();
            if(mClipToPadding) canvas.translate(getPaddingLeft(), getPaddingTop());
            needsInvalidate |= mTopGlow.draw(canvas);
            canvas.restoreToCount(sc);
        }
        if(needsInvalidate){
            postInvalidateOnAnimation();
        }
    }

    @Override
    public void doActionUp(float velocityX, float velocityY, float totalDx, float totalDy, float dx, float dy) {
        Log.v(TAG, "doActionUp");
        if(mPullingDown) {
            final float y = mRefreshingView.getY();
            final int height = mRefreshingView.getHeight();
            final float diff = y + height - mRefreshingListener.getMinDownDistance() - getPaddingTop();
            if (diff < 0) { //don't refresh if move distance is least than a distance when a finger releases up
                startPullingDownEndingAnimations();
            } else if (diff > 0) { //don't leave empty when refreshing on the top of the refreshing view
                final int duration = 200;
                final TimeInterpolator interpolator = new FastOutLinearInInterpolator();
                mRefreshingView.animate().y(y - diff).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        refreshingForDownInternal();
                    }
                }).setInterpolator(interpolator).start();
                mTargetView.animate().y(y - diff + height).setInterpolator(interpolator).setDuration(duration).start();
            } else {
                refreshingForDownInternal();
            }
        }else if(mPullingUp){
            final float y = mRefreshingUpView.getY();
            final int height = getHeight();
            final int minDistance = mRefreshingListener.getMinUpDistance();
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
        releaseEdgeEffects();
    }

    private void releaseEdgeEffects(){
        if(mTopGlow != null) mTopGlow.onRelease();
        if(mBottomGlow != null) mBottomGlow.onRelease();
    }

    private void refreshingForDownInternal(){
        mDownRefreshing = true;
        mRefreshingListener.refreshingForDown();
        mRefreshProgressListener.onLoadNew(this);
    }

    public void refreshingForDown(){
        mRefreshingForDown = true;
    }

    private void refreshingWhenEntering(){
        if(!mRefreshingForDown) return;
        mRefreshingForDown = false;
        final int h = mRefreshingView.getHeight();
        final float y = getPaddingTop() + mRefreshingListener.getMinDownDistance() - h;
        mRefreshingView.setY(y);
        mTargetView.setY(y + h);
        refreshingForDownInternal();
    }

    private void refreshingForUp(){
        mUpRefreshing = true;
        mRefreshingListener.refreshingForUp();
        mRefreshProgressListener.onLoadMore(this);
    }

    private OnLoadListener mRefreshProgressListener;
    public void setOnRefreshListener(OnLoadListener listener){
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
        refreshingDone();
        final int duration = mEndingAnimationDuration;
        final TimeInterpolator interpolator = new AccelerateInterpolator();
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
        refreshingDone();
        final int duration = mEndingAnimationDuration;
        final TimeInterpolator interpolator = new AccelerateInterpolator();
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
        }else if(mUpRefreshing){
            mPullingUp = false;
            mUpRefreshing = false;
        }
    }

    private void refreshingDone(){
        if(mDownRefreshing){
            mRefreshingListener.refreshingOverForDown();
        }else if(mUpRefreshing){
            mRefreshingListener.refreshingOverForUp();
        }
    }
}
