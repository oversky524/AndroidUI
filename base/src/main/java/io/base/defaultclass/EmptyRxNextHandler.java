package io.base.defaultclass;

import rx.functions.Action1;

/**
 * Created by gaochao on 2016/2/19.
 */
public class EmptyRxNextHandler<T> implements Action1<T> {
    @Override
    public void call(T t) {

    }
}
