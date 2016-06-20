package io.base.gesturepassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import io.base.utils.AndroidUtils;

/**
 * Created by Vernon on 15/3/19.
 */
public class GesturePasswordActivity extends Activity implements View.OnClickListener {
    private ImageButton settingBack;
    private Switch enableGesturePassword;
    private Switch showGestureTrace;
    private View modifyGestruePassword;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting_gesture_password);
        ((TextView)findViewById(R.id.include_title_tx)).setText("手势密码");
        settingBack = (ImageButton)findViewById(R.id.include_left_btn);
        enableGesturePassword = (Switch)findViewById(R.id.enable_gesture_password);
        showGestureTrace = (Switch)findViewById(R.id.show_gesture_trace);
        modifyGestruePassword = findViewById(R.id.modify_setting_gesture_password);

        setListener();
    }

    private void setListener(){
        settingBack.setOnClickListener(this);
        /*enableGesturePassword.setup(!SettingUtils.isGestureEnabled(this), new MyImageSwitcher.OnStateChangeListener() {
            @Override
            public void newStateChange(boolean state) {
                modifyGestruePassword.setVisibility(state ? View.VISIBLE : View.GONE);
                SettingUtils.setEnableGesture(state, GesturePasswordActivity.this);
            }
        });
        showGestureTrace.setup(!SettingUtils.isGestureTraceEnabled(this), new MyImageSwitcher.OnStateChangeListener() {
            @Override
            public void newStateChange(boolean state) {
                SettingUtils.setShowGestureTrace(state, GesturePasswordActivity.this);
            }
        });*/
        modifyGestruePassword.setOnClickListener(this);
    }
    /*@Override
    protected void progressLogic(){
        modifyGestruePassword.setVisibility(SettingUtils.isGestureEnabled(this) ? View.VISIBLE : View.GONE);
    }*/

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if(id == R.id.include_left_btn) AndroidUtils.activityFinish(this);
        else if(id == R.id.modify_setting_gesture_password) AndroidUtils.startActivity(this,
                new Intent(this, InputOldGesturePasswordActivity.class));
    }
}
