package com.fengshenrich.mvp.view.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.humblerookie.R
import com.humblerookie.widgets.SlidingTabLayout
import io.base.ui.ActivityBase
import io.base.utils.ResourcesUtils

/**
 * Created by gaochao on 2016/5/18.
 */
abstract class SlidingTabActivity : ActivityBase(), SlidingTabLayout.TabColorizer {
    private var mIndicatorColor = 0
    override fun getDividerColor(position: Int): Int {
        return Color.TRANSPARENT
    }

    override fun getIndicatorColor(position: Int): Int {
        return mIndicatorColor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding_tab)
        mFragmentTitles = titles()
        mFragments = fragments()
        mFragmentArgs = fragmentArgs()

        if(mEqualIndicatorNumber != 0) mTabWidth = resources.displayMetrics.widthPixels/mEqualIndicatorNumber

        val viewPager = findViewById(R.id.viewpager) as ViewPager
        viewPager.adapter = MyCouponsPagerAdapter(supportFragmentManager)

        mIndicatorColor = indicatorColor()

        val tabIndicators = findViewById(R.id.tab_indicators) as SlidingTabLayout
        createTabView(tabIndicators)
        tabIndicators.setCustomTabColorizer(this)
        tabIndicators.setViewPager(viewPager)
    }

    private inner class MyCouponsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int  = mFragments!!.size

        override fun getItem(position: Int): Fragment? =
                Fragment.instantiate(this@SlidingTabActivity, mFragments!![position].canonicalName,
                        mFragmentArgs!![position])

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles!![position]
        }
    }

    protected abstract fun fragments(): Array<Class<*>>
    protected open fun fragmentArgs(): Array<Bundle?> = arrayOfNulls(fragments().size)
    protected abstract fun titles(): Array<String>
    protected abstract fun createTabView(tabIndicators: SlidingTabLayout)

    protected fun setEqualIndicatorNumber(number: Int){
        mEqualIndicatorNumber = number
    }

    protected fun getTabWidth() = mTabWidth

    open fun indicatorColor() = ResourcesUtils.getColor(resources, R.color.default_red)

    private var mFragments: Array<Class<*>>? = null
    private var mFragmentArgs : Array<Bundle?>? = null
    private var mFragmentTitles : Array<String>? = null
    private var mEqualIndicatorNumber = 0
    private var mTabWidth = 0
}
