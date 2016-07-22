package io.oversky524.pullrefresh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.base.utils.ResourcesUtils;

/**
 * Created by gaochao on 2016/7/21.
 */
public class MeituanRefreshListener implements OnPullRefreshListener {
    private static final boolean DEBUG = true;
    private static final String TAG = MeituanRefreshListener.class.getSimpleName();

    private ImageView mTargetIv;
    private AnimationDrawable mDownInProgressAnimationDrawable;
    private AnimationDrawable mDownRefreshingAnimationDrawable;
    private boolean mDownInProgressSet;

    @Override
    public void init() {

    }

    @Override
    public View getRefreshView(ViewGroup parent) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.pull_down_new, parent, false);
        mTargetIv = (ImageView) view.findViewById(R.id.target);
        Resources resources = context.getResources();
        mDownInProgressAnimationDrawable = (AnimationDrawable) ResourcesUtils.getDrawable(resources,
                R.drawable.animation_pull_down_in_progress);
        mDownRefreshingAnimationDrawable = (AnimationDrawable) ResourcesUtils.getDrawable(resources,
                R.drawable.animation_pull_down_refreshing);
        return view;
    }

    @Override
    public void downInProgress(float dx, float totalDx, float dy, float totalDy) {
        int height = mTargetIv.getHeight();
        if(height > totalDy){
            mTargetIv.setBackgroundResource(R.mipmap.pull_image);
            mTargetIv.setPivotX(mTargetIv.getWidth()/2);
            mTargetIv.setPivotY(mTargetIv.getHeight());
            mTargetIv.setScaleX(totalDy/height);
            mTargetIv.setScaleY(totalDy/height);
        }else{
            mTargetIv.setScaleX(1);
            mTargetIv.setScaleY(1);
            if(!mDownInProgressSet) {
                mTargetIv.setBackground(mDownInProgressAnimationDrawable);
                mDownInProgressAnimationDrawable.stop();
                mDownInProgressAnimationDrawable.start();
            }
        }
    }

    @Override
    public void refreshing() {
        if(DEBUG) Log.v(TAG, "refreshing");
        mTargetIv.setScaleX(1);
        mTargetIv.setScaleY(1);
        mTargetIv.setBackground(mDownRefreshingAnimationDrawable);
        mDownRefreshingAnimationDrawable.stop();
        mDownRefreshingAnimationDrawable.start();
    }

    @Override
    public void refreshingOver() {
        if(DEBUG) Log.v(TAG, "refreshingOver");
        mDownInProgressSet = false;
    }

    @Override
    public int getMaxPullingDownDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMinPullingDownDistance() {
        return mTargetIv.getMeasuredHeight();
    }
}
