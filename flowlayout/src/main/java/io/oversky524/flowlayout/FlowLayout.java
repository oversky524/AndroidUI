package io.oversky524.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";
    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;

    protected List<List<View>> mAllViews = new ArrayList<List<View>>();
    protected List<Integer> mLineHeight = new ArrayList<Integer>();
    protected List<Integer> mLineWidth = new ArrayList<Integer>();
    private int mGravity = LEFT;
    private int mHorizontalGap, mVerticalGap;
    private List<View> lineViews = new ArrayList<>();
    private int mMaxLineNumber = Integer.MAX_VALUE;

    public void setMaxLineNumber(int lineNumber) {
        mMaxLineNumber = lineNumber;
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mHorizontalGap = ta.getDimensionPixelSize(R.styleable.FlowLayout_horizontalGap, 0);
        mVerticalGap = ta.getDimensionPixelSize(R.styleable.FlowLayout_verticalGap, 0);
        ta.recycle();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    private View mLastView;

    public void setLastView(View lastView) {
        mLastView = lastView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0, height = 0;

        int lineWidth = 0, lineHeight = 0;

        int lastWidth = 0, lastHeight = 0, possibleMaxWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        View lastView = mLastView;
        boolean lastExist = lastView != null;
        if (lastExist) {
            ViewParent parent = lastView.getParent();
            if (parent != null) ((ViewGroup) parent).removeView(lastView);
            measureChild(lastView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) lastView.getLayoutParams();
            lastWidth = lastView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            lastHeight = lastView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }

        int cCount = getChildCount();
        int lines = 0, maxLineNumber = mMaxLineNumber;
        int hgap = mHorizontalGap;

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            if (lines >= maxLineNumber) {
//                if(child != lastView) child.setVisibility(GONE);
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            int curWidth = lineWidth + childWidth;
            boolean last = lastExist && lines + 1 == maxLineNumber, lastItem = i == cCount - 1;
            if (last) curWidth += lastWidth;
            if (curWidth > possibleMaxWidth) {
                if (last) {//for last line
                    width = Math.max(width, lastWidth + lineWidth);
                    height += Math.max(lineHeight, lastHeight);
                } else if (lines < maxLineNumber) {
                    width = Math.max(width, lineWidth);
                    lineWidth = 0;
                    height += lineHeight;
                    lineHeight = 0;
                    --i;
                }
                ++lines;
                if (lastItem) break;
                if (lines >= maxLineNumber) {
                    child.setVisibility(GONE);
                    continue;
                }
            } else {
                lineWidth += childWidth;
                if (lineWidth + hgap <= possibleMaxWidth) {
                    lineWidth += hgap;
                }
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (lastItem) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
                ++lines;
            }
        }

        if (lastExist) {
            if (lastView.getVisibility() != VISIBLE) lastView.setVisibility(VISIBLE);
            addView(lastView);
        }
        if (modeHeight == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else {
            height += getPaddingBottom() + getPaddingTop();
            if (lines > 0) height += (lines - 1) * mVerticalGap;
        }
        if (modeWidth == MeasureSpec.EXACTLY) {
            width = sizeWidth;
        } else {
            width += getPaddingLeft() + getPaddingRight();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        lineViews.clear();

        int width = getWidth(), possibleMaxWidth = width - getPaddingLeft() - getPaddingRight(),
                lineWidth = 0, lineHeight = 0;

        int cCount = getChildCount();
        int hgap = mHorizontalGap, vgap = mVerticalGap;
        boolean gapAdded = false;

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (childWidth + lineWidth > possibleMaxWidth) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                mLineWidth.add(gapAdded ? lineWidth - hgap : lineWidth);

                lineWidth = 0;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth;
            gapAdded = false;
            if (lineWidth + hgap <= possibleMaxWidth) {
                lineWidth += hgap;
                gapAdded = true;
            }
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);

        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            // set gravity
            int currentLineWidth = this.mLineWidth.get(i);
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    left = width - currentLineWidth + getPaddingLeft();
                    break;
            }

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + hgap;
            }
            top += lineHeight + vgap;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}