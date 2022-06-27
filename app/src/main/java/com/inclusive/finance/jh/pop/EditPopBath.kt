package com.inclusive.finance.jh.pop


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.*
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.databinding.PopBianjibanbathBinding
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil


class EditPopBath(var mcontext: Context?, val businessType: Int = ApplyModel.BUSINESS_TYPE_APPLY, val title: String, var view: TextView? = null, var regex: String? = "", var regexErrorMsg: String? = "", var listener: (str: String) -> Unit? = {}) :
    DialogFragment(){
    var fj = ""



    var dataBind: PopBianjibanbathBinding? = null
    override fun onStart() {
        super.onStart()
        StatusBarUtil.immersive(dialog?.window)
        //        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(true)
        params?.width = RelativeLayout.LayoutParams.WRAP_CONTENT
        params?.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.CENTER
        //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopBianjibanbathBinding.inflate(inflater, container, false)
        dataBind?.tvTitle?.text=title
        dataBind?.ivQianzi?.setOnClickListener {
            QianZiBanPop(context) { requestCode: Int, url: String ->
                DataCtrlClass.uploadFiles(
                    context, keyId = "", type = "", files = arrayListOf(FileUtils.getFileByPath(url))
                ) { urls ->
                    if (urls != null) {
                        mcontext?.let {
                            dataBind?.ivQianzi?.let { it1 ->
                                GlideApp.with(it).load(SZWUtils.getIntactUrl(urls[0].filePath))
                                    .centerInside().into(it1)
                            }
                        }

                        fj = urls[0].filePath ?: ""
                    }
                }

            }.apply {
                requestCode = 200
            }.show(childFragmentManager, this.javaClass.name)
        }
        dataBind?.ivClose?.setOnClickListener { dismiss() }
        dataBind?.tvOk?.setOnClickListener {
            if (dataBind?.tv?.text.toString() != "" && fj != "") {
                listener.invoke(dataBind?.tv?.text.toString() + "," + fj)
                dismiss()
            } else {
                SZWUtils.showSnakeBarMsg(contentView = dataBind?.root, "请填写所有必填项")
            }
        }

        return dataBind?.root!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


}