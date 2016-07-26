package io.base.mvp;

import java.util.ArrayList;

/**
 * Created by gaochao on 2016/6/23.
 */
public class MvpPresenterWithTwoData<MvpView, DataType, ListDataType> extends MvpPresenter<MvpView>{
    private DataType mData;

    public DataType data(){ return mData; }

    public void data(DataType data){ mData = data; }

    private ArrayList<ListDataType> mList = new ArrayList<>();

    public int size(){ return mList.size(); }

    public ListDataType item(int position){ return mList.get(position); }

    public ArrayList<ListDataType> list(){ return mList; }
}
