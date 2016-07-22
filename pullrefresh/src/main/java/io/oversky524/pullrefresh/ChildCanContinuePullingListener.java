package io.oversky524.pullrefresh;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by gaochao on 2016/7/20.
 */
public interface ChildCanContinuePullingListener {

    boolean canScrollUp(View target);

//    boolean canScrollDown(View target);

    class ListViewPullingListener implements ChildCanContinuePullingListener {
        @Override
        public boolean canScrollUp(View target) {
            if(target instanceof AbsListView){
                final AbsListView absListView = (AbsListView) target;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            }
            return false;
        }
    }
}
