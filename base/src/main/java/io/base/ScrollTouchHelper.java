package io.base;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by Administrator on 2016/1/30.
 */
public class ScrollTouchHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = ScrollTouchHelper.class.getSimpleName();
    private static final int INVALID_POINTER = -1;

    private int mTouchSlop;
    private int mActivePointerId = INVALID_POINTER;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;
    private float mLastMotionX = Float.MIN_VALUE;
    private float mLastMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsDragging;
    private ViewGroup mTargetView;

    public ScrollTouchHelper(ViewGroup target, ScrollTouchCallback callback) {
        this(target, callback, false);
    }

    public ScrollTouchHelper(ViewGroup target, ScrollTouchCallback callback, boolean paged) {
        mTargetView = target;
        setScrollTouchCallback(callback);
        final Context context = target.getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = paged ?
                ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
                : configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    private boolean resetTouch() {
        boolean needsInvalidate = false;
        mActivePointerId = INVALID_POINTER;
//        endDrag();
//        needsInvalidate = mLeftEdge.onRelease() | mRightEdge.onRelease();
        return needsInvalidate;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            resetTouch();
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsDragging) return true;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                if (DEBUG) Log.v(TAG, "initX=" + mInitialMotionX);
                mCallback.clearForInit();
                mIsDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mInitialMotionX;
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float dy = y - mInitialMotionY;
                if (DEBUG) Log.v(TAG, "mInitialMotionY1=" + mInitialMotionY + ", y=" + y);
                if (mCallback.shouldInterceptTouchEvent(mTouchSlop, dx, dy)) {
                    mIsDragging = true;
                }
                if(!mIsDragging && mLastMotionX != Float.MIN_VALUE &&
                        mCallback.shouldInterceptTouchMoveEvent(mTouchSlop, x - mLastMotionX, y - mLastMotionY)){
                    mIsDragging = true;
                    mInitialMotionX = mLastMotionX;
                    mInitialMotionY = mLastMotionY;
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        return mIsDragging;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = mTargetView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
        pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        mLastMotionX = MotionEventCompat.getX(ev, pointerIndex);
        mLastMotionY = MotionEventCompat.getY(ev, pointerIndex);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    // Scroll to follow the motion event
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float dx = x - mLastMotionX;
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float dy = y - mLastMotionY;
                    if (DEBUG)
                        Log.v(TAG, "x=" + x + ", lastX=" + mLastMotionX + ", dx=" + dx + ", dy=" + dy);
                    mCallback.doActionMove(dx, x - mInitialMotionX, dy, y - mInitialMotionY);
                    mLastMotionX = x;
                    mLastMotionY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (DEBUG) Log.v(TAG, "mIsDragging=" + mIsDragging);
                if (mIsDragging) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
                    final float y = MotionEventCompat.getY(ev, activePointerIndex);
                    mCallback.doActionUp(VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId),
                            VelocityTrackerCompat.getYVelocity(velocityTracker, mActivePointerId),
                            x - mInitialMotionX, y - mInitialMotionY, x - mLastMotionX, y - mLastMotionY);
                    endDrag();
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mLastMotionY = MotionEventCompat.getY(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_CANCEL:
                mCallback.doActionCancel();
                endDrag();
                break;
        }
        return true;
    }

    private void endDrag() {
        mIsDragging = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface ScrollTouchCallback {
        void clearForInit();

        boolean shouldInterceptTouchEvent(int touchSlop, float dx, float dy);

        boolean shouldInterceptTouchMoveEvent(int touchSlop, float dx, float dy);

        /**
         * @param dx x offset between first down(or last move) event and this move event
         * @param tx x offset between first down event and this move event
         * @param dy y offset between first down(or last move) event and this move event
         * @param dy y offset between first down event and this move event
         */
        void doActionMove(float dx, float tx, float dy, float ty);

        void doActionCancel();

        void doActionUp(float velocityX, float velocityY, float totalDx, float totalDy, float dx, float dy);
    }

    private ScrollTouchCallback mCallback;

    public void setScrollTouchCallback(ScrollTouchCallback callback) {
        mCallback = callback;
    }
}
