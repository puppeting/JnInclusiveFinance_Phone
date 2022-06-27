package com.inclusive.finance.jh.widget

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer


class SampleCoverVideo: StandardGSYVideoPlayer {
    constructor(context: Context) : super(context)
    constructor(context: Context, fullFlag: Boolean) : super(context, fullFlag)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

//    override fun clearFullscreenLayout() {
//        mOrientationUtils.isLand=0
//        super.clearFullscreenLayout()
//    }
    var mCoverImage: ImageView? = null

    var mCoverOriginUrl: String? = null

    var mCoverOriginId = 0

    var mDefaultRes = 0

    override fun init(context: Context?) {
        super.init(context)
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
        if (mThumbImageViewLayout != null && (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.visibility = VISIBLE
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_cover
    }

    fun loadCoverImage(url: String?, res: Int) {
        mCoverOriginUrl = url
        mDefaultRes = res
//        Glide.with(context.applicationContext)
//            .setDefaultRequestOptions(RequestOptions().frame(1000000).centerCrop().error(res)
//                .placeholder(res)).load(url).into(mCoverImage?: ImageView(context))
//        SZWUtils.loadPhotoImg(context, url,mCoverImage)
        GlideApp.with(context).load(url).error(ContextCompat.getDrawable(context,res)).centerInside().into(mCoverImage?:ImageView(context))
    }

    fun loadCoverImageBy(id: Int, res: Int) {
        mCoverOriginId = id
        mDefaultRes = res
        mCoverImage?.setImageResource(id)
    }

    override fun startWindowFullscreen(context: Context?, actionBar: Boolean, statusBar: Boolean): GSYBaseVideoPlayer? {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val sampleCoverVideo: SampleCoverVideo = gsyBaseVideoPlayer as SampleCoverVideo
        if (mCoverOriginUrl != null) {
            sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes)
        } else if (mCoverOriginId != 0) {
            sampleCoverVideo.loadCoverImageBy(mCoverOriginId, mDefaultRes)
        }
        return gsyBaseVideoPlayer
    }


    override fun showSmallVideo(size: Point?, actionBar: Boolean, statusBar: Boolean): GSYBaseVideoPlayer? {
        //下面这里替换成你自己的强制转化
        val sampleCoverVideo: SampleCoverVideo = super.showSmallVideo(size, actionBar, statusBar) as SampleCoverVideo
        sampleCoverVideo.mStartButton.setVisibility(GONE)
        sampleCoverVideo.mStartButton = null
        return sampleCoverVideo
    }

    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
        super.cloneParams(from, to)
        val sf: SampleCoverVideo = from as SampleCoverVideo
        val st: SampleCoverVideo = to as SampleCoverVideo
        st.mShowFullAnimation = sf.mShowFullAnimation
    }


    /**
     * 退出window层播放全屏效果
     */
    override fun clearFullscreenLayout() {
        mOrientationUtils.isLand=0
        if (!mFullAnimEnd) {
            return
        }
        mIfCurrentIsFullscreen = false
        var delay = 0
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo()
            mOrientationUtils.isEnable = false
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener()
                mOrientationUtils = null
            }
        }
        if (!mShowFullAnimation) {
            delay = 0
        }
        val vp: ViewGroup = CommonUtil.scanForActivity(context)
            .findViewById(Window.ID_ANDROID_CONTENT)
        val oldF: View = vp.findViewById(fullId)
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            val gsyVideoPlayer: SampleCoverVideo = oldF as SampleCoverVideo
            gsyVideoPlayer.mIfCurrentIsFullscreen = false
        }
        if (delay == 0) {
            backToNormal()
        } else {
            postDelayed({ backToNormal() }, delay.toLong())
        }
    }


    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽  */
    override fun onSurfaceUpdated(surface: Surface?) {
        super.onSurfaceUpdated(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
            mThumbImageViewLayout.visibility = INVISIBLE
        }
    }

    override fun setViewShowState(view: View, visibility: Int) {
        if (view === mThumbImageViewLayout && visibility != VISIBLE) {
            return
        }
        super.setViewShowState(view, visibility)
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
                mThumbImageViewLayout.visibility = INVISIBLE
            }
        }
    }

    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽  */
//    protected var byStartedClick = false
//
////    override fun onClickUiToggle() {
////        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
////            setViewShowState(mLockScreen, VISIBLE)
////            return
////        }
////        byStartedClick = true
////        super.onClickUiToggle()
////    }
//
//    override fun changeUiToNormal() {
//        super.changeUiToNormal()
//        byStartedClick = false
//    }

//    override fun changeUiToPreparingShow() {
//        super.changeUiToPreparingShow()
//        Debuger.printfLog("Sample changeUiToPreparingShow")
//        setViewShowState(mBottomContainer, INVISIBLE)
//        setViewShowState(mStartButton, INVISIBLE)
//    }
//
//    override fun changeUiToPlayingBufferingShow() {
//        super.changeUiToPlayingBufferingShow()
//        Debuger.printfLog("Sample changeUiToPlayingBufferingShow")
//        if (!byStartedClick) {
//            setViewShowState(mBottomContainer, INVISIBLE)
//            setViewShowState(mStartButton, INVISIBLE)
//        }
//    }
//
//    override fun changeUiToPlayingShow() {
//        super.changeUiToPlayingShow()
//        Debuger.printfLog("Sample changeUiToPlayingShow")
//        if (!byStartedClick) {
//            setViewShowState(mBottomContainer, INVISIBLE)
//            setViewShowState(mStartButton, INVISIBLE)
//        }
//    }
//
//    override fun startAfterPrepared() {
//        super.startAfterPrepared()
//        Debuger.printfLog("Sample startAfterPrepared")
//        setViewShowState(mBottomContainer, INVISIBLE)
//        setViewShowState(mStartButton, INVISIBLE)
//        setViewShowState(mBottomProgressBar, VISIBLE)
//    }
//
//    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//        byStartedClick = true
//        super.onStartTrackingTouch(seekBar)
//    }
}