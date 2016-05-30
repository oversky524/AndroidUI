package io.base.ui;

import io.base.utils.AndroidUtils;

/**
 * Created by gaochao on 2015/7/27.
 * 没有Fragment的Activity基类
 */
public class ActivityBase extends ActivityBaseRoot {

    @Override
    public void onBackPressed() {
        AndroidUtils.activityFinish(this);
    }
}
