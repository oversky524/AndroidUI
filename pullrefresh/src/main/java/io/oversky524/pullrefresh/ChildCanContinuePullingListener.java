package io.oversky524.pullrefresh;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by gaochao on 2016/7/20.
 */
public interface ChildCanContinuePullingListener {

    boolean canScrollUp(View target);

    boolean canScrollDown(View target);

    class ListViewPullingListener implements ChildCanContinuePullingListener {
        @Override
        public boolean canScrollUp(View target) {
            if (android.os.Build.VERSION.SDK_INT < 14) {
                if (target instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) target;
                    return absListView.getChildCount() > 0
                            && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                            .getTop() < absListView.getPaddingTop());
                } else {
                    return ViewCompat.canScrollVertically(target, -1) || target.getScrollY() > 0;
                }
            } else {
                return ViewCompat.canScrollVertically(target, -1);
            }
        }

        @Override
        public boolean canScrollDown(View target) {
            if (android.os.Build.VERSION.SDK_INT < 14) {
                if (target instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) target;
                    final int count = absListView.getChildCount();
                    return count > 0 && (absListView.getLastVisiblePosition() < count - 1 ||
                            absListView.getChildAt(absListView.getChildCount() - 1)
                                    .getBottom() > absListView.getPaddingTop() + absListView.getHeight());
                } else {
                    return ViewCompat.canScrollVertically(target, 1) || target.getScrollY() < 0;
                }
            } else {
                return ViewCompat.canScrollVertically(target, 1);
            }
        }
    }
}
