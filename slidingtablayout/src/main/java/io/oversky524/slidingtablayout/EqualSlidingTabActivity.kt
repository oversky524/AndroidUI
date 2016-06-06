package io.oversky524.slidingtablayout

import android.view.ViewGroup
import com.fengshenrich.mvp.view.activity.SlidingTabActivity
import com.humblerookie.R
import com.humblerookie.widgets.SlidingTabLayout
import io.base.utils.ViewUtils

abstract class EqualSlidingTabActivity : SlidingTabActivity() {
    private var mTabIndicators: ViewGroup ? = null

    override fun createTabView(tabIndicators: SlidingTabLayout) {
        mTabIndicators = tabIndicators.tabStrip
        tabIndicators.setOnCreateTabView {
            val tv = layoutInflater.inflate(R.layout.include_tab_view, mTabIndicators, false)
            mTabIndicators!!.addView(tv)
            mTabIndicators!!.removeView(tv)
            ViewUtils.modifyWidth(tv, getTabWidth())
            return@setOnCreateTabView tv
        }
    }
}
