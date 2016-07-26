package io.base.ui;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import io.base.BaseApplication;
import io.base.utils.AndroidUtils;

/**
 * Created by gaochao on 2015/10/15.
 * 监控应用所有Activity的生命周期
 */
public class ActivityLifeCycle extends ActivityLifecycleCallbacksAdapter {
    private static Activity sCurrentActivity;
    private static boolean sInBackground;

    public static Activity getCurrentActivity(){
        return sCurrentActivity;
    }

    private static void setCurrentActivity(Activity activity){
        sCurrentActivity = activity;
    }

    private static ArrayList<Activity> sActivities = new ArrayList<>();

    public interface OnClearActivityConditionListener{
        /**
         * @return true that a activity can be finished
         * */
        boolean isFinished(Activity activity);
    }

    public static void clearActivities(OnClearActivityConditionListener listener){
        ArrayList<Activity> clearedActivities = new ArrayList<>();
        for(Activity activity : sActivities){
            if(listener.isFinished(activity)) clearedActivities.add(activity);
        }
        sActivities.removeAll(clearedActivities);
        for(Activity activity : clearedActivities){
            activity.finish();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        setCurrentActivity(activity);
        sActivities.add(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        setCurrentActivity(activity);
        if(!BaseApplication.underTest()) {
            MobclickAgent.onPageStart(activity.getClass().getName());
            MobclickAgent.onResume(activity);
        }
        if(mCallback != null && sInBackground){
            mCallback.onEnterForeground();
            sInBackground = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if(!BaseApplication.underTest()) {
            MobclickAgent.onPageEnd(activity.getClass().getName());
            MobclickAgent.onPause(activity);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(getCurrentActivity() == activity){
            setCurrentActivity(null);
        }
        sActivities.remove(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(mCallback != null && !AndroidUtils.isForeground(activity)){
            mCallback.onEnterBackground();
            sInBackground = true;
        }
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
