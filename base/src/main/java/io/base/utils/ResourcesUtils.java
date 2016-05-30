package io.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by gaochao on 2015/10/10.
 */
public class ResourcesUtils {
    private ResourcesUtils(){}

    public static int getColor(Resources resources, int colorId){
        if(Build.VERSION.SDK_INT < 23){
            return resources.getColor(colorId);
        }else{
            return resources.getColor(colorId, null);
        }
    }

    public static Drawable getDrawable(Resources resources, int resId){
        if(Build.VERSION.SDK_INT < 22){
            return resources.getDrawable(resId);
        }else{
            return resources.getDrawable(resId, null);
        }
    }

    //DIP转换到像素
    public static int dpToPix(float dip, DisplayMetrics metrics){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }

    //DIP转换到像素
    public static int dpToPix(float dip, Context context){
        return dpToPix(dip, context.getResources().getDisplayMetrics());
    }

    //给drawable着色
    public static Drawable tintDrawable(Drawable drawable, int color){
        Drawable newDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(newDrawable, color);
        return newDrawable;
    }
}
