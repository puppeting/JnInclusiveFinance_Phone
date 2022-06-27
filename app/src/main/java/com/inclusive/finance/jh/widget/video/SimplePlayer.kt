package com.inclusive.finance.jh.widget.video

import android.content.pm.ActivityInfo
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.databinding.ActivitySimplePlayBinding
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
@Route(path = "/com/SimplePlayer")
class SimplePlayer : BaseActivity() {
    @Autowired
    @JvmField
    var url = ""

    @Autowired
    @JvmField
    var thumbImage = ""

    var orientationUtils: OrientationUtils? = null
    lateinit var viewBind: ActivitySimplePlayBinding
    lateinit var viewModel: ApplyModel
    override fun initToolbar() {
        StatusBarUtil.darkMode(this) //        StatusBarUtil.setPaddingSmart(this, viewBind.appBar)
    }

    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivitySimplePlayBinding>(this, R.layout.activity_simple_play)
            .apply {
                viewModel = ViewModelProvider(this@SimplePlayer).get(ApplyModel::class.java)
                lifecycleOwner = this@SimplePlayer
            }
    }

    override fun init() {
//        val source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
        viewBind.videoPlayer.setUp(SZWUtils.getIntactUrl(url), true, "")

        //增加封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideApp.with(this).load(SZWUtils.getIntactUrl(url)).into(imageView)
        viewBind.videoPlayer.thumbImageView = imageView //增加title
        //         viewBind.videoPlayer.titleTextView.visibility = View.VISIBLE
        //设置返回键
        viewBind.videoPlayer.backButton.visibility = View.VISIBLE //设置旋转
        orientationUtils = OrientationUtils(this, viewBind.videoPlayer) //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        viewBind.videoPlayer.fullscreenButton.setOnClickListener {
            orientationUtils?.resolveByClick()
        }
        //是否可以滑动调整
        viewBind.videoPlayer.setIsTouchWiget(true)
        //设置返回按键功能
        viewBind.videoPlayer.backButton.setOnClickListener {
            onBackPressed()
        }
        viewBind.videoPlayer.startPlayLogic()
    }

    override fun onPause() {
        super.onPause()
        viewBind.videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        viewBind.videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null) orientationUtils?.releaseListener()
    }

    override fun onBackPressed() { //先返回正常状态
        if (orientationUtils?.screenType != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            viewBind.videoPlayer.fullscreenButton.performClick()
            return
        } //释放所有
        viewBind.videoPlayer.setVideoAllCallBack(null)
        super.onBackPressed()
    }


}