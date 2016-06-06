package io.oversky524.slidingtablayout

import android.content.Intent
import android.os.Bundle
import com.humblerookie.R
import io.base.ui.ActivityLifeCycle
import io.base.utils.AndroidUtils

class SigninRecordsActivity : EqualSlidingTabActivity(){
    override fun fragmentArgs(): Array<Bundle?> {
        val args = arrayOf(null, Bundle())
//        args[1]!!.putByte(RewardFragment.KEY_TYPE, RewardFragment.TYPE_IN_SIGNIN_RECORDS)
        return args
    }

    override fun fragments(): Array<Class<*>> {
        return arrayOf(DemoFragment::class.java,
                DemoFragment::class.java)
    }

    override fun titles(): Array<String> {
        return arrayOf(getString(R.string.good_friends), getString(R.string.reward))
    }

    companion object{
        fun startThis(){
            val activity = ActivityLifeCycle.getCurrentActivity()
            AndroidUtils.startActivity(activity, Intent(activity, SigninRecordsActivity::class.java))
        }
    }

    init {
        setEqualIndicatorNumber(2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(this, getString(R.string.signin_records))
    }
}
