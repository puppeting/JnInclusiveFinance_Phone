package com.inclusive.finance.jh.widget

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.RegexUtils
import com.hwangjr.rxbus.RxBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureConfig.CHOOSE_REQUEST
import com.luck.picture.lib.config.PictureMimeType
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.permissionWAndRWithPermissionCheck
import com.inclusive.finance.jh.databinding.ActivityMyWebBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import com.inclusive.finance.jh.R

@RuntimePermissions
@Route(path = "/com/MyWebActivity")
class MyWebActivity : BaseActivity() {
    @Autowired
    @JvmField
    var webUrl = ""

    @Autowired
    @JvmField
    var webTitle = ""


    @Autowired
    @JvmField
    var screen_orientation = ""

    @Autowired
    @JvmField
    var isPDF = false
    private var isAnimStart = false
    private var currentProgress: Int = 0
    private var mUploadMessage: ValueCallback<Uri>? = null

    private var mUploadMessageForAndroid5: ValueCallback<Array<Uri>>? = null
    override fun initToolbar() {
        if (screen_orientation == "portrait") {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.toolbar.title = webTitle
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            if (!this.viewBind.mWebView.canGoBack()) {
                this.finish()
            } else {
                viewBind.mWebView.goBack()
            }
        }
    }

    //
    //    override fun setInflateId(): Int {
    //        return R.layout.activity_my_web
    //    }
    lateinit var viewBind: ActivityMyWebBinding
    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityMyWebBinding>(this, R.layout.activity_my_web)
            .apply {
                lifecycleOwner = this@MyWebActivity
            }

    }

    var tbsReaderView: TbsReaderView? = null
    var readerCallback = TbsReaderView.ReaderCallback { integer, o, o1 -> }

    override fun onDestroy() {
        viewBind.mWebView.reload()
        //销毁
        viewBind.mWebView.clearCache(true)
        viewBind.mWebView.clearFormData()
        viewBind.mWebView.destroy()
        tbsReaderView?.onStop()
        super.onDestroy()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    fun permissionWAndR(listener: Runnable) {
        listener.run()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun init() {
        //        mWebView.loadUrl("http://hpb.xzsem.cn/mobile/after_login.aspx?uid=4")
        //        mWebView.postUrl("http://hpb.xzsem.cn/mobile/after_login.aspx",EncodeUtils.base64Decode("uid=4"))
        permissionWAndRWithPermissionCheck(null, 100, false) {
            if (isPDF) {
//                tbsReaderView = TbsReaderView(this, readerCallback)
//
//                DataCtrlClass.downloadPDF(this, webUrl) {
//                    viewBind.rootView.addView(tbsReaderView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
//                    val bundle = Bundle()
//                    bundle.putString("filePath", it)
//                    bundle.putString("tempPath", Environment.getExternalStorageDirectory().path)
//                    //加载文件
//                    val bool = tbsReaderView?.preOpen("pdf", false)
//                    if (bool == true) {
//                        tbsReaderView?.openFile(bundle)
//                    }
//                }
                viewBind.mWebView.loadUrl("file:///android_asset/previewIndex.html?$webUrl")
            } else viewBind.mWebView.loadUrl(webUrl)
        }
        //启用数据库
        val webSettings = viewBind.mWebView.settings
        webSettings.javaScriptEnabled = true
//        webSettings.allowFileAccess = true
//        webSettings.domStorageEnabled = true
//        //        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
//        webSettings.loadWithOverviewMode = true
//        webSettings.useWideViewPort = true
        webSettings?.builtInZoomControls=true
        webSettings?.displayZoomControls=false
//        webSettings.databaseEnabled = true
//        webSettings.javaScriptCanOpenWindowsAutomatically = true
//        webSettings.setGeolocationEnabled(true)
//        webSettings.defaultTextEncodingName = "UTF-8" // 先载入JS代码
//        viewBind.mWebView.addJavascriptInterface(JavascriptInterface(), "backlistner")
        viewBind.mWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        viewBind.mWebView.clearHistory() // js通信接口
        viewBind.mWebView.clearFormData()
        viewBind.mWebView.clearCache(true)
        viewBind.mWebView.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.white))
        viewBind.mWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                viewBind.progressBar.visibility = View.VISIBLE
                viewBind.progressBar.alpha = 1.0f
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                try {
                    if (url.startsWith("weixin://") || url.startsWith("alipays://") || url.startsWith("mailto://") || url.startsWith("tel://") || url.startsWith("tel:") || url.startsWith("tbopen://") || url.startsWith("baidumap://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    } //其他自定义的scheme
                } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false
                }
                if (url.contains("https://3gimg.qq.com/lightmap/components/locationPicker2/back.html?")) {
                    val intent = intent
                    intent.putExtra("url", url)
                    setResult(Activity.RESULT_OK, intent)
                    if (!getQueryStr(url, "name").contains("我的位置")){
                        SZWUtils.showSnakeBarError("仅能选择 “我的位置”")
                        viewBind.mWebView.goBack()
                    }else{
                        RxBus.get().post("BackUrl", url)
                        finish()
                    }
                    return true
                }else
                //处理http和https开头的url
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                if (p1 != null) {
                    p1.proceed();//忽略证书的错误继续加载页面内容，不会变成空白页面
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (TextUtils.isEmpty(webTitle)) viewBind.actionBarCustom.mTitle.text = view?.title

                //这段js函数的功能就是注册监听，遍历所有的img标签，并添加onClick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
                viewBind.mWebView.loadUrl("javascript:(function(){" + "var objs = document.getElementsByClassName(\"return_btn\"); " + "for(var i=0;i<objs.length;i++) {" + "    objs[i].onclick=function()  {  " + "        window.backlistner.goBack();  " + "}" + "}" + "var objs1 = document.getElementsByClassName(\"return_btn2\"); " + "for(var i=0;i<objs1.length;i++) {" + "    objs1[i].onclick=function()  {  " + "        window.backlistner.goBack();  " + "}" + "}" + "var objs2 = document.getElementsByClassName(\"back\"); " + "for(var i=0;i<objs2.length;i++) {" + "    objs2[i].onclick=function()  {  " + "        window.backlistner.goBack();  " + "}" + "}" + "})()")
            }
        }
        viewBind.mWebView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(s: String, geolocationPermissionsCallback: GeolocationPermissionsCallback) {
                geolocationPermissionsCallback.invoke(s, true, true)
                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                this@MyWebActivity.currentProgress = this@MyWebActivity.viewBind.progressBar.progress
                if (newProgress >= 100 && !this@MyWebActivity.isAnimStart) {
                    this@MyWebActivity.isAnimStart = true
                    this@MyWebActivity.viewBind.progressBar.progress = newProgress
                    this@MyWebActivity.startDismissAnimation(this@MyWebActivity.viewBind.progressBar.progress)
                } else {
                    this@MyWebActivity.startProgressAnimation(newProgress)
                }


            }

            override fun onJsAlert(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                val builder = AlertDialog.Builder(p0?.context)
                builder.setTitle("提示").setMessage(p2).setPositiveButton("确定", null)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()
                p3?.confirm()
                return true
            }

            override fun onJsConfirm(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                val builder = AlertDialog.Builder(p0?.context)
                builder.setTitle("提示").setMessage(p2)
                    .setPositiveButton("确定") { _, _ -> p3?.confirm() }
                    .setNeutralButton("取消") { _, _ -> p3?.cancel() }
                builder.setOnCancelListener { p3?.cancel() }
                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener { _, _, _ -> true }
                // 禁止响应按back键的事件
                // builder.setCancelable(false);
                val dialog = builder.create()
                dialog.show()
                return true
            }

            override fun openFileChooser(p0: ValueCallback<Uri>?, p1: String?, p2: String?) {
                mUploadMessage = p0
                PictureSelector.create(this@MyWebActivity).openGallery(PictureMimeType.ofImage())
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .selectionMode(PictureConfig.SINGLE).compress(true).enableCrop(true)
                    .withAspectRatio(1, 1)
                    //                            .maxSelectNum(1)
                    //                        .videoMaxSecond(15)
                    //                            .recordVideoSecond(15)
                    .forResult(PictureConfig.CHOOSE_REQUEST) //结果回调onActivityResult code
            }

            override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
                mUploadMessageForAndroid5 = p1
                PictureSelector.create(this@MyWebActivity).openGallery(PictureMimeType.ofImage())
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .selectionMode(PictureConfig.SINGLE).compress(true).enableCrop(true)
                    .withAspectRatio(1, 1)
                    //                            .maxSelectNum(1)
                    //                        .videoMaxSecond(15)
                    //                            .recordVideoSecond(15)
                    .forResult(PictureConfig.CHOOSE_REQUEST) //结果回调onActivityResult code
                return true
            }
        }
    }
    private fun getQueryStr(url: String, str: String): String {
        val matches = RegexUtils.getMatches("(^|)$str=([^&]*)(&|$)", url)
        return try {
            if (matches.size > 0) {
                EncodeUtils.urlDecode(matches[0].split("=")[1].replace("&", ""))
            } else ""
        } catch (e: Exception) {
            "地址选取错误。请重试"
        }
    }
    inner class JavascriptInterface {

        @android.webkit.JavascriptInterface
        fun goBack() {
            if (!this@MyWebActivity.viewBind.mWebView.canGoBack()) {
                this@MyWebActivity.finish()
            }
        }
    }

    private fun startProgressAnimation(newProgress: Int) {
        val animator = ObjectAnimator.ofInt(this.viewBind.progressBar, "progress", this.currentProgress, newProgress)
        animator.duration = 300L
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    private fun startDismissAnimation(progress: Int) {
        val anim = ObjectAnimator.ofFloat(this.viewBind.progressBar, "alpha", 1.0f, 0.0f)
        anim.duration = 1500L
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val offset = 100 - progress
            this@MyWebActivity.viewBind.progressBar.progress = (progress.toFloat() + offset.toFloat() * fraction).toInt()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@MyWebActivity.viewBind.progressBar.progress = 0
                this@MyWebActivity.viewBind.progressBar.visibility = View.GONE
                this@MyWebActivity.isAnimStart = false
            }
        })
        anim.start()
    }


    override fun onBackPressed() {
        if (!this@MyWebActivity.viewBind.mWebView.canGoBack()) {
            this@MyWebActivity.finish()
        } else {
            viewBind.mWebView.goBack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_REQUEST) {
                // 图片、视频、音频选择结果回调
                val selectList = PictureSelector.obtainMultipleResult(data)
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                val path = Uri.parse(if (selectList[0].isCompressed) {
                    selectList[0].compressPath
                } else selectList[0].path)
                when {
                    null != mUploadMessage -> {
                        mUploadMessage?.onReceiveValue(path)
                        mUploadMessage = null

                    }
                    null != mUploadMessageForAndroid5 -> {
                        if (path != null) {
                            mUploadMessageForAndroid5?.onReceiveValue(arrayOf(path))
                        } else {
                            mUploadMessageForAndroid5?.onReceiveValue(arrayOf())
                        }
                        mUploadMessageForAndroid5 = null
                    }
                    else -> return
                }
            }
        } else {
            when {
                null != mUploadMessage -> {
                    mUploadMessage?.onReceiveValue(Uri.EMPTY)
                    mUploadMessage = null

                }
                null != mUploadMessageForAndroid5 -> {
                    mUploadMessageForAndroid5?.onReceiveValue(arrayOf())
                    mUploadMessageForAndroid5 = null
                }
                else -> return
            }
        }

    }

    companion object {
        var Intent_WebUrl = "webUrl"
        var Intent_WebTitle = "webTitle"
    }
}
