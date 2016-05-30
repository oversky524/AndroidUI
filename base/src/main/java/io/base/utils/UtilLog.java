package io.base.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gaochao on 2015/9/1.
 */
public class UtilLog {
    private UtilLog(){}

    private static Context gsContext;
    public static void setContext(Context context){
        gsContext = context;
    }

    private static boolean gsShowLog = true;
    public static void setShowLog(boolean show){
        gsShowLog = show;
    }

    private static boolean gsShowLogInToast = true;
    public static void setShowLogForm(boolean inToast){
        gsShowLogInToast = inToast;
    }

    public static void showTextV(String text, String tag){
        if(!gsShowLog){
            return;
        }

        if(gsShowLogInToast){
            Toast.makeText(gsContext, text, Toast.LENGTH_SHORT).show();
        }else{
            Log.v(tag, text);
        }
    }

    public static void showTextE(String text, String tag){
        if(!gsShowLog){
            return;
        }

        if(gsShowLogInToast){
            Toast.makeText(gsContext, text, Toast.LENGTH_SHORT).show();
        }else{
            Log.e(tag, text);
        }
    }
}
