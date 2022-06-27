package com.inclusive.finance.jh.ui.login

import android.content.Context
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.app.ToolApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Constants.BusAction.Bus_LoginFinish
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.ui.login.LoginThirdActivity.Companion.RESULT_LOGIN_CANCELED
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.inclusive.finance.jh.widget.VerificationAction
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.databinding.ActivityBindphonecodeBinding
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
@Deprecated("暂时不用")
@Route(path = "/com/BindPhoneCodeActivity")
class BindPhoneCodeActivity : BaseActivity(), View.OnClickListener {
    @Autowired
    @JvmField
    var bindPhoneNum: String? = ""

    @Autowired
    @JvmField
    var bindType: String? = ""
    private var countDownTimer: CountDownTimer? = null
    private val time = 60000//倒计时时间
    private var downKey = "B"
    override fun initToolbar() {
        viewBind.toolbar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.tool_arrow_back_black_24dp)
        viewBind.toolbar.setNavigationOnClickListener { finish() }
//        toolbar.title = "收藏的线下讲座"
        when (bindType) {
            "1" -> {
                viewBind.tvBindphone.text = "更换手机号"
            }
            else -> {
                viewBind.tvBindphone.text = "绑定手机号"
            }
        }
        viewBind.toolbar.backgroundColor = ContextCompat.getColor(this, R.color.white)
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, viewBind.toolbar)
    }

    lateinit var viewBind: ActivityBindphonecodeBinding
    override fun setInflateBinding() {
        viewBind= DataBindingUtil.setContentView<ActivityBindphonecodeBinding>(this, R.layout.activity_bindphonecode).apply {
            lifecycleOwner = this@BindPhoneCodeActivity
        }

    }
    override fun init() {
        viewBind.tvPhone.text = bindPhoneNum
        viewBind.edCode.postDelayed({
            if (true) {
                viewBind.edCode.isFocusable = true
                viewBind.edCode.isFocusableInTouchMode = true
                viewBind.edCode.requestFocus()
                val inputManager =
                    viewBind.edCode.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(viewBind.edCode, 0)
            }
        }, 200)
        getSecurityCode()
        viewBind.edCode.setOnVerificationCodeChangedListener(object :
            VerificationAction.OnVerificationCodeChangedListener {
            override fun onInputCompleted(s: CharSequence?) {
                KeyboardUtils.hideSoftInput(viewBind.edCode)
                if (ToolApplication.appDebug)
                    RxBus.get().post(Bus_LoginFinish, Bus_LoginFinish)
                else
                    DataCtrlClass.verifySecurityCode(
                        this@BindPhoneCodeActivity, bindPhoneNum
                            ?: "", s.toString()
                    ) { it ->
                        if (it != null) {
                            val user = MyApplication.user as User
                            DataCtrlClass.saveUser(
                                this@BindPhoneCodeActivity, user.userName
                                    ?: "", bindPhoneNum ?: ""
                            ) { entity ->
                                if (entity != null) {
                                    user.telephone = bindPhoneNum
                                    if (!user.telephone.isNullOrEmpty()) {
                                        user.infoDone = true
                                    }
                                    PreferencesService.saveAccount(
                                        this@BindPhoneCodeActivity,
                                        user.userId,
                                        user.headUrl ?: ""
                                    )
                                    RxBus.get().post(Bus_LoginFinish, Bus_LoginFinish)
                                }
                            }
                        } else {
                            viewBind.textInputLay.isErrorEnabled = true
                            viewBind.textInputLay.error = "验证码不正确"
                            viewBind.edCode.setText("")
                        }
                    }
            }

            override fun onVerCodeChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (ed_code.figures!=count) {
//                    textInputLay.isErrorEnabled=false
//                }

            }
        })
        viewBind.edCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (viewBind.edCode.figures != count) {
                    viewBind.textInputLay.isErrorEnabled = false
                }
            }
        })
        viewBind.btRecode.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        getSecurityCode()
    }

    private fun downTimer(l: Long) {
        countDownTimer = object : CountDownTimer(l, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resetTimer(false, millisUntilFinished)
            }

            override fun onFinish() {
                resetTimer(true, java.lang.Long.MIN_VALUE)
            }
        }
        countDownTimer?.start()
    }

    private fun resetTimer(b: Boolean, millisUntilFinished: Long) {
        if (b) {
            countDownTimer?.cancel()
            viewBind.btRecode.text = "重新获取验证码"
            viewBind.btRecode.isClickable = true
            viewBind.btRecode.textColor = ContextCompat.getColor(this, R.color.color_main_blue)
            PreferencesService.setDownTimer(this, downKey, 0)
        } else {
            viewBind.btRecode.isClickable = false
            viewBind.btRecode.textColor = ContextCompat.getColor(this, R.color.color_text_title)
            viewBind.btRecode.text = String.format("请稍候 0:%s", millisUntilFinished / 1000)
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

    private fun getSecurityCode() {
        if (!RegexUtils.isMobileExact(bindPhoneNum)) {
            toast("手机号码有误")
        } else {
            downTimer(time.toLong())
            PreferencesService.setDownTimer(this, downKey, System.currentTimeMillis())
            DataCtrlClass.getSecurityCode(this, bindPhoneNum ?: "") {
                if (it != null) {
                    toast("短信验证码发送成功,请注意查收")
                } else {
                    toast(it?.msg ?: "系统繁忙，请稍后重试")
                    resetTimer(true, java.lang.Long.MIN_VALUE)
                }
            }
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Bus_LoginFinish)))
    fun finish(str: String) {
        RxBus.get().post(Constants.BusAction.Bus_Refresh_Main, Constants.BusAction.Bus_Refresh_Main)
        overridePendingTransition(R.anim.logo_fade_in, R.anim.slide_out_bottom)
        finish()
    }

    override fun onBackPressed() {
        setResult(RESULT_LOGIN_CANCELED)
        overridePendingTransition(R.anim.logo_fade_in, R.anim.slide_out_bottom)
        finish()
        super.onBackPressed()
    }
}