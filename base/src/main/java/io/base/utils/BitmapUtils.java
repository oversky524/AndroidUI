package io.base.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.base.model.Size;

/**
 * Created by gaochao on 2015/11/9.
 */
public class BitmapUtils {
    private BitmapUtils() { throw new AssertionError("No instance!"); }

    public static Size getBitmapSize(Uri uri, Context context) {
        String scheme = uri.getScheme();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int rotateDegrees = 0;
        switch (scheme) {
            case ContentResolver.SCHEME_CONTENT:
            case ContentResolver.SCHEME_ANDROID_RESOURCE:
                InputStream inputStream = null;
                try {
                    inputStream = context.getContentResolver().openInputStream(uri);
                    BitmapFactory.decodeStream(inputStream, null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (null != inputStream) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case ContentResolver.SCHEME_FILE:
                File file = FileUtils.getFile(uri);
                if (file != null) {
                    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    try {
                        rotateDegrees = getExifOrientationDegrees(
                                new ExifInterface(file.getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("uri " + uri.toString() + " is illegal");
        }
        return new Size(options.outWidth, options.outHeight, rotateDegrees);
    }

    public static Bitmap getBitmap(Uri uri, int width, int height, Context context) {
        Size size = getBitmapSize(uri, context);
        String scheme = uri.getScheme();
        BitmapFactory.Options options = new BitmapFactory.Options();
        int wRatio = (int) Math.ceil(size.width / (float) width);
        int hRatio = (int) Math.ceil(size.height / (float) height);
        options.inSampleSize = wRatio > hRatio ? wRatio : hRatio;
        Bitmap bitmap = null;
        switch (scheme) {
            case ContentResolver.SCHEME_CONTENT:
            case ContentResolver.SCHEME_ANDROID_RESOURCE:
                InputStream inputStream = null;
                try {
                    inputStream = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (null != inputStream) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case ContentResolver.SCHEME_FILE:
                bitmap = BitmapFactory.decodeFile(FileUtils.getFile(uri).getAbsolutePath(), options);
                break;

            default:
                throw new IllegalArgumentException("uri " + uri.toString() + " is illegal");
        }
        return bitmap;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, int maxThumbnailWidth, int maxThumbnailHeight) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        float scale = 1.f;
        if(width <= maxThumbnailWidth && height <= maxThumbnailHeight){
            return bitMap;
        }
        // 设置想要的大小
        int newWidth = maxThumbnailWidth;
        int newHeight = maxThumbnailHeight;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        scale = scaleHeight > scaleWidth ? scaleWidth : scaleHeight;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        return Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
    }

    public static int getSampleSize(int bitmapWidth, int bitmapHeight, int targetWidth, int targetHeight){
        int sampleSize = 1;
        if(targetHeight < bitmapHeight || targetWidth < bitmapWidth){
            final int halfWidth = bitmapWidth / 2;
            final int halfHeight = bitmapHeight / 2;
            while ((halfHeight / sampleSize) > targetHeight
                    && (halfWidth / sampleSize) > targetWidth) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }

    public static Bitmap getBitmap(Context context, int resId){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + resId);
        Size size = getBitmapSize(uri, context);
        BitmapFactory.Options options = new BitmapFactory.Options();
        if(size.width > dm.widthPixels || size.height > dm.heightPixels){
            options.inSampleSize = getSampleSize(size.width, size.height, dm.widthPixels, dm.heightPixels);
        }
        options.inScaled = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * Get the # of degrees an image must be rotated to match the given exif orientation.
     *
     * @param exifOrientation The exif orientation [1-8]
     * @return the number of degrees to rotate
     */
    public static int getExifOrientationDegrees(int exifOrientation) {
        final int degreesToRotate;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_ROTATE_90:
                degreesToRotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                degreesToRotate = 180;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                degreesToRotate = 270;
                break;
            default:
                degreesToRotate = 0;

        }
        return degreesToRotate;
    }

    /**
     * src isn't recycled
     * */
    public static Bitmap circleBitmap(Bitmap src, Bitmap dst){
        int width = src.getWidth(), height = src.getHeight();
        if(dst == null) dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        BitmapShader bitmapShader = new BitmapShader(src, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
        paint.setShader(bitmapShader);
        paint.setAntiAlias(true);
        float radius = Math.min(width, height)/2;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        return dst;
    }

    public static Bitmap getOrignalSrcBitmap(Resources resources, int resId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    //缩放系数
    private final static int SCALE = 8;

    /**
     * 模糊函数
     * @param context
     * @param sentBitmap
     * @param radius
     * @return
     */
    public static Bitmap doBlur(Context context, Bitmap sentBitmap, float radius, float scalex, float scaley) {
        if(sentBitmap==null) return null;
        if (radius <= 0 || radius > 25) radius = 25f;//范围在1-25之间
        if (radius<=6&& Build.VERSION.SDK_INT > 16) {//经测试，radius大于6后，fastBlur效率更高，并且RenderScript在api11以上使用
            Bitmap bitmap = Bitmap.createScaledBitmap(sentBitmap, (int)(sentBitmap.getWidth() * scalex),
                    (int)(sentBitmap.getHeight() * scaley),false);//先缩放图片，增加模糊速度
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            rs.destroy();
            return bitmap;
        }else{//快速模糊
            return fastBlur(sentBitmap,radius, scalex, scaley);
        }
    }

    public static Bitmap doBlur(Context context, Bitmap sentBitmap, float radius){
        float scale = 1/(float)SCALE;
        return doBlur(context, sentBitmap, radius, scale, scale);
    }

    /**
     * 快速模糊算法
     * @param sbitmap
     * @param radiusf
     * @return
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012

     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * If you are using this algorithm in your code please add
     * the following line:
     *
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */
    public static Bitmap fastBlur(Bitmap sbitmap, float radiusf, float scalex, float scaley){
        Bitmap bitmap = Bitmap.createScaledBitmap(sbitmap, (int)(sbitmap.getWidth() * scalex),
                (int)(sbitmap.getHeight() * scaley), false);//先缩放图片，增加模糊速度
        int radius = (int)radiusf;
        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static Bitmap fastBlur(Bitmap sbitmap, float radiusf){
        float scale = 1/(float)SCALE;
        return fastBlur(sbitmap, radiusf, scale, scale);
    }
}
