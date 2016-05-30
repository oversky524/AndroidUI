package io.base.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaochao on 2015/8/25.
 */
public class UtilRx {
    private UtilRx(){}

    private static final Observable.Transformer sIoMainTransformer = new  Observable.Transformer() {
        @Override public Object call(Object observable) {
            return ((Observable)observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static <T> Observable.Transformer<T, T> getIoMainTransformaer() {
        return (Observable.Transformer<T, T>) sIoMainTransformer;
    }

    private static final Observable.Transformer sNewThreadMainTransformer = new  Observable.Transformer() {
        @Override public Object call(Object observable) {
            return ((Observable)observable).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static <T> Observable.Transformer<T, T> getNewThreadMainTransformaer() {
        return (Observable.Transformer<T, T>) sNewThreadMainTransformer;
    }
}
