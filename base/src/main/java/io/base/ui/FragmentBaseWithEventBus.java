package io.base.ui;

import android.content.Context;

import de.greenrobot.event.EventBus;

/**
 * Created by gaochao on 2016/3/1.
 */
public class FragmentBaseWithEventBus extends FragmentBase {
    private boolean mHasEventBus;
    protected void setHasEventBus(boolean hasEventBus){ mHasEventBus = hasEventBus; }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mHasEventBus) EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mHasEventBus) EventBus.getDefault().unregister(this);
    }
}
