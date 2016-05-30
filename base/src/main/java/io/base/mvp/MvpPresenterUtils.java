package io.base.mvp;

import java.util.List;

import io.base.utils.ListUtils;

/**
 * Created by gaochao on 2016/3/9.
 */
public class MvpPresenterUtils {
    private MvpPresenterUtils() { throw new AssertionError("No Instance!"); }

    /**
     * 进行一般化，例行的处理，如果MVPView是CommonMvpView
     * */
    public static <MvpView extends CommonMvpView, T> void doCommonAfterLoading(
            MvpPresenterWithData<MvpView, T> presenter, List<T> newData, boolean clear){
        CommonMvpView commonMvpView = presenter.getView();
        List<T> data = presenter.data();
        if(clear) data.clear();
        if(ListUtils.isEmpty(newData)){
            if(clear){
                commonMvpView.showNoData();
            }else{
                commonMvpView.showNoMore();
            }
        }else{
            data.addAll(newData);
        }
        commonMvpView.notifyDataSetChanged();
    }
}
