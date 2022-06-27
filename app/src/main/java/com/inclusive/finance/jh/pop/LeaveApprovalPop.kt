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
import com.inclusive.finance.jh.databinding.DialogLeaveApprovalBinding
import com.inclusive.finance.jh.utils.EditTextRegex
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class LeaveApprovalPop(var mContext: Context?, val id: String, var listener: (confirm: Boolean) -> Unit) :
    DialogFragment(), View.OnClickListener {
    var result: String = "通过" //(同意传agree  不同意传reject)
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
                DataCtrlClass.SystemManagerNet.save_main_leaveApprove(mContext, id, result, dataBind.etMs.text.toString()) {
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

    lateinit var dataBind: DialogLeaveApprovalBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = DialogLeaveApprovalBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            result = when (checkedId) {
                R.id.rb1 -> {
                    "通过"
                }
                R.id.rb2 -> {
                    "否决"
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