package com.inclusive.finance.jh.ui.apply.credit

import android.os.Bundle
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
 import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.inclusive.finance.jh.DataCtrlClass
 import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
 import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCreditAuthorizationInfoBinding
import com.inclusive.finance.jh.interfaces.OnRichLayoutChange
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.pop.GHQYJPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 征信授权书
 * */
class CreditAuthorizationFragment : MyBaseFragment(), PresenterClick, OnRichLayoutChange {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditAuthorizationInfoBinding
    private var getUrl = ""
    private var saveUrl = ""
    private var deleteUrl = ""
    private var submitUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCreditAuthorizationInfoBinding.inflate(inflater, container, false)
            .apply {
                presenterClick = this@CreditAuthorizationFragment
                viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
                data = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@CreditAuthorizationFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter = adapter
        adapter.keyId = viewModel.keyId
        adapter.type = "yxzl"
        adapter.onRichLayoutChangeListener = this
    }

    override fun initData() { //        val mainData = SZWUtils.getJson(context, "征信授权.json")
        //        val list = Gson().fromJson<MutableList<BaseTypeBean>>(mainData, object :
        //            TypeToken<ArrayList<BaseTypeBean>>() {}.type)
        //
        //        adapter.setNewInstance(list)
        when (viewModel.title) {
            "证件上传" -> {
                 when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL -> {
                        getUrl = Urls.get_creditManager_zj
                        saveUrl = Urls.save_creditManager_zj
                    }
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_ZXSP
                    -> {
                        getUrl = Urls.get_sunshine_CreditManagerZJ
                        saveUrl = Urls.save_sunshine_CreditManagerZJ
                    }
                    else -> {
                        getUrl = Urls.getCreditManagerZJ
                        saveUrl = Urls.saveCreditManagerZJ

                    }
                }
                deleteUrl = ""
                viewBind.btDelete.visibility = View.GONE
            }
            "征信授权书" -> {

                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_APPLY,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                    ApplyModel.BUSINESS_TYPE_ZXSP,
                    ApplyModel.BUSINESS_TYPE_ZXFHQZ,
                    -> {
                        getUrl = Urls.getCreditManagerSQS
                        saveUrl = Urls.saveCreditManagerSQS
                        deleteUrl = Urls.deleteCreditManagerSQS
                        submitUrl = Urls.creditAnalysisAdd
                        viewBind.btDelete.visibility = View.VISIBLE
                    }
                    ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL,
                    -> {
                        getUrl = Urls.getCreditManagerSQS
                        saveUrl = Urls.saveCreditManagerSQS
                        deleteUrl = Urls.deleteCreditManagerSQS
                        submitUrl = Urls.creditAnalysisAdd
                        viewBind.btDelete.visibility = View.VISIBLE
                        businessType = SZWUtils.getBusinessType(viewModel.businessType)
                    }
                    ApplyModel.BUSINESS_TYPE_JNJ_YX,
                    ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
                    ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
                    -> {
                        getUrl = Urls.get_jnj_cj_personal_zxgl_sqs
                        saveUrl = Urls.save_jnj_cj_personal_zxgl_sqs
                        deleteUrl = ""
                        submitUrl = Urls.submit_jnj_cj_personal_zxgl
                        viewBind.btDelete.visibility = View.GONE
                    }

                    ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                    ApplyModel.BUSINESS_TYPE_SUNSHINE_ZXSP,
                    -> {
                        getUrl = Urls.getCreditManagerSQS
                        saveUrl = Urls.saveCreditManager_sunshine_SQS
                        deleteUrl = Urls.deleteCreditManagerSQS
                        submitUrl = Urls.submit_sunshine_CreditAnalysisAdd
                        viewBind.btDelete.visibility = View.VISIBLE
                    }
                }

            }
            "征信授权-资料上传" -> {
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_ZXSP,
                    ApplyModel.BUSINESS_TYPE_JNJ_YX,
                    ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
                    ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
                    ApplyModel.BUSINESS_TYPE_DH_ZXSP,

                    -> {
                        getUrl = Urls.get_jnj_cj_personal_zxgl_sqs
                        saveUrl = Urls.save_jnj_cj_personal_zxgl_sqs
                        deleteUrl = ""
                        submitUrl = Urls.submit_jnj_cj_personal_zxgl
                        viewBind.btDelete.visibility = View.GONE
                    }
                }

            }
        }

        DataCtrlClass.ApplyNet.getCreditAuthorizationInfo(requireActivity(), url = getUrl, viewModel.keyId, viewModel.jsonObject, businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)

                adapter.setNewInstance(it)
            } else adapter.notifyDataSetChanged()
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, url = saveUrl, adapter.data, keyId = viewModel.keyId, jsonObject = JsonParser.parseString(viewModel.jsonObject
            ?: "{}").asJsonObject, businessType = businessType) {
            if (it != null) {
                when (viewModel.title) {
                    "证件上传"->{
                        viewBind.btSubmit.visibility = View.GONE
                    }
                    else ->{
                        viewBind.btSubmit.visibility = View.VISIBLE
                    }
                }
                 if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btDelete -> {
                ConfirmPop(context, "确定删除吗？") {
                    if (it) {
                        DataCtrlClass.ApplyNet.deleteCreditAuthorizationInfo(context, deleteUrl, viewModel.keyId, viewModel.jsonObject) {
                            refreshData()
                            if (isAdded) (activity as BaseActivity).refreshData()
                        }
                    }

                }.show(childFragmentManager, this.javaClass.name)

            }
            viewBind.btSave -> {
                saveData()
            }
            viewBind.btSubmit -> {


                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_APPLY,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                    -> {
                        val jsonObject: JsonObject? =JsonParser().parse(viewModel.jsonObject).getAsJsonObject();

//                        if(SZWUtils.getJsonObjectString(jsonObject, "state")=="500"){

                            DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, viewModel.jsonObject, dhId = viewModel.dhId, businessType = businessType) {
                                refreshData()
                            }
//                        }else {
//                             GHQYJPop(context, jsonObject, ApplyModel.BUSINESS_TYPE_APPLY) {
//                                jsonObject?.addProperty("sprgh", ""+it)
//                                DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
//                                    refreshData()
//                                }
//                            }.show(childFragmentManager, this.javaClass.name)
//                        }

                    }
                    else -> {
                        DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, viewModel.jsonObject, dhId = viewModel.dhId, businessType = businessType) {
                            refreshData()
                        }
                    }
                }
            } //            viewBind.btNext -> {
            //                ConfirmPop(context, "") {
            //
            //                }.showPopupWindow()
            //            }
        }
    }

    override fun onClick(key: String) {
        refreshData()
    }

}