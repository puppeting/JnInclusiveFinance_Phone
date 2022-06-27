package com.inclusive.finance.jh.ui.investigation

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.android.material.chip.ChipGroup
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.base.permissionCameraWithPermissionCheck
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentVodBinding
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.service.ZipService
import com.inclusive.finance.jh.ui.ApplyActivity
import com.inclusive.finance.jh.utils.SZWUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import org.jetbrains.anko.support.v4.act
import java.text.SimpleDateFormat
import java.util.*


/**
 *视频上传
 * */
class VodFragment : MyBaseFragment(), PresenterClick {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentVodBinding
    var orientationUtils: OrientationUtils? = null
    lateinit var gsyVideoOption: GSYVideoOptionBuilder
    private var isPlay = false
    private var isPause = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentVodBinding.inflate(inflater, container, false).apply {
            presenterClick = this@VodFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var url = ""
    var getUrl = ""
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }

    }
    override fun initView() {

        activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                viewBind.bottomLayout.visibility = if (viewModel.seeOnly == true) View.GONE else View.VISIBLE
            }
            else -> {
                viewBind.bottomLayout.visibility = View.GONE
            }
        }

        //增加封面
        val imageView = ImageView(act)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        //增加title

        //增加title
        viewBind.gsyVideoPlayer.titleTextView.visibility = View.GONE
        viewBind.gsyVideoPlayer.backButton.visibility = View.GONE

        orientationUtils = OrientationUtils(act, viewBind.gsyVideoPlayer)
        orientationUtils?.isEnable = false
        gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption.setThumbImageView(imageView).setIsTouchWiget(true).setRotateViewAuto(false)
            .setLockLand(false).setShowFullAnimation(true) //打开动画
            .setNeedLockFull(true).setUrl(SZWUtils.getIntactUrl(url)).setCacheWithPlay(false)
            .setVideoTitle("").setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects) //开始播放了才能旋转和全屏
                    //                    orientationUtils.isEnable = true
                    isPlay = true
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any) {
                    super.onQuitFullscreen(url, objects)
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[0]) //title
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[1]) //当前非全屏player
                    orientationUtils?.isLand = 0
                    orientationUtils?.backToProtVideo()
                }
            }).build(viewBind.gsyVideoPlayer)
        viewBind.gsyVideoPlayer.backButton.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }//直接横屏
        viewBind.gsyVideoPlayer.fullscreenButton.setOnClickListener(View.OnClickListener { //直接横屏
            //            orientationUtils.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            viewBind.gsyVideoPlayer.startWindowFullscreen(act, true, true)
            onBackPressedCallback.isEnabled=true
        })

    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                getUrl = Urls.getyxLgfkLylxUrl
            }
            else -> {
                getUrl = Urls.getVodUrl
            }
        }
        DataCtrlClass.SXDCNet.getVodUrl(requireActivity(), getUrl, viewModel.creditId) { //        DataCtrlClass.ApplyNet.getBusinessApplyInfo(context, "878a8a13f80cccd96074405d2dd0e93f") {
            if (it != null) {
                url = SZWUtils.getJsonObjectString(it, "imgUrl")
                val createDate = SZWUtils.getJsonObjectString(it, "createDate")
                if (createDate.isNotEmpty()) {
                    viewBind.videoTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(TimeUtils.string2Date(createDate))
                }
                GlideApp.with(act).load(SZWUtils.getIntactUrl(url)).centerInside()
                    .error(ContextCompat.getDrawable(act, R.mipmap.bg_vod_v))
                    .into(viewBind.gsyVideoPlayer.thumbImageView as ImageView)
                viewBind.gsyVideoPlayer.loadCoverImage(SZWUtils.getIntactUrl(url), R.mipmap.bg_vod_v)
                viewBind.gsyVideoPlayer.currentPlayer.setUp(SZWUtils.getIntactUrl(url), false, "")
            } else {
                GlideApp.with(act).load(SZWUtils.getIntactUrl(url)).centerInside()
                    .error(ContextCompat.getDrawable(act, R.mipmap.bg_vod_v))
                    .into(viewBind.gsyVideoPlayer.thumbImageView as ImageView)
                viewBind.gsyVideoPlayer.loadCoverImage(SZWUtils.getIntactUrl(url), R.mipmap.icon_error)
            }
        }
    }

    fun onBackPressed() {
        orientationUtils?.isLand = 0
        orientationUtils?.backToProtVideo()
        onBackPressedCallback.isEnabled=false
        if (GSYVideoManager.backFromWindowFull(context)) {
            return
        }

    }


    override fun onPause() {
        viewBind.gsyVideoPlayer.currentPlayer.onVideoPause()
        super.onPause()
        isPause = true
    }

    //    override fun onResume() {
    //        viewBind.gsyVideoPlayer.currentPlayer.onVideoResume(false)
    //        super.onResume()
    //        isPause = false
    //    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            viewBind.gsyVideoPlayer.currentPlayer.release()
        }
        orientationUtils?.releaseListener()
    }


    //    override fun onConfigurationChanged(newConfig: Configuration) {
    //        super.onConfigurationChanged(newConfig)
    //        //如果旋转了就全屏
    //        if (isPlay && !isPause) {
    //            viewBind.gsyVideoPlayer.onConfigurationChanged(act, newConfig, orientationUtils, true, true)
    //        }
    //    }

    fun recordVideo(context: Activity, limit_time: Int, size: Int) {
        val intent = Intent()
        intent.action = MediaStore.ACTION_VIDEO_CAPTURE
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        if (size != 0) {
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, size * 1024 * 1024L) //限制录制大小(10M=10 * 1024 * 1024L)
        }
        if (limit_time != 0) {
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, limit_time) //限制录制时间(10秒=10)
        }
        context.startActivityForResult(intent, 213)
    }

    override fun onClick(v: View?) {

        when (v) {
            viewBind.btRecord -> {
                if (ZipService.isRunning) {
                    context?.let { SZWUtils.showSnakeBarMsg("有一项未完成的任务，请稍后重试") }
                    return
                }
                if (url.isNotEmpty()) {
                    context?.let { SZWUtils.showSnakeBarMsg("注意，重新录制将会覆盖") }
                }
                (act as BaseActivity).permissionCameraWithPermissionCheck(null, 200, false) { //            recordVideo(act,60,0)
                    // 录制
                    val open = when (viewModel.businessType) {
                        ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                            val openCamera = PictureSelector.create(this)
                                .openCamera(PictureMimeType.ofVideo())
                            openCamera.maxSelectNum(1).isCamera(true).recordVideoSecond(60)
                                .videoMaxSecond(60) //                .isUseCustomCamera(true)
                                //                .setButtonFeatures(CustomCameraView.BUTTON_STATE_ONLY_RECORDER)//自定相机是否单独拍照、录像
                                //            .isPreviewVideo(true)//是否预览视频
                                .isSingleDirectReturn(true) //PictureConfig.SINGLE模式下是否直接返回
                                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                                    override fun onResult(result: List<LocalMedia?>) {
                                        if (ObjectUtils.isEmpty(result) || result.isEmpty()) {
                                            return


                                        }
                                        var picturePath: String? = result[0]?.compressPath
                                        if (picturePath == null) {
                                            picturePath = result[0]?.realPath
                                        }
                                        if (picturePath == null) {
                                            picturePath = result[0]?.path
                                        }


                                        startZipService(Uri.parse(picturePath))

                                    }

                                    override fun onCancel() {

                                    }
                                })
                            openCamera
                        }
                        else -> {
                            ARouter.getInstance().build("/com/RecordActivity").navigation()
                            PictureSelector.create(this).openGallery(PictureMimeType.ofVideo())
                        }
                    }

                }
            }
            viewBind.btNext -> {
                if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.lylxCheck == true) {
                    ActivityCompat.requireViewById<ChipGroup>(act, R.id.chipGroup)
                        .check(R.id.bt_jjsc)
                }
            }
        }

    }

    private fun startZipService(uri:Uri) {
        ZipService.bindService(act.applicationContext, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = (service as ZipService.ZipBinder)
                binder.start(act as ApplyActivity, uri, viewModel.creditId ?: "", viewModel.businessType)
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        })
    }
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag("videoUri")])
    fun onVideoUri(uri: Uri){
        startZipService(uri)
    }
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag("refreshVod")))
    fun refreshVod(url: String) {
        if (isAdded) (activity as BaseActivity).refreshData()
        initData()
    }
}