package io.base.mvp;

/**
 * Created by gaochao on 2016/6/23.
 */
public class MvpPresenterWithObject<MvpView, DataType> extends MvpPresenter<MvpView> {
    private DataType mData;

    public DataType data(){ return mData; }

    public void data(DataType data){ mData = data; }
}
