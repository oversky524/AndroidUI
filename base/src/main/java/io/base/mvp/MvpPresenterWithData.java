package io.base.mvp;

import java.util.ArrayList;

/**
 * Created by gaochao on 2016/3/9.
 */
public class MvpPresenterWithData<MvpView, DataType> extends MvpPresenter<MvpView> {
    private ArrayList<DataType> mData = new ArrayList<>();

    public int size(){ return mData.size(); }

    public DataType getItem(int position){ return mData.get(position); }

    public ArrayList<DataType> data(){ return mData; }
}
