package io.base.defaultclass;

import io.base.exceptions.ExceptionUtils;
import rx.functions.Action1;

/**
 * Created by gaochao on 2016/2/18.
 */
public class DefaultRxErrorHandler implements Action1<Throwable> {
    @Override
    public void call(Throwable throwable) {
        ExceptionUtils.printExceptionStack(throwable);
    }
}
