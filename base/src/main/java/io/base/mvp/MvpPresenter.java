package io.base.mvp;

import android.content.Context;

import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by gaochao on 2015/12/10.
 */
abstract public class MvpPresenter<MvpView> {
    private MvpView mView;
    private Context mContext;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private boolean mHasEventBus;

    protected void setHasEventBus(boolean hasEventBus){ mHasEventBus = hasEventBus; }

    public void attachView(MvpView view){ attachView(view, null); }

    public void attachView(MvpView view, Context context){
        mView = view;
        mContext = context;
        if(mHasEventBus) EventBus.getDefault().register(this);
    }

    public void detachView(){
        mView = null;
        mContext = null;
        mCompositeSubscription.unsubscribe();
        mCompositeSubscription = null;
        if(mHasEventBus) EventBus.getDefault().unregister(this);
    }

    public MvpView getView(){
        return mView;
    }

    public Context getContext(){ return mContext; }

    protected void addSubscription(Subscription s){ if(s != null) mCompositeSubscription.add(s); }

    protected void removeSubscription(Subscription s){ if(s != null) mCompositeSubscription.remove(s); }
}
