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
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ScreenUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.PopKhfpBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class KHFPPop(var mContext: Context?, val jsons: List<JsonObject>, var businessType: Int? = 0, var isGhq: Boolean = false) :
    DialogFragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_cancel, R.id.iv_close -> {

                dismiss()
            }
            R.id.tv_ok -> {
                if (!canSubmit) {
                    SZWUtils.showSnakeBarError(dataBind.root, "请选择管户人")
                    return
                }
                val jsonArray = JsonArray()
                jsons.forEach {
                    jsonArray.add(it)
                }
                val url: String
                val jsonName = when (businessType) {
                    ApplyModel.BUSINESS_TYPE_VISIT_NEW or ApplyModel.BUSINESS_TYPE_VISIT_EDIT -> {
                        if (!isGhq) {
                            url = Urls.save_visit_kh_ghr
                            "zfList"
                        } else {
                            url = Urls.save_visit_ghq_ghr
                            "zfList"
                        }
                    }
                    else -> {
                        url = Urls.save_visit_ghq_ghr
                        "zfList"
                    }
                }
                jsonObject.add(jsonName, jsonArray)
                DataCtrlClass.VisitNet.save_visit_ghq(context, url, jsonObject, dataBind.root) {
                    if (it != null) {
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

    lateinit var dataBind: PopKhfpBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopKhfpBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    val jsonObject = JsonObject()
    var canSubmit = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvCancel.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        DataCtrlClass.VisitNet.get_visit_kh_ghr(context) { ghrList ->
            if (ghrList != null) {
                val ghrDownList = arrayListOf<BaseTypeBean.Enum12>()
                ghrList.forEach { jsonObject ->
                    ghrDownList.add(BaseTypeBean.Enum12().apply {
                        keyName = SZWUtils.getJsonObjectString(jsonObject, "userName")
                        valueName = SZWUtils.getJsonObjectString(jsonObject, "realName")
                    })
                }
                dataBind.ghr.setOnClickListener { view ->
                    DownPop(dialog, enums12 = ghrDownList, checkedTextView = view as AppCompatCheckedTextView, isSingleChecked = true) { key, value, position ->
                        when (businessType) {
                            ApplyModel.BUSINESS_TYPE_VISIT_NEW or ApplyModel.BUSINESS_TYPE_VISIT_EDIT -> {
                                //                                                manager(管护经理名称)  managerNum(管护经理工号)
                                jsonObject.addProperty("zfrNo", key)
                                jsonObject.addProperty("zfr", value)
                            }
                        }


                        canSubmit = true
                    }.showPopupWindow(view)
                }
            }
        }
    }
}