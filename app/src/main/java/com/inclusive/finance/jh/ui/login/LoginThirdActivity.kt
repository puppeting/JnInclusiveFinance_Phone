package com.inclusive.finance.jh.ui.login

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.AppUtils
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Constants.BusAction.Bus_LoginSuccess
import com.inclusive.finance.jh.databinding.ActivityLoginThirdBinding
import com.inclusive.finance.jh.utils.StatusBarUtil

@Deprecated("暂时不用")
@Route(path = "/com/LoginThirdActivity")
class LoginThirdActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val RESULT_LOGIN_CANCELED = 3000
        fun login(act: Activity, phoneNum: String, pwd: String, it: User?) {
            MyApplication.user = it
            val intent = act.intent
            val className = intent.getStringExtra(Constants.Result.Intent_ClassName)
            if (!TextUtils.isEmpty(className)) {
                intent.setClassName(act, className ?: "")
                act.startActivityForResult(intent, 100)
            }
            RxBus.get().post(Bus_LoginSuccess, Bus_LoginSuccess)

        }

    }

    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
        viewBind.header.visibility = View.VISIBLE
        viewBind.header.layoutParams.height = StatusBarUtil.getStatusBarHeight(this)
    }

    override fun setInflateId() = R.layout.activity_login_third
    lateinit var viewBind: ActivityLoginThirdBinding
    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityLoginThirdBinding>(this, R.layout.activity_start_page_trans)
            .apply {
                lifecycleOwner = this@LoginThirdActivity
            }

    }

    override fun init() {
//        val config = UMShareConfig()
//        config.isNeedAuthOnGetUserInfo(true)
//        UMShareAPI.get(this).setShareConfig(config)
//        viewBind.btLoginWechat.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
//        when (v) {
//            viewBind.btLoginWechat -> {
//                if (ToolApplication.appDebug) {
//                    login(this@LoginThirdActivity, "", "", User())
//                    ARouter.getInstance().build("/com/BindPhoneActivity").navigation()
//                } else UMShareAPI.get(this)
//                    .getPlatformInfo(this, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
//                        override fun onComplete(
//                            p0: SHARE_MEDIA?,
//                            p1: Int,
//                            p2: MutableMap<String, String>?,
//                        ) {
//                            DataCtrlClass.loginWechatApp(this@LoginThirdActivity, p2?.get("uid")
//                                ?: "", p2?.get("iconurl") ?: "") {
//                                if (it != null) {
//                                    login(this@LoginThirdActivity, p2?.get("uid")
//                                        ?: "", p2?.get("iconurl") ?: "", it)
//                                    if (it.telephone.isNullOrEmpty()) {
//                                        ARouter.getInstance().build("/com/BindPhoneActivity")
//                                            .navigation()
//                                    } else {
//                                        PreferencesService.saveAccount(this@LoginThirdActivity, (MyApplication.user as User).userId, (MyApplication.user as User).headUrl
//                                            ?: "")
//                                        RxBus.get()
//                                            .post(Constants.BusAction.Bus_LoginFinish, Constants.BusAction.Bus_LoginFinish)
//                                    }
//                                }
//                            }
//                        }
//
//                        override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
//                        }
//
//                        override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
//                        }
//
//                        override fun onStart(p0: SHARE_MEDIA?) {
//                        }
//                    })
//            }
//        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Constants.BusAction.Bus_LoginFinish)))
    fun finish(str: String) {
        RxBus.get().post(Constants.BusAction.Bus_Refresh_Main, Constants.BusAction.Bus_Refresh_Main)
        finish()
        overridePendingTransition(R.anim.logo_fade_in, R.anim.slide_out_bottom)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        AppUtils.exitApp()
        super.onBackPressed()
    }
}