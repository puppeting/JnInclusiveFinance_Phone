package com.inclusive.finance.jh.pop


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.CheckProgressAdapter
import com.inclusive.finance.jh.databinding.DialogLeaveSuggestionBinding
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.utils.EditTextRegex
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class ApprovalSuggestionPop(var mContext: Context?,var type: String, val keyId: String,var businessType:Int?=0, var listener: (confirm: Boolean) -> Unit) :
    DialogFragment(), View.OnClickListener {
    var signPath=""
    var operation="agree"
    var mAdapter: CheckProgressAdapter = CheckProgressAdapter()
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_cancel, R.id.iv_close -> {

                listener.invoke(false)
                dismiss()
            }
            R.id.iv_qianzi->
                QianZiBanPop(mContext) { requestCode: Int, url: String ->
                    DataCtrlClass.uploadFiles(mContext, keyId = keyId, type = "", files = arrayListOf(FileUtils.getFileByPath(url))) { urls ->
                        if (urls != null) {
                            GlideApp.with(this).load(SZWUtils.getIntactUrl(urls[0].filePath)).centerInside().into(dataBind.ivQianzi)

                            signPath = urls[0].filePath ?: ""
                        }
                    }

                }.apply {
                    requestCode = 200
                }.show(childFragmentManager, this.javaClass.name)
            R.id.tv_temp -> {
                save(0)
            }
            R.id.tv_ok -> {
                if (ObjectUtils.isEmpty(signPath)) {
                     SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "未签名")
                    return
                }
                if (ObjectUtils.isEmpty(dataBind.etMs.text.toString())) {
                    SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "未填写审批意见")
                    return
                }
                save(1)

            }
            else -> {
            }
        }
    }

    private fun save(type: Int) {
        DataCtrlClass.SXSPNet.saveSignSuggestion(
            mContext,
            keyId,
            signPath,
            dataBind.etMs.text.toString(),
            when (type) {
                0 -> {
                    "1"
                }
                1 -> {
                    ""
                }
                else -> {
                    ""
                }
            },
            operation,
            dataBind.root,
            businessType
        ) {
            if (it != null) {
                listener.invoke(true)
                dismiss()
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
//        DataCtrlClass.SXSPNet.getList_flowHis(mContext, keyId = keyId, type = type, businessType = businessType) {
//            if (it != null) {
//                dataBind.mRecyclerView.layoutManager = LinearLayoutManager(mContext)
//                dataBind.mRecyclerView.adapter = mAdapter
//                mAdapter.setNewInstance(it)
//                if (it.size <= 0) SZWUtils.showSnakeBarMsg(dataBind.root,"暂无进度")
//            }
//        }
        DataCtrlClass.SXSPNet.getSuggestion(mContext, keyId = keyId, type = type, businessType = businessType) {
            if (it != null) {
                GlideApp.with(this).load(SZWUtils.getIntactUrl(SZWUtils.getJsonObjectString(it,"annex"))).centerInside().into(dataBind.ivQianzi)

                signPath=SZWUtils.getJsonObjectString(it,"annex")
                dataBind.etMs.setText(SZWUtils.getJsonObjectString(it,"remarks"))
            }
        }
        if (!manager.isDestroyed) {
            super.show(manager, tag)
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
        Log.e("muuuu","******")
    }

    lateinit var dataBind: DialogLeaveSuggestionBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = DialogLeaveSuggestionBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvCancel.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvTemp.setOnClickListener(this)
        dataBind.ivQianzi.setOnClickListener(this)
        EditTextRegex.textRegex(view = dataBind.etMs)
        dataBind.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            operation = when (checkedId) {
                R.id.rb1 -> {
                    "agree"
                }
                R.id.rb2 -> {
                    "returnTo"
                }
                else -> ""
            }
        }
    }
}