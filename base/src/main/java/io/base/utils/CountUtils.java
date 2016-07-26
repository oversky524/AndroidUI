package io.base.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 倒计时
 * 
 * @author zhangqiang Created by Vernon on 15/3/8.
 */
public class CountUtils extends CountDownTimer {

    /**
     * @param millisInFuture
     *            The number of millis in the future from the call to {@link #start()} until the countdown is done and {@link #onFinish()} is called.
     * @param countDownInterval
     *            The interval along the way to receive {@link #onTick(long)} callbacks.
     */
    private TextView  btn_time;
    private Context context;
    private CharSequence text;

    /*
    * @param millisInFuture,持续时间
    * **/
    public CountUtils(long millisInFuture, long countDownInterval, TextView btn_time, Context context) {
        super (millisInFuture, countDownInterval);
        this.btn_time = btn_time;
        text = btn_time.getText();
        this.context=context;
    }

    @Override
    public void onTick(long millisUntilFinished){
//        btn_time.setTextColor(Color.RED);
//        btn_time.setText ("重新获取" + "(" + millisUntilFinished / 1000 + ")");
        btn_time.setText (millisUntilFinished / 1000 + "s");
        btn_time.setClickable(false);
    }

    @Override
    public void onFinish(){
        btn_time.setText (text);
        btn_time.setClickable(true);
        /*btn_time.setText ("获取验证码");
        btn_time.setTextColor (context.getResources().getColor(R.color.color_333333));
        btn_time.setEnabled(true);*/
    }
}
