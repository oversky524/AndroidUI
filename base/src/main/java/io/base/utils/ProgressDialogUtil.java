package io.base.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import io.base.R;

/**
 * @ClassName: ProgressUtil
 * @Title:
 * @Description:
 * @Author:wuzhen
 * @Since:2013-12-4下午10:58:13
 * @Version:1.0
 */
public final class ProgressDialogUtil {
    private ProgressDialogUtil(){}

    private static Dialog dialog = null;

    public static void showCustomProgressDialog(Context context, int msgResId){
        showCustomProgressDialog(context, context.getString(msgResId));
    }

    public static void showCustomProgressDialog(Context context,String msg){
        try {
            if (dialog == null || !dialog.isShowing ()) {
                dialog = new Dialog (context, R.style.dialog);
            }
//            View view=View.inflate(context, R.layout.dialog_loading,null);
            dialog.setContentView (R.layout.dialog_loading);
            dialog.setCancelable(true);
            TextView tv_msg= (TextView) dialog.findViewById(R.id.tv_msg);
            tv_msg.setText(msg);
            dialog.show ();
        } catch (Exception e) {
            ExceptionUtils.printExceptionStack(e);
        }
    }

    public static void dismiss(){
        try {
            if (dialog != null && dialog.isShowing ()) {
                dialog.dismiss ();
                dialog = null;
            }
        } catch (Exception e) {
            ExceptionUtils.printExceptionStack(e);
        }
    }
}
