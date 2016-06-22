package io.base.ui;

import android.os.Bundle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.base.mvp.MvpPresenter;

/**
 * Created by gaochao on 2016/3/16.
 */

interface PresenterMarker<T>{}

public class PresenterBaseActivity<T extends MvpPresenter> extends ActivityBase implements PresenterMarker<T> {
    private T mPresenter;

    public PresenterBaseActivity(){
        Type type = getClass().getGenericSuperclass();
        Class real = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
        try {
            mPresenter = (T)real.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.attachView(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        mPresenter = null;
    }

    protected T presenter(){ return mPresenter; }
}
