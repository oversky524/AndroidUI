package io.base.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by gaochao on 2015/10/15.
 * 监控应用所有Activity的生命周期
 */
public class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
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
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        setCurrentActivity(activity);
        MobclickAgent.onPageStart(activity.getClass().getName());
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        MobclickAgent.onPageEnd(activity.getClass().getName());
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(getCurrentActivity() == activity){
            setCurrentActivity(null);
        }
    }
}
