package io.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import io.base.BaseApplication;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by gaochao on 2015/12/2.
 */
public class ImageLoadUtils {
    private ImageLoadUtils(){}

    public static void intoGlobal(String url, ImageView imageView){
        Glide.with(BaseApplication.getGlobalApp()).load(url).into(imageView);
    }

    public static void intoGlobalCenterCrop(Uri uri, ImageView imageView){
        Glide.with(BaseApplication.getGlobalApp()).load(uri).centerCrop().into(imageView);
    }

    public static void setBackground(String url, final View target){
        Context context = BaseApplication.getGlobalApp();
        Glide.with(context).load(url).transform(new BitmapTransformation(context) {
            @Override
            protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                Bitmap bitmap = BitmapUtils.fastBlur(toTransform, 8f, 1f, 1f);
                return bitmap;
            }

            @Override
            public String getId() {
                return "gauss-blur";
            }
        }).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                io.base.utils.ViewUtils.setBackground(resource, target);
            }
        });
    }

    public static void intoGlobalCenterCrop(String url, int width, int height, ImageView imageView){
        Glide.with(BaseApplication.getGlobalApp()).load(url).override(width, height).centerCrop().into(imageView);
    }

    public static void intoGlobalCenterCrop(String url, int width, int height, ImageView imageView, int placeholder){
        Glide.with(BaseApplication.getGlobalApp()).load(url).override(width, height).centerCrop().placeholder(placeholder).into(imageView);
    }

    public static void intoGlobalCenterCrop(File file, int width, int height, ImageView imageView){
        Glide.with(BaseApplication.getGlobalApp()).load(file).override(width, height).centerCrop().into(imageView);
    }

    private static class CircleTransformation extends BitmapTransformation {

        public CircleTransformation(Context context) {
            super(context);
        }

        @Override
        public String getId() {
            return "io.ciwei.circle";
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int width = toTransform.getWidth(), height = toTransform.getHeight();
            Bitmap dst = pool.get(width, height, Bitmap.Config.ARGB_8888);
            if(dst == null){
                dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
            BitmapUtils.circleBitmap(toTransform, dst);
            return dst;
        }
    }

    public static void clearCaches(){
        Observable.just(null).subscribeOn(Schedulers.io()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Glide.get(BaseApplication.getGlobalApp()).clearDiskCache();
            }
        });
    }

    public static void intoGlobalWithCircle(final String url, final ImageView imageView, final int placeholder) {
        Glide.with(BaseApplication.getGlobalApp()).load(url).asBitmap().transform(new CircleTransformation(BaseApplication.getGlobalApp()))
                .placeholder(placeholder).into(imageView);
    }
}
