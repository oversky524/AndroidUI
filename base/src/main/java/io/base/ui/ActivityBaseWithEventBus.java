package io.base.ui;

import android.os.Bundle;

import de.greenrobot.event.EventBus;
import io.base.mvp.MvpPresenter;

/**
 * Created by gaochao on 2016/3/1.
 */
public class ActivityBaseWithEventBus<T extends MvpPresenter> extends PresenterBaseActivity<T> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
