package com.inclusive.finance.jh.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.permissionWAndRWithPermissionCheck
import com.inclusive.finance.jh.bean.VersionBean
import com.inclusive.finance.jh.databinding.ActivityUpdateBinding
import com.inclusive.finance.jh.service.DownloadService


/**
 * Created by bin on 17/7/7.
 */
class UpdateActivity : BaseActivity() {
    private var mVersion: VersionBean? = null

    /**
     * 回调
     */
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            startDownloadApp(service as DownloadService.DownloadBinder)
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }
   lateinit var viewBind :ActivityUpdateBinding

    override fun setInflateBinding() {

        viewBind = DataBindingUtil.setContentView<ActivityUpdateBinding>(this, R.layout.activity_update)
            .apply {
                lifecycleOwner = this@UpdateActivity
            }
        mVersion = intent.getSerializableExtra(EXTRA_VERSION) as VersionBean
        if (mVersion == null) {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        if (mVersion?.isForceUpdate.equals("1")) {
            viewBind.llClose.visibility = View.GONE
        }
        val versionName: String = mVersion?.versionName?:""
        if (!TextUtils.isEmpty(versionName)) {
            viewBind.tvTitle.text = versionName
        } else {
            viewBind.tvTitle.text = "发现新版本"
        }
        val updateLog: String = mVersion?.updateContent?:""
        if (!TextUtils.isEmpty(updateLog)) {
            viewBind.tvUpdateInfo.text = updateLog
        }

        //        String targetSize = mVersion.getUpdateSize() + "";
        //        if (!TextUtils.isEmpty(targetSize)) {
        //            mTextTargetSize.setText("新版本大小: " + targetSize);
        //            mTextTargetSize.setVisibility(View.VISIBLE);
        //        }
        viewBind.btnUpdate.setOnClickListener(View.OnClickListener {
            checkPermission()
            //                downloadApp();
            //                mBtnUpdate.setVisibility(View.GONE);
        })
        viewBind.llClose.setOnClickListener(View.OnClickListener {
            finish()
        })
    }


    /**
     * 回调监听下载
     */
    private fun startDownloadApp(binder: DownloadService.DownloadBinder) {
        // 开始下载，监听下载进度，可以用对话框显示
        if (mVersion != null) {
            binder.start(this,mVersion, object : DownloadService.DownloadCallback {
                override fun onStart() {
                    if (!this@UpdateActivity.isFinishing) {
                        LogUtils.e("start")
                        viewBind.progressBar.visibility = View.VISIBLE
                    }
                }

                override fun onProgress(progress: Float) {
                    if (!this@UpdateActivity.isFinishing) {
                        viewBind.progressBar.progress = (progress.toInt())
                    }
                }

                override fun setMax(total: Float) {
                    if (!this@UpdateActivity.isFinishing) {
                        viewBind.progressBar.max = total.toInt()
                    }
                }

                override fun onFinish() {
                    if (!this@UpdateActivity.isFinishing) {
                        this@UpdateActivity.finish()
                    }
                }

                override fun onError(msg: String?) {
                    if (!this@UpdateActivity.isFinishing) {
//                        this@UpdateActivity.finish()
                    }
                }
            })
        }
    }

    /**
     * 开启后台服务下载
     */
    private fun downloadApp() {
        //使用ApplicationContext延长他的生命周期
        DownloadService.bindService(applicationContext, conn)
    }

    override fun onBackPressed() {
        //        //禁用
        //        if (mVersion != null && mVersion.isConstraint()) {
        ////          ActManager.getInstance().finishAllActivity();
        //            android.os.Process.killProcess(android.os.Process.myPid());
        //        }
        //        super.onBackPressed();

        //        if (mVersion != null && mVersion.isConstraint()) {
        //            super.onBackPressed();
        //        }
    }

    override fun initToolbar() {

    }

    override fun onDestroy() {
        isShow = false
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermission() {
        permissionWAndRWithPermissionCheck(null, 100, false){
            val haveInstallPermission = mContext.packageManager.canRequestPackageInstalls()
            if (!haveInstallPermission) {
                val packageURI: Uri = Uri.parse("package:" + mContext.packageName)
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                startActivityForResult(intent, 200)
            }else{
                downloadApp()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 200) {
            downloadApp()
        }
    }
    companion object {
        const val EXTRA_VERSION = "extra_version"
        var isShow = false
    }
}