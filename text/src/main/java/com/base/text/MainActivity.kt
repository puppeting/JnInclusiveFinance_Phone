package com.base.text

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.base.text.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var viewBind: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .apply {
                lifecycleOwner = this@MainActivity
            }
//        startActivity(Intent(this,CameraActivity::class.java))
        initView()
    }

    fun initView() {
        //在线pdf
//        viewBind.mWebView.loadUrl("file:///android_asset/previewIndex.html?" + "https://www.gjtool.cn/pdfh5/git.pdf")
            //图片加载
//        viewBind.mWebView.loadUrl( "http://phjr.gnnsyh.com:9022/jeecg-boot/ygxt/dcbg/preview?creditId=32082200107400689202204241107172")
            //在线pdf 类似谷歌加载不需要本地文件
        viewBind.mWebView.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + "https://www.gjtool.cn/pdfh5/git.pdf")
        val webSettings = viewBind.mWebView.settings
        webSettings.setJavaScriptEnabled(true)
//        webSettings.setAllowFileAccess(true)
//        webSettings.setAllowFileAccessFromFileURLs(true)
//        webSettings.setAllowUniversalAccessFromFileURLs(true)
    }

}