package io.oversky524.androidui.pullrefresh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.base.utils.ResourcesUtils;
import io.oversky524.androidui.R;
import io.oversky524.pullrefresh.OnPullListener;

/**
 * Created by gaochao on 2016/7/21.
 */
public class MeituanListener implements OnPullListener {
    private static final boolean DEBUG = true;
    private static final String TAG = MeituanListener.class.getSimpleName();

    private ImageView mTargetIv;
    private AnimationDrawable mDownInProgressAnimationDrawable;
    private AnimationDrawable mDownRefreshingAnimationDrawable;
    private boolean mDownInProgressSet;

    @Override
    public void initForDown() {

    }

    @Override
    public void initForUp() {

    }

    @Override
    public View getDownView(ViewGroup parent) {
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
    public View getUpView(ViewGroup parent) {
        return null;
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
    public void upInProgress(float dx, float totalDx, float dy, float totalDy) {

    }

    @Override
    public void refreshingForDown() {
        if(DEBUG) Log.v(TAG, "refreshing");
        mTargetIv.setScaleX(1);
        mTargetIv.setScaleY(1);
        mTargetIv.setBackground(mDownRefreshingAnimationDrawable);
        mDownRefreshingAnimationDrawable.stop();
        mDownRefreshingAnimationDrawable.start();
    }

    @Override
    public void refreshingForUp() {

    }

    @Override
    public void refreshingOverForDown() {
        if(DEBUG) Log.v(TAG, "refreshingOver");
        mDownInProgressSet = false;
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
        return mTargetIv.getMeasuredHeight();
    }

    @Override
    public int getMinUpDistance() {
        return 0;
    }
}
