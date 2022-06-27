package com.inclusive.finance.jh.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.TimeUtils
import com.inclusive.finance.jh.R
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.*


open class CustomProgress : Dialog {
    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    protected constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener)

    /**
     * 当窗口焦点改变时调用
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        val imageView = findViewById<View>(R.id.spinnerImageView) as ImageView
//        // 获取ImageView上的动画背景
//        val spinner = imageView.background as AnimationDrawable
//        // 开始动画
//        spinner.start()
    }

    /**
     * 给Dialog设置提示信息
     *
     * @param message
     */
    fun setMessage(message: CharSequence?) {
        if (message != null && message.isNotEmpty()) {
            findViewById<View>(R.id.message).visibility = View.VISIBLE
            val txt = findViewById<View>(R.id.message) as TextView
            txt.text = message
            txt.invalidate()
        }
    }

    companion object {


        private var sProgress: CustomProgress? = null
        private var isDismiss: Boolean=true

        /**
         * 弹出自定义ProgressDialog
         *
         * @param context        上下文
         * @param message        提示
         * @param cancelable     是否按返回键取消
         * @param cancelListener 按下返回键监听
         * @return
         */
        fun show(context: Context?, message: CharSequence?, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?): CustomProgress? {
            if (sProgress != null && sProgress?.isShowing == true) {
                return sProgress
            }

            try {
                sProgress = CustomProgress(requireNotNull(context), R.style.Custom_Progress)
            } catch (e: Exception) {
                UMCrash.generateCustomLog(e,"UmengException")
            }
            sProgress?.setTitle("")
            sProgress?.setContentView(R.layout.custom_prograss_dialog_layout)
            if (message == null || message.isEmpty()) {
                sProgress?.findViewById<View>(R.id.message)?.visibility = View.GONE
            } else {
                val txt = sProgress?.findViewById<View>(R.id.message) as TextView
                txt.text = message
            }
            // 按返回键是否取消
            sProgress?.setCancelable(cancelable)
            sProgress?.setCanceledOnTouchOutside(false)
            // 监听返回键处理
            if (cancelListener != null) {
                sProgress?.setOnCancelListener(cancelListener)
            }
            // 设置居中
            sProgress?.window?.attributes?.gravity = Gravity.CENTER
            val lp = sProgress?.window?.attributes
            // 设置背景层透明度
            lp?.dimAmount = 0.2f
            sProgress?.window?.attributes = lp
            // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            isDismiss=false

            GlobalScope.launch(Dispatchers.Main) {
                delay(200)
                sProgress?.show()
                loadingDismiss(TimeUtils.getNowMills())
            }

            return sProgress
        }

        private fun loadingDismiss(nowMills: Long) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(500)
                if (isDismiss) {
                    sProgress?.dismiss()
                } else {
                    if ((TimeUtils.getNowMills()-10000)>nowMills){
                        isDismiss=true
                    }
                    loadingDismiss(nowMills)
                }
                cancel()
            }
        }


        /**
         * 关闭dialog
         */
        fun disMiss() {
            isDismiss=true
        }

        /**
         * 关闭dialog
         */
        fun disMissNow() {
            if (sProgress != null && sProgress?.isShowing == true) {
                sProgress?.dismiss()
                isDismiss=true
            }
        }
    }
}