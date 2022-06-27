package com.inclusive.finance.jh.widget

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import com.inclusive.finance.jh.R


/**
 * Created by AMing on 15/11/2.
 * Company RongCloud
 */
open class ClearPwdEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyleAttr), View.OnFocusChangeListener, TextWatcher {

    /**
     * 删除按钮的引用
     */
    private lateinit var visibleDrawable: Drawable
    private lateinit var goneDrawable: Drawable
    private var isVisible=true

    init {
        init()
    }

    private fun init() {
        visibleDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off_black_24dp)!!
        visibleDrawable.setBounds(0, 0, visibleDrawable.intrinsicWidth, visibleDrawable.intrinsicHeight)
        goneDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_black_24dp)!!
        goneDrawable.setBounds(0, 0, goneDrawable.intrinsicWidth, goneDrawable.intrinsicHeight)
        setClearIconVisible(false)
        this.onFocusChangeListener = this
        this.addTextChangedListener(this)
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        setClearIconVisible(s.isNotEmpty())
        if (s.length >20){
            setShakeAnimation()
            setText(s.toString().subSequence(0,(s.length)-1))
            setSelection(text?.length?:0)
        }
    }


    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible  boolean
     */
     fun setClearIconVisible(visible: Boolean) {
        val right = if (visible) if (!isVisible) visibleDrawable else goneDrawable else null
        setCompoundDrawables(compoundDrawables[0],
                compoundDrawables[1], right, compoundDrawables[3])
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            if (event.action == MotionEvent.ACTION_UP) {
                val touchable = event.x > width- paddingRight - visibleDrawable.intrinsicWidth && event.x < width - paddingRight
                if (touchable) {
                    isVisible=!isVisible
                    transformationMethod = if (!isVisible)
                        HideReturnsTransformationMethod.getInstance()
                    else
                        PasswordTransformationMethod.getInstance()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text?.isNotEmpty()==true)
        } else {
            setClearIconVisible(false)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun afterTextChanged(s: Editable) {

    }

    /**
     * 设置晃动动画
     */
    fun setShakeAnimation() {
        this.startAnimation(shakeAnimation(3))
    }

    companion object {


        /**
         * 晃动动画
         * @param counts 半秒钟晃动多少下
         * @return  animation
         */
        fun shakeAnimation(counts: Int): Animation {
            val translateAnimation = TranslateAnimation(0f, 10f, 0f, 0f)
            translateAnimation.interpolator = CycleInterpolator(counts.toFloat())
            translateAnimation.duration = 500
            return translateAnimation
        }
    }
}//这里构造方法也很重要，不加这个很多属性不能再XML里面定义
