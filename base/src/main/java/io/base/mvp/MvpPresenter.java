package io.base.mvp;

import android.content.Context;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by gaochao on 2015/12/10.
 */
abstract public class MvpPresenter<MvpView> {
    private MvpView mView;
    private Context mContext;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public void attachView(MvpView view){
        mView = view;
    }

    public void attachView(MvpView view, Context context){
        mView = view;
        mContext = context;
    }

    public void detachView(){
        mView = null;
        mContext = null;
        mCompositeSubscription.unsubscribe();
        mCompositeSubscription = null;
    }

    public MvpView getView(){
        return mView;
    }

    public Context getContext(){ return mContext; }

    protected void addSubscription(Subscription subscription){ mCompositeSubscription.add(subscription); }
}
