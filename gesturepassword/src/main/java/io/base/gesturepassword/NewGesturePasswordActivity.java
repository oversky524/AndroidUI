package io.base.gesturepassword;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.base.utils.AndroidUtils;

/**
 * Created by Vernon on 15/3/19.
 */
public class NewGesturePasswordActivity extends Activity implements View.OnClickListener {
    private ImageButton back;
    private TextView tipTx;
    private LockPatternView lockView;
    private boolean firstInputNewPassword = true;
    private List<LockPatternView.Cell> firstPattern = new ArrayList<LockPatternView.Cell>();
    private View mPreviewViews[][] = new View[3][3];
    private View gestureTraceView;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_new_gesture_password);
        ((TextView)findViewById(R.id.include_title_tx)).setText("新手势密码");
        back = (ImageButton)findViewById(R.id.include_left_btn);
        tipTx = (TextView)findViewById(R.id.gesture_psd_tip);
        lockView = (LockPatternView)findViewById(R.id.input_gesture_psd);

        initPreviewViews();
        setListener();
    }

    private void initPreviewViews() {
        mPreviewViews = new View[3][3];
        mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
        mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
        mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
        mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
        mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
        mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
        mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
        mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
        mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);

        gestureTraceView = findViewById(R.id.gesturepwd_setting_preview);
    }

    private void updatePreviewViews() {
        if (firstPattern == null)
            return;
        //Log.i("way", "result = " + firstPattern.toString());
        for (LockPatternView.Cell cell : firstPattern) {
            /*Log.i("way", "cell.getRow() = " + cell.getRow()
                    + ", cell.getColumn() = " + cell.getColumn());*/
            mPreviewViews[cell.getRow()][cell.getColumn()]
                    .setBackgroundResource(R.mipmap.gesture_create_grid_selected);

        }
    }

    private void setListener(){
        back.setOnClickListener(this);
        lockView.setOnPatternListener(new LockPatternView.OnPatternListener() {

            public void onPatternStart() {
            }

            public void onPatternCleared() {
            }

            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
//                LockPatternUtils lockPatternUtils = GlobalApp.getInstance().getLockPatternUtils();
                if(firstInputNewPassword)
                {
                    firstInputNewPassword = false;
                    tipTx.setText("请再次输入新密码");
                    firstPattern.clear();
                    firstPattern.addAll(pattern);
                    lockView.clearPattern();
//                    if(SettingUtils.isGestureTraceEnabled(NewGesturePasswordActivity.this)){
                        updatePreviewViews();
//                    }
                }else {
                    String pattern1 = LockPatternUtils.patternToString(firstPattern);
                    String pattern2 = LockPatternUtils.patternToString(pattern);
                    if(pattern1.equals(pattern2))
                    {
                        LockPatternUtils.saveLockPattern(pattern);
                        AndroidUtils.activityFinish(NewGesturePasswordActivity.this);
                    }else {
                        lockView.clearPattern();
                        tipTx.setText("与上一次绘制不一致，请重新绘制");
                        tipTx.setTextColor(Color.RED);
                    }
                }
            }

            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
            }

            private void patternInProgress() {
            }
        });
    }

    /*@Override
    protected void progressLogic(){
        gestureTraceView.setVisibility(SettingUtils.isGestureTraceEnabled(this) ? View.VISIBLE : View.INVISIBLE);
    }*/

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.include_left_btn) AndroidUtils.activityFinish(this);
    }
}
