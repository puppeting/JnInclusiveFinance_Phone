package com.inclusive.finance.jh.base

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.arouter.launcher.ARouter
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.widget.CustomProgress
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions


/**
 * Created by Swain
 * on 2017/1/16.
 */
@RuntimePermissions
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner, AbsBaseActivity {
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
        //            finish();
        //            return;
        //        }
        //        if (!SZWUtils.authentication){
        //            SZWUtils.security {
        //                if (!SZWUtils.authentication)
        //                    finish()
        //            }
        //        }
//        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        when {
            setInflateId() == -1 -> setInflateBinding()
            setInflateId() > 0 -> setContentView(setInflateId())
            setInflateId() == 0 -> setContentView(setInflateView())
        }

        mContext = this
        //        ButterKnife.bind(this);
        try {
            RxBus.get().register(this)
            ARouter.getInstance().inject(this)
            init()
            init(savedInstanceState)
            initToolbar()
            initData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initdat()
    }
    fun initdat() {

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(object : DefaultRefreshHeaderCreator {
            override fun createRefreshHeader(context: Context, layout: RefreshLayout): RefreshHeader {
                layout.setPrimaryColorsId(R.color.colorAccent, R.color.AntiqueWhite)//全局设置主题颜色
                var classicsHeader: ClassicsHeader = ClassicsHeader(context)
                return classicsHeader////指定为经典Header，默认是 贝塞尔雷达Header
            }
        })
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(object : DefaultRefreshFooterCreator {
            //            @Override public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                ClassicsFooter classicsFooter = new ClassicsFooter(context)
//                classicsFooter.setDrawableSize(20)
//                //底部加载动画和布局
//                classicsFooter.setVisibility(View.GONE)
//                //内容不满一页时不能开启上拉加载功能
//                layout.setEnableLoadMoreWhenContentNotFull(false)
//                return classicsFooter
//            }
            override fun createRefreshFooter(context: Context, layout: RefreshLayout): RefreshFooter {
                var classicsFooter: ClassicsFooter =  ClassicsFooter(context)
                classicsFooter.setDrawableSize(20F)
                //底部加载动画和布局
                classicsFooter.setVisibility(View.GONE)
                //内容不满一页时不能开启上拉加载功能
                layout.setEnableLoadMoreWhenContentNotFull(false)
                return classicsFooter
            }
        })
    }
    override fun onStart() {
        super.onStart()
        if (MyApplication.user?.screenRecordeAble == false) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    override fun setInflateBinding() {

    }

    override fun setInflateView(): View {
        return View(this)
    }

    override fun setInflateId() = -1

    /**
     * 视图，组件,数据的初始化
     */
    @Throws(Exception::class)
    override fun init() {

    }

    override fun init(savedInstanceState: Bundle?) {

    }

    override fun initData() {}
    override fun refreshData(type: Int?) {
        initData()
    }

    @NeedsPermission(CAMERA, WRITE_EXTERNAL_STORAGE)
    fun permissionCamera(intent: Intent?, requestCode: Int, isService: Boolean, listener: Runnable) {
        listener.run()
        startAction(intent, isService, if (requestCode == -1) Constants.Permission.Camera else requestCode)
    }

    @NeedsPermission(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    fun permissionLocation(listener: Runnable) {
        listener.run()
    }

    @NeedsPermission(RECEIVE_SMS, READ_SMS)
    fun permissionSMS(intent: Intent?, requestCode: Int, isService: Boolean) {
        startAction(intent, isService, if (requestCode == -1) Constants.Permission.SMS else requestCode)
    }

    @NeedsPermission(CALL_PHONE)
    fun permissionCallPhone(listener: Runnable) {
        listener.run()
    }

    @NeedsPermission(READ_PHONE_STATE)
    fun permissionPhoneState(intent: Intent?, requestCode: Int, isService: Boolean) {
        startAction(intent, isService, if (requestCode == -1) Constants.Permission.Phone else requestCode)
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    fun permissionWAndR(intent: Intent?, requestCode: Int, isService: Boolean, listener: Runnable) {
        listener.run()
        startAction(intent, isService, if (requestCode == -1) Constants.Permission.Phone else requestCode)
    }

    @NeedsPermission(REQUEST_INSTALL_PACKAGES)
    fun permissionInstallAPP(listener: Runnable) {
        listener.run()
    }

    private fun startAction(intent: Intent?, isService: Boolean, requestCode: Int) {
        if (intent != null) {
            if (isService) startService(intent)
            else startActivityForResult(intent, requestCode)
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val allFragments = supportFragmentManager.fragments
        for (fragment in allFragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    public override fun onDestroy() {

        try {
            RxBus.get().unregister(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CustomProgress.disMissNow()
        super.onDestroy()
    }
}
