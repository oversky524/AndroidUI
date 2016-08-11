package io.oversky524.androidui.pullrefresh;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.oversky524.pullrefresh.OnPullListener;

/**
 * Created by gaochao on 2016/7/20.
 */
public class DefaultListener implements OnPullListener {
    private static final boolean DEBUG = true;
    private static final String TAG = DefaultListener.class.getSimpleName();
    private TextView mPullingDownTv;
    private ProgressBar mRefreshingPb;

    @Override
    public void initForDown() {

    }

    @Override
    public void initForUp() {

    }

    @Override
    public View getDownView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(io.oversky524.pullrefresh.R.layout.default_refreshing_layout, parent, false);
        mPullingDownTv = (TextView)view.findViewById(io.oversky524.pullrefresh.R.id.pulling_down);
        mRefreshingPb = (ProgressBar)view.findViewById(io.oversky524.pullrefresh.R.id.refreshing);
        mPullingDownTv.setVisibility(View.VISIBLE);
        mRefreshingPb.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public View getUpView(ViewGroup parent) {
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
    public void refreshingForDown() {
        if(DEBUG) Log.v(TAG, "refreshing");
        mPullingDownTv.setVisibility(View.INVISIBLE);
        mRefreshingPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshingForUp() {

    }

    @Override
    public void refreshingOverForDown() {
        if(DEBUG) Log.v(TAG, "refreshingOver");
    }

    @Override
    public void refreshingOverForUp() {

    }

    @Override
    public int getMaxDownDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxUpDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMinDownDistance() {
        return 0;
    }

    @Override
    public int getMinUpDistance() {
        return 0;
    }
}
