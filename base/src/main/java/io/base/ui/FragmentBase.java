package io.base.ui;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

import io.base.BaseApplication;

/**
 * Created by gaochao on 2015/9/30.
 */
public class FragmentBase extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        if(!BaseApplication.underTest()) {
            MobclickAgent.onPageStart(getClass().getName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!BaseApplication.underTest()) {
            MobclickAgent.onPageEnd(getClass().getName());
        }
    }
}
