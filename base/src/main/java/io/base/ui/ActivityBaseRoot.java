package io.base.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.greenrobot.event.EventBus;
import io.base.R;
import io.base.utils.AndroidUtils;
import io.base.utils.ResourcesUtils;

/**
 * Created by gaochao on 2015/11/14.
 */
public class ActivityBaseRoot extends AppCompatActivity {
    private SystemBarTintManager mSystemBarTintManager;
    private boolean mSetStatusBarOnce;
    private boolean mSupportEventBus;
    protected void setSupportEventBus(boolean support){ mSupportEventBus = support; }

    protected SystemBarTintManager systemBarTintManager(){ return mSystemBarTintManager; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mSupportEventBus) EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSupportEventBus) EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mSetStatusBarOnce){
            setStatusBar();
            mSetStatusBarOnce = true;
        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(this, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        mSystemBarTintManager = tintManager;
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(ResourcesUtils.getColor(getResources(), R.color.colorPrimary));
        // enable navigation bar tint
//        tintManager.setNavigationBarTintEnabled(true);
    }

    protected static Toolbar initToolbar(final AppCompatActivity activity, String title){
        return initToolbar(activity, title, -1, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AndroidUtils.activityFinish(activity);
                    }
                });
    }

    protected static Toolbar initToolbar(final AppCompatActivity activity, String title, int navigationResId){
        return initToolbar(activity, title, -1, navigationResId, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AndroidUtils.activityFinish(activity);
                    }
                });
    }

    protected static Toolbar initToolbar(final AppCompatActivity activity, String title, int logoResId, int navigationResId,
                                         View.OnClickListener listener){
        Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        if(null == toolbar){
            return toolbar;
        }
        int color = Color.WHITE;
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(color);
        activity.setSupportActionBar(toolbar);
        if(logoResId != -1){
            toolbar.setLogo( logoResId);
        }
        if(navigationResId != -1){
            toolbar.setNavigationIcon(navigationResId);
        }else{
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(ResourcesUtils.tintDrawable(toolbar.getNavigationIcon(), color));
        toolbar.setNavigationOnClickListener(listener);
        return toolbar;
    }
}
