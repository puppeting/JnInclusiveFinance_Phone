package com.inclusive.finance.jh.ui.login

import android.media.SoundPool
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.R.raw
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.permissionWAndRWithPermissionCheck
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.databinding.ActivityStartPageTransBinding
import com.inclusive.finance.jh.utils.StatusBarUtil


/**
 * Created by pc on 2017/12/4.
 */
@Route(path = "/com/StartPageTransActivity")
class StartPageTransActivity : BaseActivity() {
    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
    }


    lateinit var viewBind: ActivityStartPageTransBinding
    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityStartPageTransBinding>(this, R.layout.activity_start_page_trans)
            .apply {
                lifecycleOwner = this@StartPageTransActivity
            }
    }
    val soundPool = SoundPool.Builder().build()
    override fun onDestroy() {
        soundPool.release()
        super.onDestroy()

    }
    override fun init() {
        super.init()

//        val mMediaPlayer = MediaPlayer.create(this, raw.welcome_puci)
//        mMediaPlayer.isLooping = false
//        mMediaPlayer.start()


        val soundID = soundPool?.load(application, raw.welcome_puci, 1) ?: 0
        soundPool.setOnLoadCompleteListener { pool, i, i2 ->
            pool?.play(
                soundID, 1f,  //左耳道音量【0~1】
                1f,  //右耳道音量【0~1】
                0,  //播放优先级【0表示最低优先级】
                0,  //循环模式【0循环一次，-1一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1f //播放速度【1正常，范围0~2】
            )

        }
        Log.i("Task:", "TestAppStartTaskOne执行 startpage")
        viewBind.imgLogo.post {
            permissionWAndRWithPermissionCheck(null, 100, false, Runnable {
                jump()
                Log.i("Task:", "TestAppStartTaskOne执行 jump")
            })
        }

//        mMediaPlayer.setOnCompletionListener {
//            Log.i("开始3","")
//            permissionWAndRWithPermissionCheck(null, 100, false, Runnable {
//                jump()
//            })
//        }


//        permissionWAndRWithPermissionCheck(null, 100, false, Runnable {
//            jump()
//        })

    }


//    private fun initView() {
//        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
//            finish()
//            return
//        }
//
//        //        UpdateHelper.getInstance().init(applicationContext, Color.parseColor("#0A93DB"));
//        //        UpdateHelper.getInstance().autoUpdate(packageName, false, 120000);
//
//        //        BDAutoUpdateSDK.uiUpdateAction(applicationContext, object : UICheckUpdateCallback {
//        //            override fun onNoUpdateFound() {
//        //
//        //            }
//        //
//        //            override fun onCheckComplete() {
//        //
//        //            }
//        //        })
//
////                PermissionLocationWithCheck(Intent(this, LocationService::class.java),true)
//        val anim = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in)
//        anim.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//
//                //                login()
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//
//            override fun onAnimationEnd(animation: Animation) {
//                viewBind.imgLogo.postDelayed({
//                    val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
//                    val versionCode = preferences.getInt("AppVersion", 0)
//
//                    val flag = AppUtils.getAppVersionCode() > versionCode
//
//                    //                    if (ToolApplication.appDebug || flag) {
//                    //                        val editor = preferences.edit()
//                    //                        editor.putInt("AppVersion", AppUtils.getAppVersionCode())
//                    //                        editor.apply()
//                    //                        ARouter.getInstance().build("/com/GuideActivity")
//                    //                            .withTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    //                            .navigation(this@StartPageTransActivity, 100)
//                    //
//                    //                    } else {
//                    permissionWAndRWithPermissionCheck(null, 100, false, Runnable {
//                        jump()
//                    })
//                    //                    type = 1
//                    //                    jump(type)
//                    //                    }
//                }, 100)
//            }
//        })
//        viewBind.imgLogo.animation = anim
//
//    }

    //    /**
    //     * 登录
    //     * */
    //    fun login() {
    //        if (UMShareAPI.get(this).isAuthorize(this, SHARE_MEDIA.WEIXIN)) {
    //            UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, object : UMAuthListener {
    //                override fun onComplete(p0: SHARE_MEDIA?, p1: Int, p2: MutableMap<String, String>?) {
    //                    DataCtrlClass.loginWechatAppNoDialog(
    //                        this@StartPageTransActivity, p2?.get("uid")
    //                            ?: "", p2?.get("iconurl") ?: ""
    //                    ) {
    //                        LoginThirdActivity.login(
    //                            this@StartPageTransActivity,
    //                            p2?.get("uid")
    //                                ?: "", p2?.get("iconurl") ?: "", it
    //                        )
    //                        if (canJump)
    //                            jump()
    //                        else
    //                            canJump = true
    //                    }
    //                }
    //
    //                override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
    //                }
    //
    //                override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
    //                }
    //
    //                override fun onStart(p0: SHARE_MEDIA?) {
    //                }
    //            })
    //        } else {
    //            if (canJump)
    //                jump()
    //            else
    //                canJump = true
    //        }
    //    }

    /**
     * @param type 0 主界面，1， 登录
     */
    private fun jump() {

        if (MyApplication.checkUserLogin() && !(MyApplication.user as User).telephone.isNullOrEmpty()) {
            PreferencesService.saveAccount(
                this@StartPageTransActivity, (MyApplication.user as User).userId, (MyApplication.user as User).headUrl
                    ?: ""
            )
            RxBus.get()
                .post(Constants.BusAction.Bus_LoginFinish, Constants.BusAction.Bus_LoginFinish)
            //            ARouter.getInstance().build("/com/MainActivity").withTransition(android.R.anim.fade_in, android.R.anim.fade_out).navigation(this)
        } else {

            ARouter.getInstance().build("/com/LoginPasswordActivity")
                .withOptionsCompat(ActivityOptionsCompat.makeSceneTransitionAnimation(this, viewBind.imgTop, "shareLogo"))
                .withTransition(android.R.anim.fade_in, android.R.anim.fade_out).navigation(this)

//                        val intent = Intent(this, LoginThirdActivity::class.java)
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, img_top, "shareLogo").toBundle())
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag(Constants.BusAction.Bus_LoginFinish)))
    fun loginFinish(str: String) {
        //        overridePendingTransition(R.anim.logo_fade_in, R.anim.slide_out_bottom)
        //        finish()
        //        ARouter.getInstance().build("/com/MainActivity").withTransition(R.anim.slide_in_bottom, android.R.anim.fade_out).navigation(this)
        finish()
    }
}
