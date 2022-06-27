package com.inclusive.finance.jh.ui.apply

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonParser
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCustomerInfoBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.ProcessProcessingPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 客户信息
 * */
class CustomerInfoFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCustomerInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCustomerInfoBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CustomerInfoFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@CustomerInfoFragment)
        //        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
//        viewBind.mRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
//            ContextCompat.getDrawable(requireContext(), R.drawable.divider_base)
//                ?.let { setDrawable(it) }
//        })
        viewBind.mRecyclerView.adapter = adapter
    }

    lateinit var idenNo: String
    var getUrl = ""
    var saveUrl = ""
    var temporaryUrl = ""
    var businessType = ""
    override fun initData() {
        //                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //
        //                adapter.setNewInstance(list)
        idenNo = SZWUtils.getJsonObjectString(
            JsonParser.parseString(
                if (viewModel.jsonObject.isNullOrEmpty()) "{}" else viewModel.jsonObject
            ).asJsonObject, "relativeIdenNo"
        )
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            -> {

                getUrl = Urls.khInfoDetail
                saveUrl = Urls.khInfoDetailEdit
                temporaryUrl = Urls.khInfoDetailTemporaryEdit
            }
            ApplyModel.BUSINESS_TYPE_FUPIN ->{
                getUrl = Urls.get_fpf_getHxx
                saveUrl = Urls.get_fpf_geteditHxx
                viewBind.btTemporarySave?.visibility=View.GONE
            }
            ApplyModel.BUSINESS_TYPE_JNJ_YX,
            -> {

                getUrl = Urls.get_jnj_yx_jbxx
                saveUrl = Urls.save_jnj_yx_jbxx
                temporaryUrl = Urls.temporary_jnj_yx_jbxx
                viewBind.btSave.text = "提交"
                adapter.type = "yxzl"
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_SJ,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            -> { //                val qygxr = SZWUtils.getJsonObjectString(JsonParser.parseString(viewModel.jsonObject
                //                    ?: "{}").asJsonObject, "item1")
                //                if (qygxr=="qygxr") {
                //                    getUrl=Urls.get_jnj_company_xxgk_frjgjrcy
                //                    saveUrl=Urls.save_jnj_company_xxgk_frjgjrcy
                //                    temporaryUrl=Urls.temporary_jnj_company_xxgk_frjgjrcy
                //                }else{
                //                    getUrl=Urls.get_jnj_cj_personal_xxgk
                //                    saveUrl=Urls.save_jnj_cj_personal_xxgk
                //                    temporaryUrl=Urls.temporary_jnj_cj_personal_xxgk
                //                }
                getUrl = Urls.get_sj_jbqk
                saveUrl = Urls.save_sj_jbqk
                temporaryUrl = Urls.temporary_sj_list
                viewBind.btSave.text = "提交"
                adapter.type = "yxzl"
            }
            ApplyModel.BUSINESS_TYPE_SJ2,//首检2021
            -> {
                getUrl = Urls.get_queryByIdApp
                temporaryUrl = Urls.temporary_rc_list
                saveUrl = Urls.save_editApp
                viewBind.btSave.text = "提交"
                adapter.type = "yxzl"
            }
            ApplyModel.BUSINESS_TYPE_RC2,//日常检2021
            -> {
                getUrl = Urls.get_dhglxtDailyCheck_queryByIdApp
                temporaryUrl = Urls.temporary_rc_list
                saveUrl = Urls.save_dhglxtDailyCheck_editApp
                viewBind.btSave.text = "提交"
                adapter.type = "yxzl"
            }
            ApplyModel.BUSINESS_TYPE_RC,//日常检
            -> {
                getUrl = Urls.get_rc_jbqk
                temporaryUrl = Urls.temporary_rc_list
                saveUrl = Urls.save_rc_jbqk
                viewBind.btSave.text = "提交"
                adapter.type = "yxzl"
            }
            ApplyModel.BUSINESS_TYPE_RC2,//日常检
            -> {
                getUrl = Urls.get_rc_jbqk
                temporaryUrl = Urls.temporary_rc_list
                saveUrl = Urls.save_rc_jbqk
                viewBind.btSave.text = "提交"
                adapter.type = "yxzl"
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl = Urls.get_visit_xxgk
                saveUrl = Urls.save_visit_xxgk
                temporaryUrl = Urls.temporary_visit_xxgk
                viewBind.btTemporarySave.visibility = View.GONE
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {

                getUrl = Urls.sunshineKhInfoDetail
                saveUrl = Urls.sunshineKhInfoDetailEdit
                temporaryUrl = Urls.sunshineKhInfoDetailTemporaryEdit
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.getKHInfo(requireActivity(), getUrl, keyId = viewModel.keyId, idenNo, businessType, viewModel.jsonObject) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_RC, ApplyModel.BUSINESS_TYPE_JNJ_YX -> {
                        var sb = StringBuffer()
                        it.forEach { bean ->
                            when (bean.dataKey) {
                                "sceneFlag",
                                -> {
                                    fun calculate(it: ArrayList<BaseTypeBean>) {
                                        it.forEach { typeBean ->
                                            when (typeBean.dataKey) {
                                                "item5",
                                                "jydz",
                                                "text1",
                                                "text2",
                                                "nczfa",
                                                -> typeBean.requireable = bean.valueName == "01"
                                                "noSceneReason",
                                                -> typeBean.requireable = bean.valueName != "01"
                                            }
                                            when (typeBean.dataKey) {
                                                "checkStartDate",
                                                -> typeBean.requireable = bean.valueName == "01"
                                            }
                                            when (typeBean.model) {
                                                "imgList",
                                                -> typeBean.requireable = bean.valueName == "01"
                                            }
                                        }
                                    }
                                    bean.addOnPropertyChangedCallback(object :
                                        Observable.OnPropertyChangedCallback() {
                                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                            if (propertyId == BR.valueName) {
                                                calculate(it)
                                            }
                                        }


                                    })
                                    calculate(it)
                                }

                            }
                        }
                    }
                    ApplyModel.BUSINESS_TYPE_SJ2,
                    ApplyModel.BUSINESS_TYPE_RC2-> {
                        var sb = StringBuffer()
                        var sb2 = StringBuffer()
                        it.forEach { bean ->
                            when (bean.dataKey) {
                                "checkDate",
                                -> {
                                    fun calculate(it: ArrayList<BaseTypeBean>) {
                                        it.forEach { typeBean ->
                                            when (typeBean.dataKey) {
                                                "checkStartDate",
                                                -> {
                                                    sb.append(typeBean.valueName + ",")
                                                    Log.e("ddddd", "###" + sb.toString())
                                                    bean.regex = sb.toString()
                                                }
                                                "checkEndDate" -> {
                                                    sb2.append(typeBean.valueName + ",")
                                                    Log.e("ddddd", "###" + sb2.toString())
                                                    bean.regexErrorMsg = sb2.toString()
                                                }
                                            }

                                        }
                                    }
                                    bean.addOnPropertyChangedCallback(object :
                                        Observable.OnPropertyChangedCallback() {
                                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                            if (propertyId == BR.valueName) {
                                                calculate(it)
                                            }
                                        }


                                    })
                                    calculate(it)
                                }
                                "ywqtfx",
                                -> {
                                    fun calculate(it: ArrayList<BaseTypeBean>) {
                                        it.forEachIndexed{ index,typeBean ->
                                            when (typeBean.dataKey) {
                                                "qtfx",
                                                -> {
                                                    if (bean.dataValue == "有") {
                                                        typeBean.visibility = true
                                                    }else{
                                                        typeBean.visibility = false
                                                        typeBean.dataValue=""
                                                    }
                                                    adapter.notifyItemChanged(index)

                                                }
                                            }

                                        }
                                    }
                                    bean.addOnPropertyChangedCallback(object :
                                        Observable.OnPropertyChangedCallback() {
                                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                            if (propertyId == BR.valueName) {
                                                calculate(it)
                                            }
                                        }


                                    })
                                    calculate(it)
                                }
                                "sxjl",
                                -> {
                                    fun calculate(it: ArrayList<BaseTypeBean>) {
                                        it.forEachIndexed{ index,typeBean ->
                                            when (typeBean.dataKey) {
                                                "zzed",
                                                -> {
                                                    if (bean.dataValue == "调减额度") {
                                                        typeBean.visibility = true
                                                    }else{
                                                        typeBean.visibility = false
                                                        typeBean.dataValue=""
                                                    }
                                                    adapter.notifyItemChanged(index)
                                                }
                                            }

                                        }
                                    }
                                    bean.addOnPropertyChangedCallback(object :
                                        Observable.OnPropertyChangedCallback() {
                                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                            if (propertyId == BR.valueName) {
                                                calculate(it)
                                            }
                                        }


                                    })
                                    calculate(it)
                                }
                            }
                        }
                    }
                }
                adapter.setNewInstance(it)

            }
        }

    }

    override fun saveData() {
        if(businessType=="600"){

            DataCtrlClass.KHGLNet.saveBaseTypePoPList2(requireActivity(), saveUrl, adapter.data, mId = viewModel.keyId, idenNo = idenNo, businessType = businessType) {
                if (it != null) {
                    if (isAdded) (activity as BaseActivity).refreshData()
                    when (viewModel.businessType) {
                        ApplyModel.BUSINESS_TYPE_SJ,
                        ApplyModel.BUSINESS_TYPE_RC,
                        ApplyModel.BUSINESS_TYPE_JNJ_YX,
                        -> {
                            DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = viewModel.creditId, businessType = viewModel.businessType, type = SZWUtils.getBusinessType(viewModel.businessType)) { configurationBean ->
                                if (configurationBean != null) {
                                    ProcessProcessingPop(context, configurationBean, keyId = viewModel.keyId, businessType = viewModel.businessType) {
                                        refreshData()
                                    }.show(childFragmentManager, this.javaClass.name)
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }
            }

        }else {
            DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), saveUrl, adapter.data, keyId = viewModel.keyId, idenNo = idenNo, businessType = businessType) {
                if (it != null) {
                    if (isAdded) (activity as BaseActivity).refreshData()
                    when (viewModel.businessType) {
                        ApplyModel.BUSINESS_TYPE_SJ,
                        ApplyModel.BUSINESS_TYPE_RC,
                        ApplyModel.BUSINESS_TYPE_JNJ_YX,
                        -> {
                            DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = viewModel.creditId, businessType = viewModel.businessType, type = SZWUtils.getBusinessType(viewModel.businessType)) { configurationBean ->
                                if (configurationBean != null) {
                                    ProcessProcessingPop(context, configurationBean, keyId = viewModel.keyId, businessType = viewModel.businessType) {
                                        refreshData()
                                    }.show(childFragmentManager, this.javaClass.name)
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btTemporarySave -> {
                when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_SJ2,
                    ApplyModel.BUSINESS_TYPE_RC2,-> {
                        DataCtrlClass.KHGLNet.saveBaseTypePoPList2(requireActivity(), saveUrl, "02",adapter.data, keyId = viewModel.keyId, idenNo = idenNo, businessType = businessType) {
                            if (it != null) {
                                if (isAdded) (activity as BaseActivity).refreshData()
//                                when (viewModel.businessType) {
//                                    ApplyModel.BUSINESS_TYPE_SJ2,
//                                    ApplyModel.BUSINESS_TYPE_RC2,
//                                    -> {
//                                        DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = viewModel.creditId, businessType = viewModel.businessType, type = SZWUtils.getBusinessType(viewModel.businessType)) { configurationBean ->
//                                            if (configurationBean != null) {
//                                                ProcessProcessingPop(context, configurationBean, keyId = viewModel.keyId, businessType = viewModel.businessType) {
//                                                    refreshData()
//                                                }.show(childFragmentManager, this.javaClass.name)
//                                            }
//                                        }
//                                    }
//                                    else -> {
//                                    }
//                                }
                            }
                        }

                    }
                    else -> {
                        DataCtrlClass.ApplyNet.saveTemporary(requireActivity(), temporaryUrl, adapter.data, keyId = viewModel.keyId, idenNo = idenNo, businessType = businessType) {
                            if (it != null) {
                                if (isAdded) (activity as BaseActivity).refreshData()
                            }
                        }
                    }
                }
            }
                viewBind.btSave -> {
                    when (viewModel.businessType) {
                        ApplyModel.BUSINESS_TYPE_SJ2,
                        ApplyModel.BUSINESS_TYPE_RC2-> {
                            DataCtrlClass.KHGLNet.saveBaseTypePoPList2(requireActivity(), saveUrl, "03",adapter.data, keyId = viewModel.keyId, idenNo = idenNo, businessType = businessType) {
                                if (it != null) {
                                    if (isAdded) (activity as BaseActivity).refreshData()

                                }
                            }

                        }
                     else ->{
                         saveData()
                     }
                    }
                }
            }
        }

    }