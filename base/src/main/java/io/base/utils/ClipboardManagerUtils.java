package io.base.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by gaochao on 2015/11/15.
 */
public class ClipboardManagerUtils {
    private ClipboardManagerUtils(){}

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     * @param content
     */
    public static void copy(String content, Context context)
    {
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(new ClipData("copy", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(content)));
    }
    /**
     * 实现粘贴功能
     * add by wangqianzhou
     * @param context
     * @return
     */
    public static String paste(Context context)
    {
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = cmb.getPrimaryClip();
        return clipData.getItemAt(0).getText().toString();
    }
}
