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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.databinding.PopBaseTypeBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import org.jetbrains.anko.support.v4.act


class BaseTypePop(var mContext: Context?, var fragment: MyBaseFragment, val title: String, var getUrl: String? = "", var saveUrl: String? = "", var json: JsonObject? = null, var keyId: String? = null,var mId: String? = null,var contractType: String? = null, var businessType: String? = null, var subscribe: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit?)? = { a, b, c -> }, var listener: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>, str: String) -> Unit)? = { adapter, resultStr -> }) :
    DialogFragment(), View.OnClickListener {
    var adapter: ItemBaseTypeAdapter<BaseTypeBean> = ItemBaseTypeAdapter(fragment)
    var flag = ""
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close, R.id.tv_cancel -> {
                dismiss()
            }
            R.id.tv_ok -> {
                if(businessType=="500") {//合同签约新增
                    DataCtrlClass.KHGLNet.saveBaseTypePoPList2(mContext, saveUrl, adapter.data, jsonObject = json, contentView = dataBind.root, keyId = keyId,mId = mId,contractType=contractType, businessType = businessType) {
                        if (it != null) {
                            listener?.invoke(adapter, it)
                            dismiss()
                        }
                    }
                }else if(businessType=="600"){

                        context?.let {
                            var HJBH: String = PreferencesService.getHJBH(
                                it
                            ).toString()

                            DataCtrlClass.KHGLNet.saveBaseTypeJtxx(mContext, saveUrl, HJBH, adapter.data, jsonObject = json, contentView = dataBind.root, keyId = "", mId = mId, contractType = contractType, businessType = businessType) {
                                if (it != null) {
                                    listener?.invoke(adapter, it)
                                    dismiss()
                                }
                            }

                    }
                } else {
                    DataCtrlClass.KHGLNet.saveBaseTypePoPList(mContext, saveUrl, adapter.data, jsonObject = json, contentView = dataBind.root, keyId = keyId, businessType = businessType) {
                        if (it != null) {
                            listener?.invoke(adapter, it)
                            dismiss()
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

    lateinit var dataBind: PopBaseTypeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopBaseTypeBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvTitle.text = title
        adapter.dialogPop = dialog
//        dataBind.mRecyclerView.layoutManager = GridLayoutManager(mContext, 6)
        dataBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        dataBind.mRecyclerView.setItemViewCacheSize(200)
        dataBind.mRecyclerView.adapter = adapter
        dataBind.mRecyclerView.setHasFixedSize(false)
        subscribe?.invoke(adapter, adapter.data as ArrayList<BaseTypeBean>, dataBind.root) //        val mainData = SZWUtils.getJson(context, "客户管理新增.json")
        //        val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //            mainData,
        //            object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //        )
        //
        //        adapter.setNewInstance(list)


        if (title.contains("查看")||saveUrl.isNullOrEmpty()) {
            dataBind.btLay.visibility = View.GONE
        } else if (title.contains("修改")) {
            flag = "edit"
        }


        dataBind.tvCancel.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (json == null && !title.contains("新增")) {
            SZWUtils.showSnakeBarMsg("请选择一条数据")
        } else {
            if(businessType=="500"){//合同签约新增
                DataCtrlClass.KHGLNet.getBaseTypePoPList2(mContext, getUrl, flag, json, keyId = keyId, mId = mId,contractType=contractType,businessType = businessType) {
                    if (it != null) {
                        if (saveUrl.isNullOrEmpty()) {
                            it.forEach { baseTypeBean ->
                                baseTypeBean.editable = false
                            }
                        }
                        adapter.setNewInstance(it)
                        if (!manager.isDestroyed) {
                            super.show(manager, tag)
                        }
                    }
                }
            }else {
                DataCtrlClass.KHGLNet.getBaseTypePoPList(mContext, getUrl, flag, json, keyId = keyId, businessType = businessType) {
                    if (it != null) {
                        if (saveUrl.isNullOrEmpty()) {
                            it.forEach { baseTypeBean ->
                                baseTypeBean.editable = false
                            }
                        }
                        adapter.setNewInstance(it)
                        if (!manager.isDestroyed) {
                            super.show(manager, tag)
                        }
                    }
                }
            }

        }
    }


}