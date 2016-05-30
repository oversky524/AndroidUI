package io.base.utils;

import android.util.Log;

/**
 * Created by gaochao on 2015/11/16.
 */
public class DebugLogUtils {
    static private boolean debug = false;
    private DebugLogUtils(){}
    public static void setDebug(boolean debug){ DebugLogUtils.debug = debug; }

    public static void d(String tag, String log){
        if(debug){
            Log.d(tag, log);
        }
    }

    public static void i(String tag, String log){
        if(debug){
            Log.i(tag, log);
        }
    }

    public static void w(String tag, String log){
        if(debug){
            Log.w(tag, log);
        }
    }

    public static void v(String tag, String log){
        if(debug){
            Log.v(tag, log);
        }
    }
}
