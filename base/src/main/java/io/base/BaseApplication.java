package io.base;

import android.app.Application;

import com.ciwei.umeng.UmengUtils;

import io.base.ui.ActivityLifeCycle;
import io.base.utils.SharedPreferencesUtils;

/**
 * Created by gaochao on 2015/12/2.
 */
public class BaseApplication extends Application {
    private static boolean sUnderTest = false;
    public static boolean underTest() { return sUnderTest; }
    public static void underTest(boolean test){ sUnderTest = test; }

    private static Application sApplication;

    public static Application getGlobalApp(){ return sApplication; }

    private static boolean sDebug;
    protected static void setDebug(boolean debug){ sDebug = debug; }
    public static boolean debug(){ return sDebug; }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        SharedPreferencesUtils.initConfigFile(this);
        mActivityLifeCycle = new ActivityLifeCycle();
        registerActivityLifecycleCallbacks(mActivityLifeCycle);
        if(!sUnderTest) UmengUtils.init(sDebug, this);
    }

    private ActivityLifeCycle mActivityLifeCycle;
    protected ActivityLifeCycle getActivityLifeCycle(){ return mActivityLifeCycle; }
}
