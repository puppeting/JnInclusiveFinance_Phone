package com.inclusive.finance.jh.ui.login

import com.alibaba.android.arouter.facade.annotation.Route
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.blankj.utilcode.util.AppUtils


/**
 * Created by pc on 2017/12/4.
 */
@Deprecated("暂时不用")
@Route(path = "/com/GuideActivity")
class GuideActivity : BaseActivity() {
    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
    }


    override fun setInflateId(): Int {
        return R.layout.activity_guide
    }

    override fun init() {
//        bt_start.setOnClickListener {
//            setResult(200)
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            finish()
//        }
    }

    override fun onBackPressed() {
        AppUtils.exitApp()
    }

}
