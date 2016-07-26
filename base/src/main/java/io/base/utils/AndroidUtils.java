package io.base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import io.base.BaseApplication;
import io.base.R;
import io.base.exceptions.ExceptionUtils;
import io.base.ui.ActivityLifeCycle;

/**
 * User: HouMingwei Date: 2014-11-26 Time: 16:33
 */
public class AndroidUtils {

    /**
     * 判断设备当前是否在线
     *
     * @param ctx
     * @return
     */
    public static boolean isOnline(Context ctx) {
        boolean flag = false;
        try {
            ConnectivityManager cwjManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = cwjManager.getActiveNetworkInfo();
            if (null == networkinfo || !networkinfo.isAvailable()) {
                flag = false;
            } else {
                flag = true;
            }
        } catch (Exception e) {
            flag = true;
        }
        return flag;
    }

    /**
     * 是否是WiFi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断SD卡是否存在
     *
     * @param ctx
     * @return
     */
    public static boolean sdcardReady(Context ctx) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        return true;
    }

    /**
     * 启动Activity
     *
     * @param activity
     * @param intent
     */
    public static void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
    public static void startActivityAndFinish(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        activityFinish(activity);
    }

    /**
     * 启动Activity返回参数
     *
     * @param activity
     * @param intent
     */
    public static void startActivityForResult(Activity activity, Intent intent, int i) {
        activity.startActivityForResult(intent, i);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    /**
     * 父容器启动Activity
     *
     * @param activity
     * @param intent
     */
    public static void startByParentActivity(Activity activity, Intent intent) {
        activity.getParent().startActivity(intent);
        activity.getParent().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    /**
     * 结束当前Activity
     *
     * @param activity
     */
    public static void activityFinish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    /**
     * 缩放启动
     *
     * @param activity
     * @param intent
     */
    public static void startActivityByZoom(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.zoom_enter, R.anim.hold);

    }

    /**
     * 缩放结束
     *
     * @param activity
     */
    public static void activityFinishByZoom(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.hold, R.anim.zoom_exit);
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    private static String sVersionName;
    public static String getVersionName() {
        if(sVersionName == null) {
            try {
                Context context = BaseApplication.getGlobalApp();
                sVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                ExceptionUtils.printExceptionStack(e);
                sVersionName = "";
            }
        }
        return sVersionName;
    }

    /**
     * 获取版本Code
     *
     * @param context
     * @return
     */
    private static int sVersionCode = -1;
    public static int getVersionCode() {
        if(sVersionCode == -1) {
            Context context = BaseApplication.getGlobalApp();
            int code = -1;
            // 获取PackageManager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;
            try {
                packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                code = packInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                ExceptionUtils.printExceptionStack(e);
            }
            sVersionCode = code;
        }
        return sVersionCode;
    }

    //获取状态栏的高度
    private static int gsStatusBarHeight = Integer.MIN_VALUE;

    public static int getStatusBarHeight(){
        return getStatusBarHeight(ActivityLifeCycle.getCurrentActivity());
    }

    public static int getStatusBarHeight(Activity activity) {
        if (gsStatusBarHeight > Integer.MIN_VALUE) {
            return gsStatusBarHeight;
        }
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight > 0) {
            gsStatusBarHeight = statusBarHeight;
            return statusBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            ExceptionUtils.printExceptionStack(e1);
        }
        gsStatusBarHeight = statusBarHeight;
        return statusBarHeight;
    }

    public static void hideKeyboard(Context context, View edit) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }
    }

    //获取当前进程名称
    public static String getAppName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : l) {
            if (runningAppProcessInfo.pid == pid) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    //在主线程运行
    private static Handler sMainHandler = new Handler(Looper.getMainLooper());
    public static void runOnMainThread(Runnable runnable){
        sMainHandler.post(runnable);
    }

    public static boolean isAppIntalled(String packageName, Context context){
        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
    * 获取设备ID
    * **/
    public static String getDeviceId(Context context) {
        try{
            JSONObject json = new JSONObject();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);
            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }
            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceId() {
        return getDeviceId(BaseApplication.getGlobalApp());
    }

    /**
     * 获取JSON格式的设备信息
     * */
    public static String getDeviceInfo(Context context){
        JSONObject jsonObject = new JSONObject();
        try {
            String deviceInfo = getDeviceId(context);
            jsonObject.put("model", "device:" + Build.DEVICE + ";brand:" + Build.BRAND);
            jsonObject.put("sys_ver", Build.VERSION.RELEASE);
            jsonObject.put("app_ver", getVersionName());
            jsonObject.put("platform", 2);
            jsonObject.put("did", deviceInfo);
//            jsonObject.put("channel", ChannelUtils.getChannelName(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static void hideInputMethod(Activity activity){
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = activity.getCurrentFocus();
        if(focus == null) focus = activity.getWindow().getDecorView();
        imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    public static void hideInputMethod(Context context, View focus){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    /**
     * 检查当前进程是否是前台进程
     * */
    public static boolean isForeground(Context context){
        final int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        for(int i=0; i<apps.size(); ++i){
            ActivityManager.RunningAppProcessInfo appProcessInfo = apps.get(i);
            if(appProcessInfo != null && appProcessInfo.pid == pid &&
                    appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }

    public static InputStream getInputStream(Uri uri) throws FileNotFoundException {
        switch (uri.getScheme()){
            case ContentResolver.SCHEME_FILE:
                File file = FileUtils.getFile(uri);
                return new FileInputStream(file);

            case ContentResolver.SCHEME_CONTENT:
            case ContentResolver.SCHEME_ANDROID_RESOURCE:
                return BaseApplication.getGlobalApp().getContentResolver().openInputStream(uri);

            default:
                throw new IllegalArgumentException("uri " + uri + " can't be transformed into a InputStream!");
        }
    }
}