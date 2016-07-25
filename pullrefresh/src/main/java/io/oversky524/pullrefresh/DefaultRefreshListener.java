package io.oversky524.pullrefresh;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by gaochao on 2016/7/20.
 */
public class DefaultRefreshListener implements OnPullRefreshListener {
    private static final boolean DEBUG = true;
    private static final String TAG = DefaultRefreshListener.class.getSimpleName();
    private TextView mPullingDownTv;
    private ProgressBar mRefreshingPb;

    @Override
    public void initForPullingDown() {

    }

    @Override
    public void initForPullingUp() {

    }

    @Override
    public View getPullingDownView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_refreshing_layout, parent, false);
        mPullingDownTv = (TextView)view.findViewById(R.id.pulling_down);
        mRefreshingPb = (ProgressBar)view.findViewById(R.id.refreshing);
        mPullingDownTv.setVisibility(View.VISIBLE);
        mRefreshingPb.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public View getPullingUpView(ViewGroup parent) {
        return null;
    }

    @Override
    public void downInProgress(float dx, float totalDx, float dy, float totalDy) {
        if(DEBUG) Log.v(TAG, "downInProgress");
        mPullingDownTv.setVisibility(View.VISIBLE);
        mRefreshingPb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void upInProgress(float dx, float totalDx, float dy, float totalDy) {

    }

    @Override
    public void refreshingForPullingDown() {
        if(DEBUG) Log.v(TAG, "refreshing");
        mPullingDownTv.setVisibility(View.INVISIBLE);
        mRefreshingPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshingForPullingUp() {

    }

    @Override
    public void refreshingOverForPullingDown() {
        if(DEBUG) Log.v(TAG, "refreshingOver");
    }

    @Override
    public void refreshingOverForPullingUp() {

    }

    @Override
    public int getMaxPullingDownDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxPullingUpDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMinPullingDownDistance() {
        return 0;
    }

    @Override
    public int getMinPullingUpDistance() {
        return 0;
    }
}
