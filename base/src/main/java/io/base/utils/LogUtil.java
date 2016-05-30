package io.base.utils;

import android.util.Log;

/**
 * Log工具
 */
public class LogUtil {

    public static boolean isLog = true; // 是否打印LOG

    public static void v(String tag,String msg){
        if (isLog) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag,String msg){
        if (isLog) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag,String msg){
        if (isLog) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag,String msg){
        if (isLog) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag,String msg){
        if (isLog) {
            Log.e(tag, msg);
        }
    }
}
