package io.oversky524.centeredchildviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.base.ScrollTouchHelper;

/**
 * Created by gaochao on 2016/1/27.
 */
public class ChildCenteredViewPager extends ViewGroup {
    private static final boolean DEBUG = true;
    private static final String TAG = ChildCenteredViewPager.class.getSimpleName();
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
    private static final int MIN_FLING_VELOCITY = 400; // dips

    private int mCurItem;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mFlingDistance;
    private float[] mOffsetArray;
    private float mFirstOffset;
    private CenteredPageAdapter mAdapter;
    private ScrollTouchHelper mStHelper;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    public ChildCenteredViewPager(Context context) {
        super(context);
        init(context);
    }

    public ChildCenteredViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChildCenteredViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChildCenteredViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        setWillNotDraw(true);
        final float density = context.getResources().getDisplayMetrics().density;
        mScroller = new Scroller(context, sInterpolator);
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);

        mStHelper = new ScrollTouchHelper(this, new ScrollTouchHelper.ScrollTouchCallback() {
            @Override
            public void clearForInit() {
                Scroller scroller = mScroller;
                if(!scroller.isFinished()){
                    if(DEBUG) Log.v(TAG, "clearForInit");
                    scroller.abortAnimation();
                    scrollTo(scroller.getFinalX(), getScrollY());
                }
            }

            @Override
            public boolean shouldInterceptTouchEvent(int touchSlop, float dx, float dy) {
                final float xdiff = Math.abs(dx);
                final float ydiff = Math.abs(dy);
                return touchSlop < xdiff && xdiff > 2 * ydiff;
            }

            @Override
            public void doScroll(float dx, float tx, float dy, float ty) {
                ChildCenteredViewPager.this.doScroll(dx);
                if(DEBUG) Log.v(TAG, "doScroll");
            }

            @Override
            public void doActionUp(float velocityX, float velocityY, float totalDx, float totalDy, float dx, float dy) {
                View finalChild = getFinalItem((int)velocityX, (int)totalDx);
                int finalScrollX = (finalChild.getLeft() + finalChild.getRight()) / 2 - getContentWidth() / 2;
                mScroller.startScroll(getScrollX(), 0, finalScrollX - getScrollX(), 0);
                invalidate();
                if(DEBUG) Log.v(TAG, "doActionUp");
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED ||
                MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("width and height can't be unspecified!");
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getContentWidth();
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingBottom() - getPaddingTop(),
                MeasureSpec.EXACTLY);
        final ArrayList<View> children = mSortedChildren;
        final int count = children.size();
        for (int i = 0; i < count; ++i) {
            View child = children.get(i);
            MyLayoutParams mlp = (MyLayoutParams) child.getLayoutParams();
            if(mlp.needsMeasure) {
                int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (width * mlp.widthFactor), MeasureSpec.EXACTLY);
                child.measure(childWidthSpec, childHeightSpec);
                mlp.needsMeasure = false;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        l += getPaddingLeft();
        t += getPaddingTop();
        b -= getPaddingBottom();

        final ArrayList<View> children = mSortedChildren;
        final int count = children.size();
        final float[] offsetArray = mOffsetArray;
        final int width = getMeasuredWidth();
        for (int i = 0; i < count; ++i) {
            View child = children.get(i);
            MyLayoutParams mlp = (MyLayoutParams) child.getLayoutParams();
            int left = l + (int)(width * offsetArray[mlp.position]);
            child.layout(left, t, left + child.getMeasuredWidth(), b);
        }

        doPageTransformation();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mStHelper.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mStHelper.onTouchEvent(ev);
    }

    private void doScroll(final float dx) {
        if (getChildCount() < 1) return;
        int finalScrollX = getScrollX() - (int) dx;
        if (finalScrollX < 0){
            finalScrollX = 0;
        }else {
            final int width = getWidth(), count = mOffsetArray.length;
            int mostRightScrollX = (int) (width * (mOffsetArray[count - 1] + mAdapter.getWidthFactor(count - 1) / 2)) - getContentWidth() / 2;
            if (mostRightScrollX < finalScrollX) finalScrollX = mostRightScrollX;
        }
        scrollTo(finalScrollX, getScrollY());
        doPageTransformation();
    }

    private int getContentWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private View getFinalItem(int velocityX, int dx) {
        int newCurItem = -1;
        if (Math.abs(dx) > mFlingDistance && Math.abs(velocityX) > mMinimumVelocity) {
            if (DEBUG) Log.v(TAG, "velocityX=" + velocityX);
            newCurItem = mCurItem + (velocityX < 0 ? 1 : -1);
            newCurItem = Math.max(0, Math.min(newCurItem, mAdapter.getCount() - 1));
        }else {
            final float[] offsetArray = mOffsetArray;
            final CenteredPageAdapter adapter = mAdapter;
            final int count = offsetArray.length;
            int curScrollX = getScrollX();
            float centerX = (curScrollX + getContentWidth() / 2)/(float)getWidth();
            for (int i = 0; i < count; ++i) {
                final float offset = offsetArray[i];
                if (centerX >= offset && centerX < offset + adapter.getWidthFactor(i)) {
                    newCurItem = i;
                    break;
                }
            }
            if (newCurItem == -1) newCurItem = count - 1;
        }
        fillItems(newCurItem);
        return getChild(newCurItem);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MyLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MyLayoutParams(p);
    }

    private static class MyLayoutParams extends LayoutParams {
        int position;
        float widthFactor;

        /**
         * true if this view was added during layout and needs to be measured
         * before being positioned.
         */
        boolean needsMeasure = true;

        public MyLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MyLayoutParams(LayoutParams source) {
            super(source);
        }

        public MyLayoutParams(int width, int height) {
            super(width, height);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), getScrollY());
            doPageTransformation();
            invalidate();
        }
    }

    private void doPageTransformation() {
        final int count = getChildCount();
        final PageTransformer pageTransformer = mPageTransformer;
        if (count < 1 || pageTransformer == null) return;

        final int centerX = getContentWidth() / 2 + getScrollX();
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            int childWidth = child.getWidth();
            int childCenterX = child.getLeft() + childWidth / 2;
            float position = (float) (childCenterX - centerX) / childWidth;
            pageTransformer.transformPage(child, position);
        }
    }

    private PageTransformer mPageTransformer;

    public void setPageTransformer(PageTransformer transformer) {
        mPageTransformer = transformer;
    }

    public interface PageTransformer {
        /**
         * Apply a property transformation to the given page.
         *
         * @param page     Apply the transformation to this page
         * @param position Position of page relative to the current front-and-center
         *                 position of the pager. 0 is front and center. 1 is one full
         *                 page position to the right, and -1 is one page position to the left.
         */
        void transformPage(View page, float position);
    }

    public interface CenteredPageAdapter{
        int getCount();
        View getView(int position, ViewGroup parent);
        void destroyItem(int position);
        float getWidthFactor(int position);
    }

    public void setAdapter(CenteredPageAdapter adapter){
        CenteredPageAdapter oldAdapter = mAdapter;
        if(oldAdapter == adapter) return;
        if(oldAdapter != null){
            int count = oldAdapter.getCount();
            for(int i=0; i<count; ++i){
                oldAdapter.destroyItem(i);
            }
            removeAllViews();
            mSortedChildren.clear();
            scrollTo(0, 0);
            mCurItem = 0;
        }

        mAdapter = adapter;
        if(adapter != null){
            fillItems(mCurItem);
        }

        calculateOffsets();
    }

    private void calculateOffsets(){
        final CenteredPageAdapter adapter = mAdapter;
        final int count = adapter.getCount();
        if(count < 1) return;
        if(mOffsetArray == null){
            mOffsetArray = new float[count];
        }

        final float[] offsetArray = mOffsetArray;
        float offset = (1f - adapter.getWidthFactor(0))/2;
        for(int i=0; i<count; ++i){
            offsetArray[i] = offset;
            offset += adapter.getWidthFactor(i);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() > 0){
            removeAllViews();
        }
    }

    /**
     * @param newCurItem new current item position
     * */
    private void fillItems(int newCurItem) {
        final CenteredPageAdapter adapter = mAdapter;
        mCurItem = newCurItem;

        final float halfExtraWidthFactor = (3f - adapter.getWidthFactor(newCurItem))/2;
        float availWidthFactor = halfExtraWidthFactor;
        boolean shouldInvalidate = addChild(newCurItem, adapter);
        int mostRight = -1, mostLeft = -1;
        for(int i= newCurItem + 1; i<adapter.getCount() && availWidthFactor > 0; ++i){
            mostRight = i;
            shouldInvalidate |= addChild(i, adapter);
            availWidthFactor -= adapter.getWidthFactor(i);
        }
        availWidthFactor = halfExtraWidthFactor;
        for(int i= newCurItem - 1; i>-1 && availWidthFactor > 0; --i){
            mostLeft = i;
            shouldInvalidate |= addChild(i, adapter);
            availWidthFactor -= adapter.getWidthFactor(i);
        }
        if(shouldInvalidate){
            final int count = getChildCount();
            for(int i=count - 1; i>-1; --i){
                View child = getChildAt(i);
                int position = ((MyLayoutParams)child.getLayoutParams()).position;
                if((position < mostLeft && mostLeft != -1) || (position > mostRight && mostRight != -1)){
                    removeViewAt(i);
                }
            }
            sortChildDrawingOrder();
            invalidate();
        }
    }

    private boolean addChild(int i, final CenteredPageAdapter adapter){
        View child = getChild(i);
        if(child == null){
            child = adapter.getView(i, this);
            addView(child);
            MyLayoutParams mlp = (MyLayoutParams)child.getLayoutParams();
            mlp.position = i;
            mlp.widthFactor = adapter.getWidthFactor(i);
            return true;
        }
        return false;
    }

    private void sortChildDrawingOrder() {
        final int childCount = getChildCount();
        final ArrayList<View> children = mSortedChildren;
        children.clear();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            children.add(child);
        }
        Collections.sort(children, sComparator);
    }

    private View getChild(int position){
        final ArrayList<View> list = mSortedChildren;
        final int size = list.size();
        for(int i=0; i<size; ++i){
            final View child = list.get(i);
            if(position == ((MyLayoutParams)child.getLayoutParams()).position){
                return child;
            }
        }
        return null;
    }

    private ArrayList<View> mSortedChildren = new ArrayList<>();
    private static Comparator<View> sComparator = new Comparator<View>() {
        @Override
        public int compare(View lhs, View rhs) {
            return ((MyLayoutParams)lhs.getLayoutParams()).position - ((MyLayoutParams)rhs.getLayoutParams()).position;
        }
    };
}
