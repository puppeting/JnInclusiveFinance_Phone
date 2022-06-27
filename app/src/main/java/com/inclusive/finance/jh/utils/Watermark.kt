package com.inclusive.finance.jh.utils

import android.app.Activity
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.blankj.utilcode.util.SizeUtils
import kotlin.math.sqrt

/**
 * 水印
 * <pre>
 * author  : Fantasy
 * version : 1.0, 2019-07-29
 * since   : 1.0, 2019-07-29
</pre> *
 */
class Watermark {
    /**
     * 水印文本
     */
    private var mText = ""

    /**
     * 字体颜色，十六进制形式，例如：0xAEAEAEAE
     */
    private var mTextColor: Int

    /**
     * 字体大小，单位为sp
     */
    private var mTextSize: Float

    /**
     * 旋转角度
     */
    private var mRotation: Float

    /**
     * 设置水印文本
     *
     * @param text 文本
     * @return Watermark实例
     */
    fun setText(text: String): Watermark? {
        mText = text
        return sInstance
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色，十六进制形式，例如：0xAEAEAEAE
     * @return Watermark实例
     */
    fun setTextColor(color: Int): Watermark? {
        mTextColor = color
        return sInstance
    }

    /**
     * 设置字体大小
     *
     * @param size 大小，单位为sp
     * @return Watermark实例
     */
    fun setTextSize(size: Float): Watermark? {
        mTextSize = size
        return sInstance
    }

    /**
     * 设置旋转角度
     *
     * @param degrees 度数
     * @return Watermark实例
     */
    fun setRotation(degrees: Float): Watermark? {
        mRotation = degrees
        return sInstance
    }
    /**
     * 显示水印，铺满整个页面
     *
     * @param activity 活动
     * @param text     水印
     */
    /**
     * 显示水印，铺满整个页面
     *
     * @param activity 活动
     */
    fun show(activity: Activity, text: String? = mText) {
        val drawable = WatermarkDrawable()
        drawable.mText = text
        drawable.mTextColor = mTextColor
        drawable.mTextSize = mTextSize
        drawable.mRotation = mRotation
        val rootView: ViewGroup = activity.findViewById(android.R.id.content)
        val layout = FrameLayout(activity)
        layout.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.background = drawable
        rootView.addView(layout)
    }

    private inner class WatermarkDrawable : Drawable() {
        private val mPaint: Paint = Paint()

        /**
         * 水印文本
         */
        var mText: String? = null

        /**
         * 字体颜色，十六进制形式，例如：0xAEAEAEAE
         */
        var mTextColor = 0

        /**
         * 字体大小，单位为sp
         */
        var mTextSize = 0f

        /**
         * 旋转角度
         */
        var mRotation = 0f
        override fun draw(@NonNull canvas: Canvas) {
            val width: Int = bounds.right
            val height: Int = bounds.bottom
            val diagonal = sqrt((width * width + height * height).toDouble()).toInt() // 对角线的长度
            mPaint.color = mTextColor
            mPaint.textSize = SizeUtils.sp2px(mTextSize)
                .toFloat() // ConvertUtils.spToPx()这个方法是将sp转换成px，ConvertUtils这个工具类在我提供的demo里面有
            mPaint.isAntiAlias = true
            val textWidth = mPaint.measureText(mText)
            canvas.drawColor(0x00000000)
            canvas.rotate(mRotation)
            var index = 0
            var fromX: Float
            // 以对角线的长度来做高度，这样可以保证竖屏和横屏整个屏幕都能布满水印
            var positionY = diagonal / 10
            while (positionY <= diagonal) {
                fromX = -width + index++ % 2 * textWidth // 上下两行的X轴起始点不一样，错开显示
                var positionX = fromX
                while (positionX < width) {
                    canvas.drawText(mText!!, positionX, positionY.toFloat(), mPaint)
                    positionX += textWidth * 2
                }
                positionY += diagonal / 10
            }
            canvas.save()
            canvas.restore()
        }

        override fun setAlpha(alpha: Int) {

        }

        override fun setColorFilter(colorFilter: ColorFilter?) {

        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }
    }

    companion object {
        private var sInstance: Watermark? = null
        val instance: Watermark?
            get() {
                if (sInstance == null) {
                    synchronized(Watermark::class.java) { sInstance = Watermark() }
                }
                return sInstance
            }
    }

    init {
        mTextColor = -0x51515152
        mTextSize = 18f
        mRotation = -25f
    }
}