package io.oversky524.slidingtablayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.humblerookie.R
import io.base.ui.FragmentBase

/**
 * Created by gaochao on 2016/5/30.
 */
class DemoFragment : FragmentBase(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.activity_sliding_tab, container, false)
        return view;
    }
}
