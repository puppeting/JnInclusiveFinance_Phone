package com.inclusive.finance.jh.ui.apply.report

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentReportLineBinding
import com.inclusive.finance.jh.widget.CustomProgress
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import org.jetbrains.anko.support.v4.act

/**
 * 调查报告·在线查看
 * */
class ReportLineFragment : MyBaseFragment() {
    private var dialog: CustomProgress? = null
    private var mUploadMessageForAndroid5: ValueCallback<Array<Uri>>? = null
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentReportLineBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBind = FragmentReportLineBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        dialog = CustomProgress.show(context, "加载中", false, null)
        //        viewBind.mWebView.loadUrl("file:///android_asset/previewIndex.html?"+Urls.getDCBGPREVIEW+"?creditId="+viewModel.keyId)
        viewBind.mWebView.loadUrl(Urls.getDCBGPREVIEW + "?creditId=" + viewModel.keyId)
        val webSettings = viewBind.mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode=true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
//        webSettings.allowFileAccess = true
//        webSettings.domStorageEnabled = true
//        //        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
//        webSettings.loadWithOverviewMode = true
//        webSettings.databaseEnabled = true
//        webSettings.javaScriptCanOpenWindowsAutomatically = true
//        webSettings.setGeolocationEnabled(true)
//        webSettings.defaultTextEncodingName = "UTF-8" // 先载入JS代码
//        viewBind.mWebView.addJavascriptInterface(JavascriptInterface(), "backlistner")
        viewBind.mWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        viewBind.mWebView.clearHistory() // js通信接口
        viewBind.mWebView.clearFormData()
        viewBind.mWebView.clearCache(true)
        viewBind.mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                dialog?.dismiss()
            }
        }
    }

}