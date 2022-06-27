package com.inclusive.finance.jh.widget.video

import android.content.Context
import android.util.AttributeSet

import com.inclusive.finance.jh.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

/**
 * 无任何控制ui的播放
 * Created by guoshuyu on 2017/8/6.
 */

class EmptyControlVideo : StandardGSYVideoPlayer {

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getLayoutId(): Int {
        return R.layout.empty_control_video
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false
    }

}