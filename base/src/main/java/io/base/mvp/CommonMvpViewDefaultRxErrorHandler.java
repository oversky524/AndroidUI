package io.base.mvp;

import io.base.defaultclass.DefaultRxErrorHandler;

/**
 * Created by gaochao on 2016/3/9.
 */
public class CommonMvpViewDefaultRxErrorHandler<MvpView extends CommonMvpView, T> extends DefaultRxErrorHandler {
    private MvpPresenterWithData<MvpView, T> mPresenter;

    public CommonMvpViewDefaultRxErrorHandler(MvpPresenterWithData<MvpView, T> presenter){
        mPresenter = presenter;
    }

    @Override
    public void call(Throwable throwable) {
        super.call(throwable);
        mPresenter.getView().notifyDataSetChanged();
        mPresenter = null;
    }
}
