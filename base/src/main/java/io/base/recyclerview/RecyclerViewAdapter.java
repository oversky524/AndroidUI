package io.base.recyclerview;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.base.R;
import io.base.exceptions.UnoverrideException;
import io.base.utils.ResourcesUtils;

/**
 * Created by gaochao on 2015/11/11.
 */
abstract public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<View> mHeaderViewList = new ArrayList<>();
    private ArrayMap<View, ViewInfo> mExtraMap = new ArrayMap<>();
    private ArrayList<View> mFooterViewList = new ArrayList<>();
    private List<T> mData;
    private LayoutInflater mLayoutInflater;

    protected T getItem(int position) {
        return mData.get(position);
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public RecyclerViewAdapter(LayoutInflater inflater, List<T> data){
        mLayoutInflater = inflater;
        mData = data;
    }

    public void setData(List<T> data, boolean notify){
        mData = data;
        if(notify){
            notifyDataSetChanged();
        }
    }

    public void addData(List<T> data){
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    final public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType >= VIEW_TYPE_REAL_FIRST){
            return onCreateViewHolderReal(parent, viewType - VIEW_TYPE_REAL_FIRST);
        }

        if(viewType >= VIEW_TYPE_FOOTER_FIRST){
            return new ViewHolder(mFooterViewList.get(viewType - VIEW_TYPE_FOOTER_FIRST));
        }

        if(viewType >= VIEW_TYPE_HEADER_FIRST){
            return new ViewHolder(mHeaderViewList.get(viewType - VIEW_TYPE_HEADER_FIRST));
        }

        throw new IllegalArgumentException("viewType " + viewType + " is illegal");
    }

    @Override
    final public void onBindViewHolder(ViewHolder holder, int position) {
        int headerSize = mHeaderViewList.size();
        if(position < headerSize){
            return;
        }
        position -= headerSize;
        if(position < getNormalSize()){
            onBindViewHolderReal(holder, position);
        }
    }

    @Override
    final public int getItemViewType(int position) {
        int size = mHeaderViewList.size();
        if(position < size){
            return VIEW_TYPE_HEADER_FIRST + position;
        }
        position -= size;
        size = getNormalSize();
        if(position < size){
            return getItemViewTypeReal(position) + VIEW_TYPE_REAL_FIRST;
        }
        position -= size;
        return VIEW_TYPE_FOOTER_FIRST + position;
    }

    protected int getItemViewTypeReal(int position){
        return 0;
    }

    protected void onBindViewHolderReal(ViewHolder holder, int position){
        holder.setup(getItem(position), position);
    }

    protected abstract ViewHolder onCreateViewHolderReal(ViewGroup parent, int viewType);

    @Override
    final public int getItemCount() {
        return mHeaderViewList.size() + getNormalSize() + mFooterViewList.size();
    }

    public void addFooterView(View footer, boolean drawDividerAbove){
        mFooterViewList.add(footer);
        ViewInfo viewInfo = new ViewInfo();
        viewInfo.mDrawDividerAboveView = drawDividerAbove;
        mExtraMap.put(footer, viewInfo);
    }

    public void addHeaderView(View header, boolean drawDividerBlew){
        mHeaderViewList.add(header);
        ViewInfo viewInfo = new ViewInfo();
        viewInfo.mDrawDividerBelowView = drawDividerBlew;
        mExtraMap.put(header, viewInfo);
    }

    public void addHeaderView(View header){
        addHeaderView(header, false);
    }

    public ViewInfo getViewInfo(View view){
        return mExtraMap.get(view);
    }

    /**
     * @param height unit:dp
     * */
    public void addHeaderView(int headerLayout, ViewGroup parent, int bgColorId, int height, boolean drawDividerBlew){
        LayoutInflater layoutInflater = getLayoutInflater();
        View header = layoutInflater.inflate(headerLayout, parent, false);
        addHeaderView(header, drawDividerBlew);
        ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        Resources resources = layoutInflater.getContext().getResources();
        layoutParams.height = ResourcesUtils.dpToPix(height, resources.getDisplayMetrics());
        header.setLayoutParams(layoutParams);
        header.setBackgroundColor(ResourcesUtils.getColor(resources, bgColorId));
    }

    public void addHeaderView(int headerLayout, ViewGroup parent, @ColorRes int bgColorId, int height){
        addHeaderView(headerLayout, parent, bgColorId, height, false);
    }

    public View addGapHeaderView(@ColorRes int bgColorId, int height, ViewGroup parent){
        LayoutInflater inflater = getLayoutInflater();
        Resources resources = inflater.getContext().getResources();
        View child = addCommonView(ResourcesUtils.getColor(resources, bgColorId), height, parent, inflater);
        addHeaderView(child);
        return child;
    }

    public int getHeaderCount(){
        return mHeaderViewList.size();
    }

    public int getFooterCount(){
        return mFooterViewList.size();
    }

    public int getNormalSize(){
        return mData == null ? 0 : mData.size();
    }

    public void addGapFooterView(@ColorRes int bgColorId, int height, ViewGroup parent, boolean drawDividerAboveView){
        LayoutInflater inflater = getLayoutInflater();
        Resources resources = inflater.getContext().getResources();
        View child = addCommonView(ResourcesUtils.getColor(resources, bgColorId), height, parent, inflater);
        addFooterView(child, drawDividerAboveView);
    }

    static private View addCommonView(@ColorInt int bgColor, int height, ViewGroup parent, LayoutInflater inflater){
        View view = inflater.inflate(R.layout.item_gap_view, parent, false);
        view.setBackgroundColor(bgColor);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        return view;
    }

    private static final int VIEW_TYPE_HEADER_FIRST = 10000;
    private static final int VIEW_TYPE_FOOTER_FIRST = 20000;
    private static final int VIEW_TYPE_REAL_FIRST = 30000;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setup(Object params, int position){
            throw new UnoverrideException("setup");
        }
    }

    public static class ViewInfo{
        public boolean mDrawDividerBelowView;
        public boolean mDrawDividerAboveView;
    }
}
