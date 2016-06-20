package io.oversky524.maintabactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.base.ui.ActivityBase;
import io.base.ui.ActivityLifeCycle;
import io.base.ui.FragmentBase;
import io.base.utils.AndroidUtils;

public class MainTabActivity extends ActivityBase implements RadioGroup.OnCheckedChangeListener {
    private static final int TAB_HOME_PAGE = 0;
    private static final int TAB_INVESTMENT = 1;
    private static final int TAB_MINE = 2;
    private static final int TAB_HELP = 3;

    public static void startThis(){
        Activity activity = ActivityLifeCycle.getCurrentActivity();
        AndroidUtils.startActivity(activity, new Intent(activity, MainTabActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private FragmentTabHost mFragmentTabHost;
    private RadioGroup mTabsRg;
    private TextView[] mTabTvs = new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        mFragmentTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabsRg = (RadioGroup)findViewById(R.id.tabs);
        mTabTvs[TAB_HOME_PAGE] = (TextView)findViewById(R.id.home_page);
        mTabTvs[TAB_INVESTMENT] = (TextView)findViewById(R.id.investment);
        mTabTvs[TAB_MINE] = (TextView)findViewById(R.id.mine);
        mTabTvs[TAB_HELP] = (TextView)findViewById(R.id.help);

        initTabs();
    }

    private void initTabs(){
        FragmentTabHost fragmentTabHost = mFragmentTabHost;
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.real_tab_content);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("home_page").setIndicator("home_page"),
                FragmentBase.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("investment").setIndicator("investment"),
                FragmentBase.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("mine").setIndicator("mine"),
                FragmentBase.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("help").setIndicator("help"),
                FragmentBase.class, null);

        mTabsRg.setOnCheckedChangeListener(this);
        setCheck(TAB_HOME_PAGE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.help) setCheck(TAB_HELP);
        else if(checkedId == R.id.mine) setCheck(TAB_MINE);
        else if(checkedId == R.id.investment) setCheck(TAB_INVESTMENT);
        else if(checkedId == R.id.home_page) setCheck(TAB_HOME_PAGE);
        /*switch (checkedId){
            case R.id.help:
                setCheck(TAB_HELP);
                break;

            case R.id.mine:
                setCheck(TAB_MINE);
                break;

            case R.id.investment:
                setCheck(TAB_INVESTMENT);
                break;

            case R.id.home_page:
                setCheck(TAB_HOME_PAGE);
                break;
        }*/
    }

    private void setCheck(int index){
        mFragmentTabHost.setCurrentTab(index);
        TextView[] tabs = mTabTvs;
        int normalColor = Color.parseColor("#454545");
        int focusedColor = Color.parseColor("#ff5e5e");
        for(int i=0; i<tabs.length; ++i){
            if(i == index){
                tabs[i].setTextColor(focusedColor);
            }else{
                tabs[i].setTextColor(normalColor);
            }
        }
    }
}
