package io.base.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import io.base.ui.ActivityLifeCycle;
import io.base.utils.ResourcesUtils;

/**
 * Created by gaochao on 2015/11/11.
 */
public class PullUpLoadMoreItemTouchListener<VH extends RecyclerView.ViewHolder> implements RecyclerView.OnItemTouchListener {
    private RecyclerView.Adapter<VH> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private float lastY, mInitY;
    private int mTouchSlop;

    public PullUpLoadMoreItemTouchListener(LinearLayoutManager layoutManager, RecyclerView.Adapter<VH> adapter){
        mAdapter = adapter;
        mLayoutManager = layoutManager;
        mTouchSlop = ResourcesUtils.dpToPix(70, ActivityLifeCycle.getCurrentActivity());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        float currentY = e.getY();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY = currentY;
                mInitY = currentY;
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = currentY - lastY;
                lastY = currentY;
                if (mTouchSlop <= Math.abs(mInitY - currentY) && deltaY < 0 && isBottom()) {
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                if(isBottom() && null != mOnLoadMoreListener){
                    mOnLoadMoreListener.onLoadMore();
                }
                break;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private boolean isBottom() {
        int lastVisibleItemPos = mLayoutManager.findLastVisibleItemPosition();
        int last = mAdapter.getItemCount() - 1;
        if (lastVisibleItemPos != RecyclerView.NO_POSITION && lastVisibleItemPos == last) {
            View lastView = mLayoutManager.findViewByPosition(lastVisibleItemPos);
            View parent = (View)lastView.getParent();
            int childBottomInParent = (int)lastView.getY() + lastView.getHeight();
            if(childBottomInParent > parent.getScrollY() + parent.getHeight() - parent.getPaddingBottom() - parent.getPaddingTop()){
                return false;
            }
            return true;
        }
        return false;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }
    private OnLoadMoreListener mOnLoadMoreListener;
    public PullUpLoadMoreItemTouchListener setOnLoadMoreListener(OnLoadMoreListener listener){
        mOnLoadMoreListener = listener;
        return this;
    }
}
