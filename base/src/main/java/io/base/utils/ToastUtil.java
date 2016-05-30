package io.base.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @ClassName: ToastUtil
 * @Title:
 * @Author:HouMingWei
 * @Since:2014-2-28下午12:27:56
 * @Version:1.0
 */
public class ToastUtil {
    private static final String TAG = ToastUtil.class.getSimpleName();
    private ToastUtil(){}

    /**
     * 打印toast(String)
     *
     * @param context
     * @param msg
     */
    public static void show(Context context,String msg){
        if(context == null){
            LogUtil.e(TAG, "content == null");
            return;
        }
        if (msg == null || msg.equals ("")) { return; }
        Toast toast = Toast.makeText (context, msg, Toast.LENGTH_SHORT);
        toast.setGravity (Gravity.CENTER, 0, -60);
        toast.show ();
    }
    /**
     * 打印toast(int)
     *
     * @param context
     * @param resourceId
     */
    public static void show(Context context,int resourceId){
        if(context == null){
            LogUtil.e(TAG, "content == null");
            return;
        }
        Toast toast = Toast.makeText (context, resourceId, Toast.LENGTH_SHORT);
        toast.setGravity (Gravity.CENTER, 0, -60);
        toast.show ();
    }

    /**
     * 带图片的toast
     *
     * @param context
     * @param resourceId
     */
    public static void showImageToast(Context context,int resourceId){
        ImageView imageView = new ImageView (context);
        imageView.setBackgroundResource (resourceId);
        Toast toast = new Toast (context);
        toast.setGravity (Gravity.CENTER, 0, -60);
        toast.setView (imageView);
        toast.show ();
    }

}
