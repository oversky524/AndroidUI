package io.oversky524.androidui.pullrefresh;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.oversky524.androidui.R;
import io.oversky524.pullrefresh.OnPullListener;

/**
 * Created by gaochao on 2016/7/25.
 */
public class NuomiListener implements OnPullListener {
    private AnimationDrawable mRefreshingDrawable;
    private TextView mCoreTv;
    private TextView mLoadingMoreTv;

    @Override
    public void initForDown() {
        mCoreTv.setText(R.string.pulling_down_refresh);
    }

    @Override
    public void initForUp() {

    }

    @Override
    public View getDownView(ViewGroup parent) {
//        return null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pulling_down_nuomi, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.core);
        mCoreTv = textView;
        mRefreshingDrawable = (AnimationDrawable)(textView.getCompoundDrawables()[0]);
        return view;
    }

    @Override
    public View getUpView(ViewGroup parent) {
        return null;
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pulling_up_nuomi, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.loading_more);
        mLoadingMoreTv = textView;
        return view;*/
    }

    @Override
    public void downInProgress(float dx, float totalDx, float dy, float totalDy) {
        if(totalDy >= getMinDownDistance()) {
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
    public void refreshingForDown() {
        if(!mRefreshingDrawable.isRunning()) mRefreshingDrawable.start();
        mCoreTv.setText(R.string.loading_hard);
    }

    @Override
    public void refreshingForUp() {
//        mLoadingMoreTv.setText(R.string.loading_more);
    }

    @Override
    public void refreshingOverForDown() {

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
        return getMinUpDistance();
    }

    @Override
    public int getMinDownDistance() {
        return mCoreTv.getHeight();
    }

    @Override
    public int getMinUpDistance() {
        return mLoadingMoreTv.getHeight();
    }
}
