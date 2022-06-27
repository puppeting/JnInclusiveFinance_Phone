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
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.PopBaseTypeBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class CreditManagerPop(var mContext: Context?, var fragment: MyBaseFragment, val title: String, var getUrl: String? = "", var saveUrl: String? = "", var enumUrl: String? = "", var json: JsonObject? = null, var keyId: String? = null, var listener: (() -> Unit?)? = {}) :
    DialogFragment(), View.OnClickListener {
    var adapter: ItemBaseTypeAdapter<BaseTypeBean> = ItemBaseTypeAdapter(fragment)
    var flag = ""
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close, R.id.tv_cancel -> {
                dismiss()
            }
            R.id.tv_ok -> {
                DataCtrlClass.KHGLNet.saveBaseTypePoPList(mContext, saveUrl, adapter.data, keyId = keyId,jsonObject= json, contentView = dataBind.root) {
                    if (it != null) {
                        listener?.invoke()
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

    lateinit var dataBind: PopBaseTypeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopBaseTypeBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    var enumList = arrayListOf<JsonObject>()
    override fun show(manager: FragmentManager, tag: String?) {
        if (json == null && !title.contains("新增")) {
            SZWUtils.showSnakeBarMsg("请选择一条数据")
        } else {
            DataCtrlClass.KHGLNet.getBaseTypePoPList(mContext, getUrl, flag, json, keyId) {
                if (it != null) {
                    if (saveUrl.isNullOrEmpty()) {
                        it.forEach { baseTypeBean ->
                            baseTypeBean.editable = false
                        }
                    }
                    DataCtrlClass.KHGLNet.getCreditManagerCyList(mContext,enumUrl, keyId) { enumList ->
                        if (enumList != null) {
                            this.enumList = enumList
                            val enum12List = arrayListOf<BaseTypeBean.Enum12>()
                            enumList.forEach { bean -> enum12List.add(Gson().fromJson(Gson().toJson(bean), BaseTypeBean.Enum12::class.java)) }

                            if (saveUrl.isNullOrEmpty()) {
                                it.forEach { baseTypeBean ->
                                    baseTypeBean.editable = false
                                }
                            }
                            adapter.setNewInstance(it)
                            adapter.data[0].enums12 = enum12List
                            it.forEach { bean ->
                                when (bean.dataKey) {
                                    "cy" -> {
                                        bean.valueName = it.firstOrNull { item -> item.dataKey == "custName" }?.valueName
                                            ?: ""
                                        bean.addOnPropertyChangedCallback(object :
                                            Observable.OnPropertyChangedCallback() {
                                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                                if (propertyId == BR.valueName) {
                                                    val jsonObjectBean = enumList.firstOrNull { jsonObject -> SZWUtils.getJsonObjectString(jsonObject, "valueName") == bean.valueName }
                                                    if (jsonObjectBean != null) {
                                                        it.firstOrNull { item -> item.dataKey == "idenNo" }?.valueName = SZWUtils.getJsonObjectString(jsonObjectBean, "idenNo")
                                                        it.firstOrNull { item -> item.dataKey == "custName" }?.valueName = SZWUtils.getJsonObjectString(jsonObjectBean, "valueName")
                                                        it.firstOrNull { item -> item.dataKey == "mobilePhone" }?.valueName = SZWUtils.getJsonObjectString(jsonObjectBean, "mobilePhone")
                                                    }

                                                }
                                            }
                                        })
                                    }
                                }

                            }
                        }
                        if (!manager.isDestroyed) {
                            super.show(manager, tag)
                        }
                    }

                }
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvTitle.text = title
        adapter.dialogPop = dialog
        val mRecyclerView = dataBind.mRecyclerView
        mRecyclerView.adapter = adapter

        //        val mainData = SZWUtils.getJson(mContext, "客户管理新增.json")
        //        val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //            mainData,
        //            object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //        )
        //
        //        adapter.setNewInstance(list)


        if (title.contains("查看")) {
            dataBind.btLay.visibility = View.GONE
        } else if (title.contains("修改")) {
            flag = "edit"
        }


        dataBind.tvCancel.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
    }
}