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
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.DialogApproval2Binding
import com.inclusive.finance.jh.utils.EditTextRegex
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class ApprovalPop(var mContext: Context?, val id: String,var businessType:Int=0, var listener: (confirm: Boolean) -> Unit) :
    DialogFragment(), View.OnClickListener {
    var result: String = "agree" //(同意传agree  不同意传reject)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_cancel, R.id.iv_close -> {

                listener.invoke(false)
                dismiss()
            }
            R.id.tv_ok -> {
                if (ObjectUtils.isEmpty(dataBind.etMs.text.toString())) {
                    SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "未填写审批意见")
                    return
                }
                val url=when (businessType) {
                    ApplyModel.BUSINESS_TYPE_VISIT_Approval -> {
                        Urls.save_visit_approval_sp
                    }
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_ZXSP -> {
                        Urls.saveZXSP_sunshine
                    }
                    else -> {
                        Urls.saveZXSP
                    }
                }
                DataCtrlClass.SXSPNet.saveZXSP(mContext,url, id, result, dataBind.etMs.text.toString(),dataBind.root) {
                    if (it != null) {
                        listener.invoke(true)
                        dismiss()
                    }
                }

            }
            else -> {
            }
        }
    }


    override fun onStart() {
        super.onStart()
        StatusBarUtil.immersive(dialog?.window)
        //        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(false)
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.CENTER
        //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

    }

    lateinit var dataBind: DialogApproval2Binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = DialogApproval2Binding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            result = when (checkedId) {
                R.id.rb1 -> {
                    "agree"
                }
                R.id.rb2 -> {
                    "reject"
                }
                else -> ""
            }
        }
        dataBind.tvCancel.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        EditTextRegex.textRegex(view = dataBind.etMs)
    }
}