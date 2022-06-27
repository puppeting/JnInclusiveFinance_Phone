//package com.inclusive.finance.widget
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Intent
//import android.graphics.PixelFormat
//import android.net.Uri
//import android.view.KeyEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import com.alibaba.android.arouter.facade.annotation.Autowired
//import com.alibaba.android.arouter.facade.annotation.Route
//import com.inclusive.finance.R
//import com.inclusive.finance.base.BaseActivity
//import com.inclusive.finance.utils.StatusBarUtil
//import com.inclusive.finance.utils.web.X5WebView
//import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback
//import com.tencent.smtt.export.external.interfaces.JsResult
//import com.tencent.smtt.sdk.*
//import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm
//import com.tencent.smtt.utils.TbsLog
//import org.jetbrains.anko.backgroundColor
//import java.net.URL
//
//
///**
// * @author Bananv
// */
//@Route(path = "/com/BrowserActivity")
//class BrowserActivity : BaseActivity() {
//
//    @Autowired
//    @JvmField
//    var webTitle = ""
//    @Autowired
//    @JvmField
//    var webLoadUrl = ""
//
//    override fun initToolbar() {
//        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.tool_arrow_back_black_24dp)
//        toolbar.title = webTitle
//        toolbar.backgroundColor = ContextCompat.getColor(this, R.color.white)
//        StatusBarUtil.darkMode(this)
//        StatusBarUtil.setPaddingSmart(this, appBar)
//        toolbar.setNavigationOnClickListener {
//            if (!this.mWebView.canGoBack()) {
//                this.finish()
//            } else {
//                mWebView.goBack()
//            }
//        }
//    }
//
//    override fun setInflateId(): Int {
//        return R.layout.activity_browser
//    }
//
//    /**
//     * 作为一个浏览器的示例展示出来，采用android+web的模式
//     */
//    lateinit var mWebView: X5WebView
//    private var mViewParent: ViewGroup? = null
//
//
//    private var mPageLoadingProgressBar: ProgressBar? = null
//
//    private var uploadFile: ValueCallback<Uri>? = null
//
//    private var mIntentUrl: URL? = null
//
//    private fun initProgressBar() {
//        mPageLoadingProgressBar = findViewById<View>(R.id.progressBar) as ProgressBar// new
//    }
//
//    override fun init() {
//        window.setFormat(PixelFormat.TRANSLUCENT)
//        val intent = intent
//        if (intent != null&&webLoadUrl.isNotEmpty()) {
//            mIntentUrl = URL(webLoadUrl)
//        }
//        //
//        try {
//            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
//                window
//                    .setFlags(
//                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
//                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
//            }
//        } catch (e: Exception) {
//        }
//
//
//        mViewParent = findViewById<View>(R.id.webView1) as ViewGroup
//        mWebView = X5WebView(this, null)
//
//        mViewParent?.addView(mWebView, FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.FILL_PARENT,
//                FrameLayout.LayoutParams.FILL_PARENT))
//
//        initProgressBar()
//
//        mWebView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                return false
//            }
//
//        }
//
//        mWebView.webChromeClient = object : WebChromeClient() {
//
//            var myVideoView: View? = null
//            var myNormalView: View? = null
//            var callback: CustomViewCallback? = null
//
//            // /////////////////////////////////////////////////////////
//            //
//
//            /**
//             * 全屏播放配置
//             */
//            override fun onShowCustomView(view: View?,
//                                          customViewCallback: CustomViewCallback?) {
//                val normalView = findViewById<View>(R.id.web_filechooser) as FrameLayout
//                val viewGroup = normalView.parent as ViewGroup
//                viewGroup.removeView(normalView)
//                viewGroup.addView(view)
//                myVideoView = view
//                myNormalView = normalView
//                callback = customViewCallback
//            }
//
//            override fun onHideCustomView() {
//                if (callback != null) {
//                    callback?.onCustomViewHidden()
//                    callback = null
//                }
//                if (myVideoView != null) {
//                    val viewGroup = myVideoView?.parent as ViewGroup
//                    viewGroup.removeView(myVideoView)
//                    viewGroup.addView(myNormalView)
//                }
//            }
//
//            override fun onJsAlert(arg0: WebView?, arg1: String?, arg2: String?,
//                                   arg3: JsResult?): Boolean {
//                /**
//                 * 这里写入你自定义的window alert
//                 */
//                return super.onJsAlert(null, arg1, arg2, arg3)
//            }
//        }
//
//        mWebView.setDownloadListener { arg0, arg1, arg2, arg3, arg4 ->
//            TbsLog.d(TAG, "url: $arg0")
//            AlertDialog.Builder(this@BrowserActivity)
//                    .setTitle("allow to download？")
//                    .setPositiveButton("yes"
//                    ) { _, which ->
//                        Toast.makeText(
//                                this@BrowserActivity,
//                                "fake message: i'll download...",
//                                Toast.LENGTH_LONG).show()
//                    }
//                    .setNegativeButton("no"
//                    ) { dialog, which ->
//                        Toast.makeText(
//                                this@BrowserActivity,
//                                "fake message: refuse download...",
//                                Toast.LENGTH_SHORT).show()
//                    }
//                    .setOnCancelListener {
//                        Toast.makeText(
//                                this@BrowserActivity,
//                                "fake message: refuse download...",
//                                Toast.LENGTH_SHORT).show()
//                    }.show()
//        }
//
//        val webSetting = mWebView.settings
//        webSetting.allowFileAccess = true
//        webSetting.layoutAlgorithm = LayoutAlgorithm.NARROW_COLUMNS
//        webSetting.setSupportZoom(true)
//        webSetting.builtInZoomControls = true
//        webSetting.useWideViewPort = true
//        webSetting.setSupportMultipleWindows(false)
//        // webSetting.setLoadWithOverviewMode(true);
//        webSetting.setAppCacheEnabled(true)
//        // webSetting.setDatabaseEnabled(true);
//        webSetting.domStorageEnabled = true
//        webSetting.javaScriptEnabled = true
//        webSetting.setGeolocationEnabled(true)
//        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
//        webSetting.setAppCachePath(this.getDir("appcache", 0).path)
//        webSetting.databasePath = this.getDir("databases", 0).path
//        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
//                .path)
//        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
//        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
//        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        // webSetting.setPreFectch(true);
//        val time = System.currentTimeMillis()
//        if (mIntentUrl == null) {
//            mWebView.loadUrl(mHomeUrl)
//        } else {
//            mWebView.loadUrl(mIntentUrl.toString())
//        }
//        TbsLog.d("time-cost", "cost time: " + (System.currentTimeMillis() - time))
//        CookieSyncManager.createInstance(this)
//        CookieSyncManager.getInstance().sync()
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mWebView != null && mWebView.canGoBack()) {
//                mWebView.goBack()
//                return true
//            } else {
//                return super.onKeyDown(keyCode, event)
//            }
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        TbsLog.d(TAG, "onActivityResult, requestCode:" + requestCode
//                + ",resultCode:" + resultCode)
//
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                0 -> if (null != uploadFile) {
//                    val result = if (data == null || resultCode != Activity.RESULT_OK)
//                        null
//                    else
//                        data.data
//                    uploadFile?.onReceiveValue(result)
//                    uploadFile = null
//                }
//                else -> {
//                }
//            }
//        } else if (resultCode == Activity.RESULT_CANCELED) {
//            if (null != uploadFile) {
//                uploadFile?.onReceiveValue(null)
//                uploadFile = null
//            }
//
//        }
//
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        if (intent == null || mWebView == null || intent.data == null) {
//            return
//        }
//        mWebView.loadUrl(intent.data.toString())
//    }
//
//    override fun onDestroy() {
//        if (mWebView != null) {
//            mWebView.destroy()
//        }
//        super.onDestroy()
//    }
//
//    companion object {
//
//        private val mHomeUrl = "http://app.html5.qq.com/navi/index"
//        private val TAG = "SdkDemo"
//        private val MAX_LENGTH = 14
//
//        val MSG_OPEN_TEST_URL = 0
//        val MSG_INIT_UI = 1
//    }
//
//}
