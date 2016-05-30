package io.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {
    private static SharedPreferences sp   = null;
    private static Editor edit = null;

    /**
     * 初始化配置文件，在splash界面初始化
     *
     * @param context
     */
    public static void initConfigFile(Context context){
        sp = context.getSharedPreferences (context.getPackageName() + "_main", Context.MODE_PRIVATE);
        edit = sp.edit ();
        edit.commit ();
    }

    /**
     * 保存str
     *
     * @param key
     * @param value
     */
    public static void putString(String key,String value){
        edit.putString (key, value).commit();
    }

    /**
     * 获取字符串从SharedPreferences
     *
     * @param key
     *            失败返回""
     */
    public static String getString(String key){
        return sp.getString(key, "");
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String key,String defaultValue){
        return sp.getString (key, defaultValue);
    }

    /**
     * 获取int
     *
     * @param key
     * @return 失败返回-1
     */
    public static int getInt(String key){
        return sp.getInt(key, 0);
    }

    public static void putInt(String key,int value){
        edit.putInt (key, value).commit ();
    }

    /**
     * 获取long型
     *
     * @param key
     * @return 失败返回-1
     */
    public static Long getLong(String key){
        return sp.getLong(key, 0);
    }

    public static void putLong(String key,long value){
        edit.putLong(key, value).commit ();
    }

    /**
     * 获取boolean
     *
     * @param key
     * @return 失败返回false
     */
    public static boolean getBoolean(String key){
        return sp.getBoolean(key, false);
    }

    /**
     * 带默认值的
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key,boolean defaultValue){
        return sp.getBoolean (key, defaultValue);
    }

    public static void putBoolean(String key,boolean value){
        edit.putBoolean (key, value).commit ();
    }

    /**
     * 获取float
     *
     * @param key
     * @return 失败返回-1
     */
    public static Float getFloat(String key){
        return sp.getFloat(key, 0);
    }

    public static void putFloat(String key,Float value){
        edit.putFloat(key, value).commit ();
    }

    public static void clear(){
        int versionCode = SharedPreferencesUtils.getVersionCode();
        edit.clear ().commit();
        SharedPreferencesUtils.setVersionCode(versionCode);
    }

    /**
     * 获取与保存版本号
     * */
    private static final String KEY_VERSION_CODE = "KEY_VERSION_CODE";
    public static int getVersionCode(){
        return getInt(KEY_VERSION_CODE);
    }
    public static void setVersionCode(int versionCode){
        putInt(KEY_VERSION_CODE, versionCode);
    }
}
