package io.oversky524.androidui.pullrefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.base.recyclerview.RecyclerViewAdapter;
import io.oversky524.androidui.R;
import io.oversky524.pullrefresh.OnLoadListener;
import io.oversky524.pullrefresh.PullLayout;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RefreshingDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshing_demo);

        final PullLayout refreshLayout = (PullLayout)findViewById(R.id.refreshingLayout);
        RecyclerView lv = (RecyclerView)refreshLayout.getTargetView();
        lv.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> data = new ArrayList<>(15);
        for(int i=0; i<15; ++i){
            data.add(String.valueOf(i));
        }
        lv.setAdapter(new RecyclerViewAdapter(getLayoutInflater(), data) {
            @Override
            protected ViewHolder onCreateViewHolderReal(ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                return new ViewHolder(itemView){
                    @Override
                    public void setup(Object params, int position) {
                    }
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView)holder.itemView).setText(String.valueOf(position));
            }
        });
        refreshLayout.setOnRefreshListener(new OnLoadListener() {
            @Override
            public void onLoadNew(final PullLayout rl) {
                Observable.just(null).delay(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        rl.setLoadNewDone();
                    }
                });
            }

            @Override
            public void onLoadMore(final PullLayout refreshLayout) {
                Observable.just(null).delay(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                refreshLayout.setLoadMoreDone();
                            }
                        });
            }
        });
        refreshLayout.setOnPullRefreshListener(new NuomiListener());
        refreshLayout.refreshingForDown();
    }
}
