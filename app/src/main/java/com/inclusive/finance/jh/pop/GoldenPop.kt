package com.inclusive.finance.jh.pop


import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.PopGoldenBinding
import com.inclusive.finance.jh.utils.SoundUtil


class GoldenPop(var mContext: Context?, var listener: (confirm: Boolean) -> Unit? = {}) :
    DialogFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        //        KeyboardUtils.hideSoftInput(findViewById<EditText>(R.id.tv))
        when (v?.id) {
            R.id.tv_cancel -> {
                listener.invoke(false)
                dismiss()
            }
            R.id.tv_ok -> {
                listener.invoke(true)
                dismiss()
            }
            else -> {
            }
        }
    }

    //    override fun onCreateContentView(): View {
    //        return createPopupById(R.layout.pop_confirm)
    //    }
    override fun onStart() {
        super.onStart()
        //        StatusBarUtil.immersive(dialog?.window)
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(true)
        //        params?.width = RelativeLayout.LayoutParams.WRAP_CONTENT
        //        params?.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        //        params?.gravity = Gravity.CENTER
        //        //高度自己定义
        //        dialog?.window?.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params?.dimAmount = 0f
        params?.flags = params?.flags ?: 0 or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        dialog?.window?.attributes = params
    }

    lateinit var dataBind: PopGoldenBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopGoldenBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.lottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                SoundUtil.playSound()
            }

            override fun onAnimationEnd(animation: Animator?) {
                dismiss()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
    }
}