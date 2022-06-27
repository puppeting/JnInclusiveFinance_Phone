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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.PopCreditmanagerDzyBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import org.jetbrains.anko.support.v4.act


class CreditManagerDZYPop(var mContext: Context?, var fragment: MyBaseFragment, var showActionBar: Boolean, var title: String, var getUrl: String? = "", var saveUrl: String? = "", var json: JsonObject? = null, var keyId: String? = null, var mainContractNo: String? = null, var htId: String? = null, var businessType: String? = null, var subscribe: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit?)? = { a, b, c -> }, var listener: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>) -> Unit)? = {}) :
    DialogFragment(), PresenterClick {

    var adapter: ItemBaseTypeAdapter<BaseTypeBean> = ItemBaseTypeAdapter(fragment)
    var flag = ""
    override fun onClick(v: View?) {
        when (v) {
            dataBind.ivClose, dataBind.tvCancel -> {
                dismiss()
            }
            dataBind.chipAdd -> {

                CreditManagerDZYPop(mContext, fragment, false, "选择贷款方式", Urls.get_signing_contract_dzy_fs_pop, Urls.save_signing_contract_dzy_fs_pop, JsonObject(), keyId, htId = htId, mainContractNo = mainContractNo){
                    initData()
                }.show(childFragmentManager, this.javaClass.name)
            }
            dataBind.chipEdit -> {
                SZWUtils.getJsonObjectBeanFromList(adapter.data[0].listBean?.list) { jsonObject ->
                    CreditManagerDZYPop(mContext, fragment, false, "编辑", Urls.edit_signing_contract_dzy_pop, Urls.save_signing_contract_dzy_pop, jsonObject, keyId=keyId, htId=htId,mainContractNo = mainContractNo).show(childFragmentManager, this.javaClass.name)
                }
            }
            dataBind.chipDelete -> {
                SZWUtils.getJsonObjectBeanFromList(adapter.data[0].listBean?.list) { jsonObject ->
                    ConfirmPop(context, "确定删除?") {
                        if (it) DataCtrlClass.CreditManagementNet.deleteById(context, Urls.delete_signing_contract_dzy_pop, SZWUtils.getJsonObjectString(jsonObject, "id"), keyId = keyId) {
                            initData()
                        }
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }

            dataBind.tvOk -> {
                if (!saveUrl.isNullOrEmpty()) {
                    if (title == "选择贷款方式") {
                        DataCtrlClass.CreditManagementNet.saveDZYPoPList(mContext, saveUrl, adapter.data, jsonObject = json, contentView = dataBind.root, keyId = keyId, htId = htId, businessType = businessType) {
                            if (it != null) {
                                if (title == "选择贷款方式") {
                                    adapter.setNewInstance(it)
                                    title= "新增"
                                    dataBind.tvTitle.text = title
                                    saveUrl = Urls.save_signing_contract_dzy_pop
                                } else {
                                    listener?.invoke(adapter)
                                    dismiss()
                                }

                            }
                        }
                    } else {
                        DataCtrlClass.CreditManagementNet.saveDZYPoPListString(mContext, saveUrl, adapter.data, jsonObject = json, contentView = dataBind.root, keyId = keyId, htId = htId, businessType = businessType) {
                            if (it != null) {
                                listener?.invoke(adapter)
                                dismiss()
                            }
                        }
                    }
                } else {
                    listener?.invoke(adapter)
                    dismiss()
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

    lateinit var dataBind: PopCreditmanagerDzyBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopCreditmanagerDzyBinding.inflate(inflater, container, false)
        dataBind.data = ViewModelProvider(act).get(ApplyModel::class.java)
        dataBind.presenterClick = this
        return dataBind.root
    }
   fun  initData(){
        DataCtrlClass.CreditManagementNet.getDZYPoPList(mContext, getUrl, flag, json, keyId = keyId, htId = htId, mainContractNo = mainContractNo, businessType = businessType) {
            if (it != null) {
                adapter.setNewInstance(it)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvTitle.text = title
        dataBind.chipGroup.visibility = if (showActionBar) {
            View.VISIBLE
        } else View.GONE
        adapter.dialogPop = dialog
        dataBind.mRecyclerView.layoutManager = GridLayoutManager(mContext, 6)
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
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (json == null && !title.contains("新增")) {
            SZWUtils.showSnakeBarMsg("请选择一条数据")
        } else {
            DataCtrlClass.CreditManagementNet.getDZYPoPList(mContext, getUrl, flag, json, keyId = keyId, htId = htId, mainContractNo = mainContractNo, businessType = businessType) {
                if (it != null) {
                    adapter.setNewInstance(it)
                    if (!manager.isDestroyed) {
                        super.show(manager, tag)
                    }
                }
            }

        }
    }


}