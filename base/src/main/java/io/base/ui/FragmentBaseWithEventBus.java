package io.base.ui;

import android.content.Context;

import de.greenrobot.event.EventBus;

/**
 * Created by gaochao on 2016/3/1.
 */
public class FragmentBaseWithEventBus extends FragmentBase {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
