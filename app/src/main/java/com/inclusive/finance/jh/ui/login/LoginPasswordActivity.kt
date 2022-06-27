package com.inclusive.finance.jh.ui.login

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Vibrator
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager.LayoutParams
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.*
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.BuildConfig
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.DataCtrlClass.LoginNet
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.app.ToolApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.permissionLocationWithPermissionCheck
import com.inclusive.finance.jh.base.permissionPhoneStateWithPermissionCheck
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.bean.VersionBean
import com.inclusive.finance.jh.bean.model.LoginPasswordModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Constants.BusAction
import com.inclusive.finance.jh.config.Constants.BusAction.Bus_LoginSuccess
import com.inclusive.finance.jh.config.Constants.SPUtilsConfig.SP_PHONE
import com.inclusive.finance.jh.config.Constants.SPUtilsConfig.SP_USER
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.databinding.ActivityLoginPwdBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.service.LocationService
import com.inclusive.finance.jh.ui.UpdateActivity
import com.inclusive.finance.jh.ui.account.BiometricPromptUtils
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.inclusive.finance.jh.widget.gesture.GestureLockLayout
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.textColor

@Route(path = "/com/LoginPasswordActivity")
class LoginPasswordActivity : BaseActivity(), PresenterClick {
    private lateinit var viewModel: LoginPasswordModel
    private var countDownTimer: CountDownTimer? = null
    private val time = 60000 //倒计时时间
    private var downKey = "L"

    companion object {
        const val RESULT_LOGIN_CANCELED = 3000
        fun login(act: Activity, phoneNum: String, pwd: String, it: User?) {
            saveUserInfo(it, phoneNum, pwd)
            val intent = act.intent
            val className = intent.getStringExtra(Constants.Result.Intent_ClassName)
            if (!TextUtils.isEmpty(className)) {
                intent.setClassName(act, className ?: "")
                act.startActivityForResult(intent, 100)
            }
            RxBus.get().post(Bus_LoginSuccess, Bus_LoginSuccess)

        }

        private fun saveUserInfo(it: User?, phoneNum: String, pwd: String) {
            MyApplication.user = it //密码登录后，关于手势的清空

            SPUtils.getInstance().put(SP_USER, GsonUtils.toJson(it))
            SPUtils.getInstance().put(Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false)
            SPUtils.getInstance().put(Constants.SPUtilsConfig.ISFINGERLOCK_KEY, false)
            SPUtils.getInstance().put(Constants.SPUtilsConfig.GESTURELOCK_KEY, "") //记录账号密码
            SPUtils.getInstance().put(Constants.SPUtilsConfig.SP_PHONE, phoneNum)
            SPUtils.getInstance().put(phoneNum, pwd) //记录token
            SPUtils.getInstance().put(Constants.SPUtilsConfig.SP_TOKEN, it?.token)
            val headers = HttpHeaders()
            headers.put(
                "X-Access-Token", SPUtils.getInstance().getString(Constants.SPUtilsConfig.SP_TOKEN)
            )    //header不支持中文，不允许有特殊字符
            OkGo.getInstance().addCommonHeaders(headers)
        }

    }

    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
    }

    lateinit var viewBind: ActivityLoginPwdBinding
    override fun setInflateBinding() {
        viewModel = ViewModelProvider(this).get(LoginPasswordModel::class.java)
        viewBind = DataBindingUtil.setContentView<ActivityLoginPwdBinding>(this, R.layout.activity_login_pwd)
            .apply {
                data = viewModel
                presenterClick = this@LoginPasswordActivity
                lifecycleOwner = this@LoginPasswordActivity
            }
    }

    /**指纹登录start*/
//    private val cryptographyManager = CryptographyManager()
//    private val ciphertextWrapper
//        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
//            this, "SHARED_PREFS_FILENAME", Context.MODE_PRIVATE, "CIPHERTEXT_WRAPPER"
//        )
    private lateinit var biometricPrompt: BiometricPrompt
    private fun showBiometricPromptForDecryption() {
//        ciphertextWrapper?.let { textWrapper ->
            val canAuthenticate = BiometricManager.from(this)
                .canAuthenticate(Authenticators.BIOMETRIC_WEAK)
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
//                val secretKeyName = "getString(R.string.secret_key_name)"
//                val cipher = cryptographyManager.getInitializedCipherForDecryption(
//                    secretKeyName, textWrapper.initializationVector
//                )
                biometricPrompt = BiometricPromptUtils.createBiometricPrompt(
                    this, ::decryptServerTokenFromStorage
                )
                val promptInfo = BiometricPromptUtils.createPromptInfo()
                biometricPrompt.authenticate(promptInfo )
//                biometricPrompt.authenticate(promptInfo, CryptoObject(cipher))
//            }
        }
    }

    private fun decryptServerTokenFromStorage(authResult: AuthenticationResult) {
//        ciphertextWrapper?.let { textWrapper ->
//            authResult.cryptoObject?.cipher?.let {
//                val plaintext = cryptographyManager.decryptData(textWrapper.ciphertext, it)
//                SampleAppUser.fakeToken = plaintext
                // Now that you have the token, you can query server for everything else
                // the only reason we call this fakeToken is because we didn't really get it from
                // the server. In your case, you will have gotten it from the server the first time
                // and therefore, it's a real token.
                if (SPUtils.getInstance().getString(SP_USER).isNotEmpty()) {

//                    MyApplication.user = Gson().fromJson(
//                        SPUtils.getInstance().getString(SP_USER), User::class.java
//                    )
//                    RxBus.get()
//                        .post(Constants.BusAction.Bus_LoginFinish, Constants.BusAction.Bus_LoginFinish)

                    loginNet(SPUtils.getInstance().getString(SP_PHONE),SPUtils.getInstance().getString(SPUtils.getInstance().getString(SP_PHONE)),"")
                    lifecycleScope.launch {
                        delay(800)
                        SPUtils.getInstance().put(Constants.SPUtilsConfig.ISFINGERLOCK_KEY, true)
                    }
                 }else{
                    ToastUtils.showShort("登录错误，请使用密码登录")
                }
//            }
//        }
    }

    /**指纹登录end*/

    override fun init() {
//        SZWUtils.flutterEngine(this,"defaultEngineId","main")
        viewBind.tvVersion.text = "V${BuildConfig.VERSION_NAME}"
        if (SPUtils.getInstance().getBoolean(Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false)) {
            setGestureListener()
        }
        DataCtrlClass.getVersion(this) {
            if (it != null) {

                if (!it.isUpdate.equals("false")) { // 已经是最新版本
                    // 需要更新
                    val versionBean: VersionBean = it
                    val intent = Intent(this, UpdateActivity::class.java)
                    intent.putExtra(UpdateActivity.EXTRA_VERSION, versionBean)
                    startActivity(intent)

                }
            }
        }
        DataCtrlClass.getshowCode(this) {
            if (it != null) {
                viewBind.layoutCode.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
        permissionPhoneStateWithPermissionCheck(null, 200, false)
    }

    private fun setGestureListener() {
        val gestureLockPwd = SPUtils.getInstance()
            .getString(Constants.SPUtilsConfig.GESTURELOCK_KEY, "")
        if (!TextUtils.isEmpty(gestureLockPwd)) {
            viewBind.gestureLock.setAnswer(gestureLockPwd)
        } else return
        viewBind.gestureLock.setDotCount(3)
        viewBind.gestureLock.setMode(GestureLockLayout.VERIFY_MODE) //设置手势解锁最大尝试次数 默认 5
        viewBind.gestureLock.tryTimes = 5
        var mNumber = 5
        val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        viewBind.gestureLock.setOnLockVerifyListener(object :
            GestureLockLayout.OnLockVerifyListener {
            override fun onGestureSelected(id: Int) { //每选中一个点时调用
            }

            override fun onGestureFinished(isMatched: Boolean) {
                mNumber = --mNumber //绘制手势解锁完成时调用
                if (isMatched) { // 跳转首页
                    RxBus.get()
                        .post(Constants.BusAction.Bus_LoginFinish, Constants.BusAction.Bus_LoginFinish)
                } else {
                    viewBind.hintTV.visibility = View.VISIBLE
                    viewBind.hintTV.text = "你还有" + mNumber + "次机会"
                    viewBind.hintTV.startAnimation(animation)
                    viewBind.gestureLock.startAnimation(animation)
                    val vib = getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vib.vibrate(300)
                }
                resetGesture()
            }

            override fun onGestureTryTimesBoundary() { //超出最大尝试次数时调用
                viewBind.gestureLock.setTouchable(false)
            }
        })
    }

    /**
     * 重置手势布局（只是布局）
     */
    private fun resetGesture() {
        Handler().postDelayed({ viewBind.gestureLock.resetGesture() }, 300)
    }

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 1000L
    override fun onClick(v: View?) {
        when (v) {
            viewBind.btHand -> {
                viewModel.isLoginGesture.value = true
            }
            viewBind.btPwd -> {
                viewModel.isLoginGesture.value = false
            }
            viewBind.btFinger -> {
                showBiometricPromptForDecryption()
            }
            viewBind.btCode -> {
                getSecurityCode()
            }
            viewBind.btLoginPwd -> {

//                startActivity(
////                    FlutterActivity.createDefaultIntent(this)
////                    FlutterActivity.withCachedEngine("flutterEngineId").build(this)
//                    FlutterActivity.withNewEngine().initialRoute("mini").build(this)
//                )
//                ARouter.getInstance().build("/com/RecordActivity").navigation()
//                startActivity(Intent(this,com.rmondjone.CameraActivity::class.java))
//                if (false) {
                if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                    mLastClickTime = System.currentTimeMillis()
                    if (ToolApplication.appDebug) {
                        login(this@LoginPasswordActivity, "17558405099", "123456", User()) //                    ARouter.getInstance().build("/com/BindPhoneActivity").navigation()
                        RxBus.get()
                            .post(Constants.BusAction.Bus_LoginFinish, Constants.BusAction.Bus_LoginFinish)
                    } else {
                        val phone = viewBind.edPhone.text.toString().replace(" ", "")
                        val password = viewBind.edPwd.text.toString()
                        val code = viewBind.edCode.text.toString()
                        when {
                            phone.isEmpty() -> {

                                SZWUtils.showSnakeBarError("账号不能为空")
                                return
                            }
                            password.isEmpty() -> {
                                SZWUtils.showSnakeBarError("密码不能为空")
                                return
                            }
                            code.isEmpty() && viewBind.layoutCode.isShown -> {
                                SZWUtils.showSnakeBarError("验证码不能为空")
                                return
                            }
                        }
                        loginNet(phone, password, code)
                    }
                }
            }
        }
    }

    private fun loginNet(phone: String, password: String, code: String) {
        LoginNet.loginPwd(this, phone, password, code) {
            if (it != null) {
                if (it.changePwd.isNullOrEmpty()) {
                    login(this@LoginPasswordActivity, phone, password, it)
                    if (!it.screenRecordeAble) {
                        window.setFlags(LayoutParams.FLAG_SCALED, LayoutParams.FLAG_SECURE)
                    } else {
                        window.clearFlags(LayoutParams.FLAG_SECURE)
                    }
                    permissionLocationWithPermissionCheck {
                        val intent = Intent(this, LocationService::class.java)
                        stopService(intent)
                        startForegroundService(intent)
                    }
                    RxBus.get().post(BusAction.Bus_LoginFinish, BusAction.Bus_LoginFinish)
                    RxBus.get().post(BusAction.Bus_Refresh_List, BusAction.Bus_Refresh_List)
                } else {
                    saveUserInfo(it, phone, password)
                    ARouter.getInstance().build("/com/NavActivity").withString("title", "修改密码").navigation()
                }

            }
        }
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
            viewBind.btCode.text = "重新获取验证码"
            viewBind.btCode.isClickable = true
            viewBind.btCode.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#f29400"))
            viewBind.btCode.textColor = ContextCompat.getColor(this, R.color.white)
            PreferencesService.setDownTimer(this, downKey, 0)
        } else {
            viewBind.btCode.isClickable = false
            viewBind.btCode.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.line2))
            viewBind.btCode.textColor = ContextCompat.getColor(this, R.color.color_text_title)
            viewBind.btCode.text = String.format("请稍候 0:%s", millisUntilFinished / 1000)
        }

    }

    private fun getSecurityCode() {
        if (viewBind.edPhone.text.toString().replace(" ", "").isEmpty()) {
            SZWUtils.showSnakeBarMsg("账号不能为空")
        } else {
            downTimer(time.toLong())
            PreferencesService.setDownTimer(this, downKey, System.currentTimeMillis())
            DataCtrlClass.getSecurityCode(this, viewBind.edPhone.text.toString().replace(" ", "")) {
                if (it == null) {
                    resetTimer(true, java.lang.Long.MIN_VALUE)
                }
            }
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (SZWUtils.isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                viewBind.imageView.isFocusable = true
                viewBind.imageView.isFocusableInTouchMode = true
                viewBind.imageView.requestFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Constants.BusAction.Bus_LoginFinish)))
    fun finish(str: String) {
        RxBus.get().post(Constants.BusAction.Bus_Refresh_Main, Constants.BusAction.Bus_Refresh_Main)
        ARouter.getInstance().build("/com/MainActivity")
            .withTransition(R.anim.fade_in, R.anim.slide_out_bottom)
            .navigation(this) //        if (ActivityUtils.isActivityExistsInStack(MainActivity::class.java)) {
        //            finish()
        //            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_bottom)
        //        } else Handler(Looper.getMainLooper()).postDelayed({
        //            finish()
        //            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_bottom)
        //        }, 2000)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent) //            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}