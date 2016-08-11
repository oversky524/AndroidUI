package io.oversky524.pullrefresh;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gaochao on 2016/7/20.
 */
public interface OnPullListener {
    void initForDown();

    void initForUp();

    /**
     * @return refreshing view when pulling down
     * */
    View getDownView(ViewGroup parent);

    /**
     * @return refreshing view when pulling up
     * */
    View getUpView(ViewGroup parent);

    /**
     * @param dx x offset between first down(or last move) event and this move event
     * @param totalDx x offset between first down event and this move event
     * @param dy y offset between first down(or last move) event and this move event
     * @param totalDy y offset between first down event and this move event
     * */
    void downInProgress(float dx, float totalDx, float dy, float totalDy);

    /**
     * @param dx x offset between first down(or last move) event and this move event
     * @param totalDx x offset between first down event and this move event
     * @param dy y offset between first down(or last move) event and this move event
     * @param totalDy y offset between first down event and this move event
     * */
    void upInProgress(float dx, float totalDx, float dy, float totalDy);

    /**
     * pulling down refreshing is under way
     * */
    void refreshingForDown();

    /**
     * refreshing is under way
     * */
    void refreshingForUp();

    /**
     * refreshing is over, and starts disappearing
     * */
    void refreshingOverForDown();

    /**
     * refreshing is over, and starts disappearing
     * */
    void refreshingOverForUp();

    /**
     * @return max pulling down distance
     * Integer.MAX_VALUE indicates that it's not cared about
     * */
    int getMaxDownDistance();

    /**
     * @return max pulling up distance
     * Integer.MAX_VALUE indicates that it's not cared about
     * */
    int getMaxUpDistance();

    /**
     * @return min pulling down distance
     * 0 indicates that it's not cared about
     * */
    int getMinDownDistance();

    /**
     * @return min pulling up distance
     * 0 indicates that it's not cared about
     * */
    int getMinUpDistance();
}
