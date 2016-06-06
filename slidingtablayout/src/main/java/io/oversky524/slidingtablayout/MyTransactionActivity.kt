package io.oversky524.slidingtablayout

import android.content.Intent
import android.os.Bundle
import com.fengshenrich.mvp.view.activity.SlidingTabActivity
import com.humblerookie.R
import com.humblerookie.widgets.SlidingTabLayout
import io.base.ui.ActivityLifeCycle
import io.base.utils.AndroidUtils

class MyTransactionActivity : SlidingTabActivity() {
    override fun createTabView(tabIndicators: SlidingTabLayout) {
        tabIndicators.setCustomTabView(R.layout.tab_head_textview, R.id.tab_head_textview)
    }

    override fun fragments(): Array<Class<*>> {
        return arrayOf( DemoFragment::class.java,
                DemoFragment::class.java,
                DemoFragment::class.java,
                DemoFragment::class.java)
    }

    override fun titles(): Array<String> {
        return arrayOf(getString(R.string.all), getString(R.string.investment),
                getString(R.string.redemption), getString(R.string.charge))
    }

    companion object{
        fun startThis(){
            val activity = ActivityLifeCycle.getCurrentActivity()
            AndroidUtils.startActivity(activity, Intent(activity, MyTransactionActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(this, getString(R.string.my_transaction))
    }
}
