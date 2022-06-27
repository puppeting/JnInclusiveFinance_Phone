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
import com.inclusive.finance.jh.databinding.PopGhqyjBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class GHQYJPop(var mContext: Context?, val json: JsonObject? = null, var businessType: Int? = 0, var listener: (String) -> Unit? = {}) :
    DialogFragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_cancel, R.id.iv_close -> {

                dismiss()
            }
            R.id.tv_ok -> {
                when (businessType) {
                    ApplyModel.BUSINESS_TYPE_APPLY -> {////授信申请-征信管理-提交-新增选择审批人（新接口）
                        if (dataBind.bm?.text.toString()=="") {
                            SZWUtils.showSnakeBarError(dataBind.root, "请选择审批人")
                            return
                        }
                        listener.invoke(submitKey)
                        dismiss()
                    }

                    else -> {
                        if (!canSubmit) {
                            SZWUtils.showSnakeBarError(dataBind.root, "请选择管户人")
                            return
                        }
                        val jsonArray = JsonArray()
                        jsonArray.add(json)
                        val url: String
                        val jsonName = when (businessType) {
                            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL or ApplyModel.BUSINESS_TYPE_SJ_COMPANY -> {
                                url = Urls.save_jnj_ghq_ghr_sj
                                "sjList"
                            }
                            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL or ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL or ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
                            -> {
                                url = Urls.save_jnj_ghq_ghr_rcj
                                "rcjList"
                            }
                            ApplyModel.BUSINESS_TYPE_QRCODE -> {
                                url = Urls.add_qrcode_list
                                "qrcodeList"
                            }
                            ApplyModel.BUSINESS_TYPE_SHOLI -> {
                                url = Urls.save_shouli_pop
                                jsonObject.addProperty("qrId", SZWUtils.getJsonObjectString(json, "id"))
                                "shouLiList"
                            }

                            else -> {
                                url = Urls.save_jnj_ghq_ghr
                                "jnjList"
                            }
                        }
                        jsonObject.add(jsonName, jsonArray)
                        if (json != null) {
                            DataCtrlClass.JNJNet.save_jnj_ghq(context, url, jsonObject, dataBind.root) {
                                if (it != null) {
                                    listener.invoke(it)
                                    dismiss()
                                }
                            }
                        } else {
                            DataCtrlClass.YXNet.addQrcodeList(context, url, SZWUtils.getJsonObjectString(jsonObject, "key")) {
                                if (it != null) {
                                    listener.invoke(it)
                                    dismiss()
                                }
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
    }


    override fun onStart() {
        super.onStart()
        StatusBarUtil.immersive(dialog?.window) //        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(false)
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.CENTER //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

    }

    private   var submitKey: String = ""
    lateinit var dataBind: PopGhqyjBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopGhqyjBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    val jsonObject = JsonObject()
    var canSubmit = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvCancel.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        var bmUrl = ""
        var ghrUrl = ""
        when (businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY->{//授信申请-征信管理-提交-新增选择审批人（新接口）
                bmUrl = Urls.querySpUser
                dataBind.bmLayOld.visibility=View.GONE
                dataBind.ghrLayOld.visibility=View.GONE
                 dataBind.newBm="选择审批人"
                dataBind.title="审批"
            }
            ApplyModel.BUSINESS_TYPE_SHOLI -> {
                bmUrl = Urls.get_getDepartDropdown_pop
                ghrUrl = Urls.get_getUserDropdown_pop
                dataBind.bmLayOld.visibility=View.VISIBLE
                dataBind.ghrLayOld.visibility=View.VISIBLE
                dataBind.bmOld.text=SZWUtils.getJsonObjectString(json, "acceptOrgName")
                dataBind.ghrOld.text=SZWUtils.getJsonObjectString(json, "acceptManagerName")
                dataBind.oldBm="原受理网点"
                dataBind.oldGhr="原受理客户经理"
                dataBind.newBm="新受理网点"
                dataBind.newGhr="新受理客户经理"
                dataBind.title="受理移交"
            }
            ApplyModel.BUSINESS_TYPE_QRCODE -> {
                dataBind.bmLayOld.visibility=View.GONE
                dataBind.ghrLayOld.visibility=View.GONE
                bmUrl = Urls.get_qrcode_bm
                ghrUrl = Urls.get_qrcode_ghr
                dataBind.newBm="部门"
                dataBind.newGhr="用户"
                dataBind.title="新增二维码"
            }
            else -> {
                dataBind.bmLayOld.visibility=View.GONE
                dataBind.ghrLayOld.visibility=View.GONE
                bmUrl = Urls.get_jnj_ghq_bm
                ghrUrl = Urls.get_jnj_ghq_ghr
                dataBind.newBm="部门"
                dataBind.newGhr="管户人"
                dataBind.title="管护权移交"
            }
        }

        DataCtrlClass.JNJNet.get_jnj_ghq_bm(context, bmUrl) {
            if (it != null) {
                val list = arrayListOf<BaseTypeBean.Enum12>()
                it.forEach { jsonObject ->
                    list.add(BaseTypeBean.Enum12().apply {
                        when (businessType) {
                            ApplyModel.BUSINESS_TYPE_APPLY,ApplyModel.BUSINESS_TYPE_INVESTIGATE-> {////授信申请-征信管理-提交-新增选择审批人（新接口）
                                keyName = SZWUtils.getJsonObjectString(jsonObject, "username")
                                valueName = SZWUtils.getJsonObjectString(jsonObject, "realname")
                            }
                                ApplyModel.BUSINESS_TYPE_SHOLI -> {
                                keyName = SZWUtils.getJsonObjectString(jsonObject, "value")
                                valueName = SZWUtils.getJsonObjectString(jsonObject, "name")
                            }
                            else -> {
                                keyName = SZWUtils.getJsonObjectString(jsonObject, "orgCode")
                                valueName = SZWUtils.getJsonObjectString(jsonObject, "departName")
                            }
                        }

                    })
                }
                dataBind.bm.setOnClickListener { view ->

                    DownPop(dialog, enums12 = list, checkedTextView = view as AppCompatCheckedTextView, isSingleChecked = true) { key, value, position ->
                        dataBind.ghrLay.visibility = View.GONE
                        canSubmit = false
                        if (position != -1&&businessType!= ApplyModel.BUSINESS_TYPE_APPLY) {//授信申请-征信管理-提交-新增选择审批人（新接口）
                            DataCtrlClass.JNJNet.get_jnj_ghq_ghr(context, ghrUrl, key) { ghrList ->
                                if (ghrList != null) {
                                    dataBind.ghrLay.visibility = View.VISIBLE
                                    val ghrDownList = arrayListOf<BaseTypeBean.Enum12>()
                                    ghrList.forEach { jsonObject ->
                                        ghrDownList.add(BaseTypeBean.Enum12().apply {
                                            when (businessType) {
                                                ApplyModel.BUSINESS_TYPE_SHOLI -> {
                                                    keyName = SZWUtils.getJsonObjectString(jsonObject, "value")
                                                    valueName = SZWUtils.getJsonObjectString(jsonObject, "name")
                                                }
                                                else -> {
                                                    keyName = SZWUtils.getJsonObjectString(jsonObject, "userName")
                                                    valueName = SZWUtils.getJsonObjectString(jsonObject, "realName")
                                                }
                                            }
                                        })
                                    }
                                    dataBind.ghr.setOnClickListener { view ->
                                        DownPop(dialog, enums12 = ghrDownList, checkedTextView = view as AppCompatCheckedTextView, isSingleChecked = businessType != ApplyModel.BUSINESS_TYPE_QRCODE) { key, value, _ ->
                                            when (businessType) {
                                                ApplyModel.BUSINESS_TYPE_SJ_PERSONAL or ApplyModel.BUSINESS_TYPE_SJ_COMPANY -> { //                                                manager(管护经理名称)  managerNum(管护经理工号)
                                                    jsonObject.addProperty("managerNum", key)
                                                    jsonObject.addProperty("manager", value)
                                                }
                                                ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL or ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL or ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY -> { //                                                manager(管护经理名称)  managerNum(管护经理工号)
                                                    jsonObject.addProperty("managerNum", key)
                                                    jsonObject.addProperty("manager", value)
                                                }
                                                ApplyModel.BUSINESS_TYPE_QRCODE -> {
                                                    jsonObject.addProperty("key", key)
                                                    jsonObject.addProperty("value", value)
                                                }
                                                ApplyModel.BUSINESS_TYPE_SHOLI -> {
                                                    jsonObject.addProperty("newAcceptManager", key)
                                                    jsonObject.addProperty("newAcceptManagerName", value)
                                                    jsonObject.addProperty("newAcceptOrgCode", SZWUtils.getJsonObjectString(it[position], "value"))
                                                    jsonObject.addProperty("newAcceptOrgName", SZWUtils.getJsonObjectString(it[position], "name"))
                                                }
                                                else -> {
                                                    jsonObject.addProperty("cjrNo", key)
                                                    jsonObject.addProperty("cjr", value)
                                                }
                                            }


                                            canSubmit = position != -1
                                        }.showPopupWindow(view)
                                    }
                                }
                            }
                        }else{
                            submitKey=key
                        }
                    }.showPopupWindow(view)
                }
            }
        }
    }
}