package io.base.ui;

import android.app.Activity;
import android.os.Bundle;

import io.base.utils.AndroidUtils;

/**
 * Created by gaochao on 2015/10/15.
 * 监控应用所有Activity的生命周期
 */
public class ActivityLifeCycle extends ActivityLifecycleCallbacksAdapter {
    private static Activity sCurrentActivity;

    public static Activity getCurrentActivity(){
        return sCurrentActivity;
    }

    private static void setCurrentActivity(Activity activity){
        sCurrentActivity = activity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        setCurrentActivity(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        setCurrentActivity(activity);
        if(mCallback != null) mCallback.onEnterForeground();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(getCurrentActivity() == activity){
            setCurrentActivity(null);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(mCallback != null && !AndroidUtils.isForeground(activity)) mCallback.onEnterBackground();
    }

    public interface Callback{
        void onEnterForeground();
        void onEnterBackground();
    }

    private Callback mCallback;
    public Callback setCallback(Callback callback){
        Callback old = mCallback;
        mCallback = callback;
        return old;
    }
}
