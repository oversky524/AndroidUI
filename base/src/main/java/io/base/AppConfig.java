package io.base;

import android.os.Environment;

import java.io.File;

import io.base.utils.FileUtils;

/**
 * Created by Vernon on 15/2/11.
 */
public class AppConfig {
    private AppConfig(){}

    public static void clearCaches(){
        FileUtils.clearDirectory(new File(CACHE_PATH_HOME));
    }

    /**
     * 项目配置
     */
    public static final String APP_NAME = "ciwei";                                                   // 项目名称
    public static final String APP_PACKAGE_NAME = BaseApplication.getGlobalApp().getPackageName();

    /**
     * sharedPreferences配置
     */

    public static final String SP_USER_INFO = APP_PACKAGE_NAME + ".userinfo";
    public static final String SP_SWITCH_PUSH = APP_PACKAGE_NAME + ".push";                                // 热点推送

    /**
     * SD卡参数配置
     */
    // 根目录
    public static final String CACHE_PATH_HOME = Environment.getExternalStorageDirectory() + "/" + "ciwei";
    // 图片缓存地址
    public static final String CACHE_PATH_PIC = CACHE_PATH_HOME + "/pic/";

    public static final String SP_NEW_VERSION = APP_PACKAGE_NAME + ".new.version";
    // 自己的头像缓存文件
    public static final String SELF_HEAD_PHOTO_CACHE_FILE = AppConfig.CACHE_PATH_PIC + "icon_head.jpg";
    //临时文件路径
    public static final String TEMP_FILE_DIR = AppConfig.CACHE_PATH_HOME + "/temp/";

}
