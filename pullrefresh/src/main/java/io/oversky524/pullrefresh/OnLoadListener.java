package io.oversky524.pullrefresh;

/**
 * Created by gaochao on 2016/8/11.
 */
public interface OnLoadListener {
    void onLoadNew(PullLayout refreshLayout);
    void onLoadMore(PullLayout refreshLayout);
}
