package io.oversky524.pullrefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RefreshingDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshing_demo);

        MyPullRefreshLayout refreshLayout = (MyPullRefreshLayout)findViewById(R.id.refreshingLayout);
        ListView lv = (ListView)refreshLayout.getTargetView();
        ArrayList<String> data = new ArrayList<>(20);
        for(int i=0; i<20; ++i){
            data.add(String.valueOf(i));
        }
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
        refreshLayout.setOnRefreshListener(new MyPullRefreshLayout.OnRefreshListener() {
            @Override
            public void onLoadNew(final MyPullRefreshLayout rl) {
                Observable.just(null).delay(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        rl.setLoadNewDone();
                    }
                });
            }
        });
        refreshLayout.setOnPullRefreshListener(new MeituanRefreshListener());
        refreshLayout.setChildCanContinuePullingListener(new ChildCanContinuePullingListener.ListViewPullingListener());
    }
}
