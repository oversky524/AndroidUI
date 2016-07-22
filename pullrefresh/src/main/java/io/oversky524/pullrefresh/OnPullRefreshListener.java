package io.oversky524.pullrefresh;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gaochao on 2016/7/20.
 */
public interface OnPullRefreshListener {
    void init();

    /**
     * @return refreshing view
     * */
    View getRefreshView(ViewGroup parent);

    /**
     * @param dx x offset between first down(or last move) event and this move event
     * @param totalDx x offset between first down event and this move event
     * @param dy y offset between first down(or last move) event and this move event
     * @param totalDy y offset between first down event and this move event
     * */
    void downInProgress(float dx, float totalDx, float dy, float totalDy);

    /**
     * refreshing is in
     * */
    void refreshing();

    /**
     * refreshing is over, and starts disappearing
     * */
    void refreshingOver();

    /**
     * @return max pulling down distance
     * Integer.MAX_VALUE indicates that it's not cared about
     * */
    int getMaxPullingDownDistance();

    /**
     * @return min pulling down distance
     * 0 indicates that it's not cared about
     * */
    int getMinPullingDownDistance();
}
