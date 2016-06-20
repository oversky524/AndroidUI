package io.base.gesturepassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import io.base.utils.AndroidUtils;
import io.base.utils.ToastUtil;

/**
 * Created by Vernon on 15/3/19.
 */
public class InputOldGesturePasswordActivity extends Activity implements View.OnClickListener {
    private ImageButton back;
    private LockPatternView lockView;
    private int mFailedPatternAttemptsSinceLastTimeout;
    private Handler mHandler = new Handler ();
    private CountDownTimer  mCountdownTimer;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_input_old_gesture_password);
        ((TextView)findViewById(R.id.include_title_tx)).setText("更改手势密码");
        back = (ImageButton)findViewById(R.id.include_left_btn);
        lockView = (LockPatternView)findViewById(R.id.input_old_gesture_psd);

        setListener();
    }

    private Runnable  mClearPatternRunnable = new Runnable () {
        public void run(){
            lockView.clearPattern ();
        }
    };

    private void setListener(){
        back.setOnClickListener(this);
        lockView.setOnPatternListener( new LockPatternView.OnPatternListener() {

            public void onPatternStart(){
                lockView.removeCallbacks (mClearPatternRunnable);
                patternInProgress ();
            }

            public void onPatternCleared(){
                lockView.removeCallbacks (mClearPatternRunnable);
            }

            public void onPatternDetected(List<LockPatternView.Cell> pattern){
                if (pattern == null) return;
                if (LockPatternUtils.checkPattern (pattern)) {
                    Intent intent = new Intent(InputOldGesturePasswordActivity.this, NewGesturePasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    AndroidUtils.startActivity(InputOldGesturePasswordActivity.this, intent);
                    finish();
                } else {
                    lockView.setDisplayMode (LockPatternView.DisplayMode.Wrong);
                    if (pattern.size () >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
                            if (retry == 0) ToastUtil.show(InputOldGesturePasswordActivity.this, "您已5次输错密码，请30秒后再试");
                        }

                    } else {
                        ToastUtil.show(InputOldGesturePasswordActivity.this, "输入长度不够，请重试");
                    }

                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
                        mHandler.postDelayed (attemptLockout, 2000);
                    } else {
                        lockView.postDelayed (mClearPatternRunnable, 2000);
                    }
                }
            }

            public void onPatternCellAdded(List<LockPatternView.Cell> pattern){

            }

            private void patternInProgress(){}
        });
    }

    Runnable attemptLockout = new Runnable () {

        @Override
        public void run(){
            lockView.clearPattern();
            lockView.setEnabled (false);
            mCountdownTimer = new CountDownTimer(LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1,1000) {

                @Override
                public void onTick(long millisUntilFinished){
                    /*int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
                    if (secondsRemaining > 0) {
                        mHeadTextView.setText (secondsRemaining + " 秒后重试");
                    } else {
                        mHeadTextView.setText ("请绘制手势密码");
                        mHeadTextView.setTextColor (Color.WHITE);
                    }*/

                }

                @Override
                public void onFinish(){
                    lockView.setEnabled (true);
                    mFailedPatternAttemptsSinceLastTimeout = 0;
                }
            }.start ();
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.include_left_btn) AndroidUtils.activityFinish(this);
    }
}
