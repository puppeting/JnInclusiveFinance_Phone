package com.inclusive.finance.jh.ui.login

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.ToolApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.ui.login.LoginThirdActivity.Companion.RESULT_LOGIN_CANCELED
import com.inclusive.finance.jh.utils.PhoneNumberTextWatcher
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.blankj.utilcode.util.RegexUtils
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.databinding.ActivityBindphoneBinding
import org.jetbrains.anko.backgroundColor

@Deprecated("暂时不用")
@Route(path = "/com/BindPhoneActivity")
class BindPhoneActivity : BaseActivity(), View.OnClickListener {
    @Autowired
    @JvmField
    var bindType: String? = ""

    override fun initToolbar() {
        viewBind.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.tool_arrow_back_black_24dp)
        viewBind. toolbar.setNavigationOnClickListener { onBackPressed()}
//        toolbar.title = "收藏的线下讲座"
        when (bindType) {
            "1" -> {
                viewBind.tvBindphone.text="更换手机号"
                viewBind.btPass.visibility=View.GONE
            }
            else -> {
                viewBind.tvBindphone.text="绑定手机号"
            }
        }
        viewBind.toolbar.backgroundColor = ContextCompat.getColor(this, R.color.white)
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this,  viewBind.toolbar)
    }

    lateinit var viewBind: ActivityBindphoneBinding
    override fun setInflateBinding() {
        viewBind= DataBindingUtil.setContentView<ActivityBindphoneBinding>(this, R.layout.activity_bindphone).apply {
            lifecycleOwner = this@BindPhoneActivity
        }

    }
    override fun init() {
        viewBind.edPhone.postDelayed({
            if (true) {
                viewBind.edPhone.isFocusable = true
                viewBind.edPhone.isFocusableInTouchMode = true
                viewBind.edPhone.requestFocus()
                val inputManager = viewBind.edPhone.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(viewBind.edPhone, 0)
            }
        }, 200)
        viewBind.btNext.setOnClickListener(this)
        viewBind.btNext.isClickable=ToolApplication.appDebug
        viewBind.btPass.setOnClickListener(this)
        viewBind.textInputLay.isErrorEnabled = true
        viewBind.textInputLay.error = ("请输入手机号")
        viewBind.edPhone.addTextChangedListener(PhoneNumberTextWatcher(viewBind.edPhone) {

            if (it) {
                val phoneNum = viewBind.edPhone.text.toString().replace(" ", "")
                if (RegexUtils.isMobileExact(phoneNum)) {
                    DataCtrlClass.checkRegister(this, viewBind.edPhone.text.toString().replace(" ", "")) { b ->
                        if (b!=null)
                            if (b == false) {
                                viewBind.btNext.isClickable = true
                                viewBind.textInputLay.isErrorEnabled = false
                                viewBind.btNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4386f4"))
                            } else {
                                viewBind.textInputLay.isErrorEnabled = true
                                viewBind.textInputLay.error = ("手机号已绑定")
                                viewBind.btNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#77acf2"))
                                viewBind.btNext.isClickable = false
                            }
                    }

                } else {
                    viewBind.textInputLay.isErrorEnabled = true
                    viewBind.textInputLay.error = ("手机号码格式不正确")
                    viewBind.btNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#77acf2"))
                    viewBind.btNext.isClickable = false
                }

            } else {
                viewBind.textInputLay.isErrorEnabled = false
                viewBind.btNext.isClickable = false
                viewBind.btNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#77acf2"))
            }
        })

    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btPass -> {
                onBackPressed()
            }
            else -> {
                ARouter.getInstance().build("/com/BindPhoneCodeActivity").with(intent.extras).withString("bindPhoneNum",viewBind.edPhone.text.toString().replace(" ", "")).navigation()
            }
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (SZWUtils.isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                viewBind.tvBindphone.isFocusable = true
                viewBind.tvBindphone.isFocusableInTouchMode = true
                viewBind.tvBindphone.requestFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Constants.BusAction.Bus_LoginFinish)))
    fun finish(str: String) {
        RxBus.get().post(Constants.BusAction.Bus_Refresh_Main, Constants.BusAction.Bus_Refresh_Main)
        finish()
    }

    override fun onBackPressed() {
        setResult(RESULT_LOGIN_CANCELED)
        overridePendingTransition(R.anim.logo_fade_in, R.anim.slide_out_bottom)
        finish()
//        AppUtils.exitApp()
        super.onBackPressed()
    }
}