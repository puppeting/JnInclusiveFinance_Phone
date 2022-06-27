package com.inclusive.finance.jh.ui.dh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentBaseInfoBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.ui.ApplyActivity
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 基本信息
 * */
class BaseInfoFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentBaseInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentBaseInfoBinding.inflate(inflater, container, false).apply {
            presenterClick = this@BaseInfoFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data=viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@BaseInfoFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter=adapter
    }
    var getUrl=""
    var saveUrl=""
    var businessType=""
    override fun initData() {
//                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
//                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
//                    mainData,
//                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
//                )
//
//                adapter.setNewInstance(list)
        when (viewModel.businessType) {
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
                getUrl=Urls.get_jnj_cj_personal_jbxx
                saveUrl=Urls.save_jnj_cj_personal_jbxx
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            -> {
                getUrl=Urls.get_visit_jbxx
                saveUrl=Urls.save_visit_jbxx
            }
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl=Urls.get_preCredit_jbxx
                saveUrl=Urls.save_preCredit_jbxx
            }
        }
        businessType=SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(),getUrl, keyId = viewModel.keyId,businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel,it)
                it.forEach { bean ->
                    when (bean.dataKey) {
                        "jyztDm",
                        -> {
                            bean.addOnPropertyChangedCallback(object :
                                Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                    if (propertyId == BR.valueName) {
//                                        jyxmDm : 经营项目
//                                        lxrTel1 ： 联系电话1
//                                        hylxDm ：行业类型
//                                        rzxqFlag：是否有融资需求
                                        calculate(it,bean,adapter)
                                    }
                                }
                            })
                            calculate(it,bean,adapter)
                        }
                    }
                }
                adapter.setNewInstance(it)
            }
        }
    }
    private fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "jyxmDm",
                "lxrTel1",
                "hylxDm",
                "rzxqFlag",
                -> {
                    typeBean.requireable = bean.valueName == "01"
                }
            }
        }
    }
    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId = viewModel.keyId,businessType=businessType) {
            if (it != null) {
                if (isAdded){
                    when (viewModel.businessType) {
                         ApplyModel.BUSINESS_TYPE_VISIT_NEW-> {
                             if (activity is ApplyActivity){
                                 (activity as ApplyActivity).zfId=it
                                 (activity as ApplyActivity).keyId=it
                                 (activity as ApplyActivity).businessType= ApplyModel.BUSINESS_TYPE_VISIT_EDIT
                                 (activity as ApplyActivity).init()
                             }
                        }
                        else -> {
                            refreshData()
                            (activity as BaseActivity).refreshData()
                        }
                    }


                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                saveData()
            }
        }
    }

}