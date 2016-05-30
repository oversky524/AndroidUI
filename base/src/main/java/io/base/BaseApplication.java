package io.base;

import android.app.Application;

import com.ciwei.umeng.UmengUtils;

import io.base.ui.ActivityLifeCycle;
import io.base.utils.SharedPreferencesUtils;

/**
 * Created by gaochao on 2015/12/2.
 */
public class BaseApplication extends Application {
    private static Application sApplication;

    public static Application getGlobalApp(){ return sApplication; }

    private boolean mDebug;
    protected void setDebug(boolean debug){ mDebug = debug; }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        SharedPreferencesUtils.initConfigFile(this);
        registerActivityLifecycleCallbacks(new ActivityLifeCycle());
        UmengUtils.init(mDebug, this);
    }
}
