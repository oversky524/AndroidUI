package io.oversky524.pullrefresh;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by gaochao on 2016/7/25.
 */
public class NuomiRefreshListener implements OnPullRefreshListener{
    private AnimationDrawable mRefreshingDrawable;
    private TextView mCoreTv;
    private TextView mLoadingMoreTv;

    @Override
    public void initForPullingDown() {
        mCoreTv.setText(R.string.pulling_down_refresh);
    }

    @Override
    public void initForPullingUp() {

    }

    @Override
    public View getPullingDownView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pulling_down_nuomi, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.core);
        mCoreTv = textView;
        mRefreshingDrawable = (AnimationDrawable)(textView.getCompoundDrawables()[0]);
        return view;
    }

    @Override
    public View getPullingUpView(ViewGroup parent) {
        return null;
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pulling_up_nuomi, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.loading_more);
        mLoadingMoreTv = textView;
        return view;*/
    }

    @Override
    public void downInProgress(float dx, float totalDx, float dy, float totalDy) {
        if(totalDy >= getMinPullingDownDistance()) {
            mRefreshingDrawable.stop();
            mRefreshingDrawable.start();
            mCoreTv.setText(R.string.releasing_refresh);
        }else {
            mRefreshingDrawable.stop();
            mCoreTv.setText(R.string.pulling_down_refresh);
        }
    }

    @Override
    public void upInProgress(float dx, float totalDx, float dy, float totalDy) {

    }

    @Override
    public void refreshingForPullingDown() {
        mCoreTv.setText(R.string.loading_hard);
    }

    @Override
    public void refreshingForPullingUp() {
//        mLoadingMoreTv.setText(R.string.loading_more);
    }

    @Override
    public void refreshingOverForPullingDown() {

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
        return getMinPullingUpDistance();
    }

    @Override
    public int getMinPullingDownDistance() {
        return mCoreTv.getHeight();
    }

    @Override
    public int getMinPullingUpDistance() {
        return mLoadingMoreTv.getHeight();
    }
}
