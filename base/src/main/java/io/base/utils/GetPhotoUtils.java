package io.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import io.base.BaseApplication;
import io.base.R;

//import com.loopj.android.http.RequestParams;

/**
 * Created by gaochao on 2015/4/27.
 */
public class GetPhotoUtils {
    private static final String IMAGE_BASE_DIR = BaseApplication.getGlobalApp()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private static final String HEAD_PORTRAIT_PATH = IMAGE_BASE_DIR + "/head.jpg";
    private static HashMap<Uri, String> gsUriToUrl = new HashMap<>();

    public static String getUrl(Uri uri){
        return gsUriToUrl.get(uri);
    }

    public static void putUrl(Uri uri, String url){
        gsUriToUrl.put(uri, url);
    }

    public static void removeUri(Uri uri){
        gsUriToUrl.remove(uri);
    }

    public interface OnGetPhotoFinishListener{
        void onFinish(Uri uri);
    }

    public static final int    NONE              = 0;
    public static final int    CAMERA            = 1;                                    // 拍照
    public static final int    GALLERY           = 2;                                    // 相册
    public static final int    CUT               = 3;
    public static final String IMAGE_UNSPECIFIED = "image/*";

    /**
     * 相机
     */
    static public void getImageByCamera(Activity activity){
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(HEAD_PORTRAIT_PATH)));
        activity.startActivityForResult(intent, CAMERA);
    }

    static public Uri getImageByCameraWithRandomName(Activity activity){
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(new File(getImageRandomPath()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, CAMERA);
        return uri;
    }

    static public Uri getImageByCameraWithRandomName(Fragment activity){
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(new File(getImageRandomPath()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, CAMERA);
        return uri;
    }

    /**
     * 相册
     */
    static public void getImageByPhoto(Activity activity){
        Intent intent = new Intent (Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);// 获取所有图片信息
        activity.startActivityForResult(intent, GALLERY);
    }

    /**
     * 相册
     */
    static public void getImageByPhoto(Fragment activity){
        Intent intent = new Intent (Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);// 获取所有图片信息
        activity.startActivityForResult(intent, GALLERY);
    }

    /**
     * @param uri
     * @Title: cutPhoto
     * @Author: HouMingWei
     * @Since: 2014-3-25下午7:21:03
     */
    static public void cutPhoto(Uri uri, Activity activity, String outputFilePath, int outputWidth, int outputHeight){
        Intent intent = new Intent ("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra ("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputWidth);
        intent.putExtra ("outputY", outputHeight);
        gsUriAfterCutting = Uri.fromFile(new File(outputFilePath));
        intent.putExtra (MediaStore.EXTRA_OUTPUT, gsUriAfterCutting);
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, CUT);
    }

    static public void onActivityResultWithoutCut(int requestCode,int resultCode,Intent data,
                                                  OnGetPhotoFinishListener listener){
        if (resultCode == NONE) { return; }// 返回错误直接退出
        if (requestCode == CAMERA) {
            listener.onFinish(sCameraPhotoUri);
        }
        if (requestCode == GALLERY) {
            listener.onFinish(data.getData());
        }
    }

    private static Uri gsUriAfterCutting;
    static public void onActivityResult(int requestCode,int resultCode,Intent data, Activity activity,
                                        int outputWidth, int outputHeight, OnGetPhotoFinishListener listener, String outputFilePath){
        if (resultCode != Activity.RESULT_OK) return;// 返回错误直接退出
        if (requestCode == CAMERA) {
            File f = new File (HEAD_PORTRAIT_PATH);
            cutPhoto (Uri.fromFile(f), activity, outputFilePath, outputWidth, outputHeight);
        }
        if (requestCode == GALLERY) {
            cutPhoto (data.getData (), activity, outputFilePath, outputWidth, outputHeight);
        }
        if (requestCode == CUT && listener != null) {
            listener.onFinish(gsUriAfterCutting);
        }
    }
    static public void onActivityResult(int requestCode,int resultCode,Intent data, Activity activity,
                                        int outputWidth, int outputHeight, OnGetPhotoFinishListener listener){
        onActivityResult(requestCode, resultCode, data, activity, outputWidth, outputHeight, listener,
                getImageRandomPath());
    }

    public static String getImageRandomPath(){
        return IMAGE_BASE_DIR + "/" + (new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())) + ".jpg";
    }


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void showHeadPortraitSelectionDialog(final Activity activity){
        final Dialog dialog = new Dialog(activity);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_photo_for_head_portrait);
        window.getDecorView().setBackgroundResource(R.drawable.bg_shape_select_photo_for_head_portrait);
        dialog.findViewById(R.id.get_photo_from_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPhotoUtils.getImageByPhoto(activity);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.get_photo_from_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPhotoUtils.getImageByCamera(activity);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private static Uri sCameraPhotoUri;
    public static void showHeadPortraitSelectionDialogWithoutCut(final Activity activity){
        showHeadPortraitSelectionDialogWithoutCut(activity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPhotoUtils.getImageByPhoto(activity);
            }
        });
    }

    public static void showHeadPortraitSelectionDialogWithoutCut(final Fragment activity){
        showHeadPortraitSelectionDialogWithoutCut(activity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPhotoUtils.getImageByPhoto(activity);
            }
        });
    }

    public static void showHeadPortraitSelectionDialogWithoutCut(final Activity activity,
                                                                 final View.OnClickListener listener){
        final Dialog dialog = new Dialog(activity);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_photo_for_head_portrait);
        window.getDecorView().setBackgroundResource(R.drawable.bg_shape_select_photo_for_head_portrait);
        dialog.findViewById(R.id.get_photo_from_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.get_photo_from_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCameraPhotoUri = getImageByCameraWithRandomName(activity);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static void showHeadPortraitSelectionDialogWithoutCut(final Fragment activity,
                                                                 final View.OnClickListener listener){
        final Dialog dialog = new Dialog(activity.getContext());
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_photo_for_head_portrait);
        window.getDecorView().setBackgroundResource(R.drawable.bg_shape_select_photo_for_head_portrait);
        dialog.findViewById(R.id.get_photo_from_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.get_photo_from_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCameraPhotoUri = getImageByCameraWithRandomName(activity);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
