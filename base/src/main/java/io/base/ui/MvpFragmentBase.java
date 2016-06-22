package io.base.ui;

import android.content.Context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.base.mvp.MvpPresenter;

/**
 * Created by gaochao on 2016/2/26.
 */
public class MvpFragmentBase<T extends MvpPresenter> extends FragmentBaseWithEventBus {
    private T mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Type type = getClass().getGenericSuperclass();
        Class real = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
        try {
            mPresenter = (T)real.newInstance();
            mPresenter.attachView(this, context);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
        mPresenter = null;
    }

    protected T presenter(){ return mPresenter; }
}
