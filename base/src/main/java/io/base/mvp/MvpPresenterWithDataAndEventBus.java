package io.base.mvp;

import android.content.Context;

import de.greenrobot.event.EventBus;

/**
 * Created by gaochao on 2016/3/9.
 */
public class MvpPresenterWithDataAndEventBus<MvpView, T> extends MvpPresenterWithData<MvpView, T> {

    @Override
    public void detachView() {
        super.detachView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void attachView(MvpView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void attachView(MvpView view, Context context) {
        super.attachView(view, context);
        EventBus.getDefault().unregister(this);
    }
}
