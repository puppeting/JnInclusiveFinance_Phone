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
import com.inclusive.finance.jh.databinding.FragmentJianyanjielunBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.ProcessProcessingPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 检验结论
 * */
class JianYanJieLunFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentJianyanjielunBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentJianyanjielunBinding.inflate(inflater, container, false).apply {
            presenterClick = this@JianYanJieLunFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@JianYanJieLunFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
    }

    var getUrl = ""
    var saveUrl = ""
    var businessType = ""

    override fun initData() {
        //                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //
        //                adapter.setNewInstance(list)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            -> {
                getUrl = Urls.get_jnj_jc_offSite_jyjl
                saveUrl = Urls.save_jnj_jc_offSite_jyjl
                //                businessType="03"
            }
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            -> {
                getUrl = Urls.get_jnj_jc_onSite_jyjl
                saveUrl = Urls.save_jnj_jc_onSite_jyjl
            }
            ApplyModel.BUSINESS_TYPE_SJ,
            -> {
                getUrl = Urls.get_sj_jyjl2
                saveUrl = Urls.save_sj_jyjl2
            }
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            -> {
                getUrl = Urls.get_sj_jyjl
                saveUrl = Urls.save_sj_jyjl
            }
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            -> {
                getUrl = Urls.get_rcj_jyjl
                saveUrl = Urls.save_rcj_jyjl
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), getUrl, keyId = viewModel.keyId, businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
                calculateZZJYJL(adapter, it)
            }
        }
    }

    private fun calculateZZJYJL(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "sceneFlag",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculate(it, bean, adapter)
                            }
                        }
                    })
                    calculate(it, bean, adapter)
                }
            }
        }
    }

    private fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "finalAmt",
                "creditAmtCd",
                "reason",
                "cflCd",
                "zzjyjl",
                -> {
                    if (typeBean.visibility != (bean.valueName != "1")) {
                        typeBean.visibility = bean.valueName != "1"
                        typeBean.requireable = typeBean.visibility
                        adapter.notifyItemChanged(index)
                    }

                }
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), saveUrl, adapter.data, keyId = viewModel.keyId, businessType = businessType) {
            if (it != null) {
                DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = viewModel.keyId, businessType = viewModel.businessType, type = when (viewModel.businessType) {
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
                    -> "2"
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
                    -> "3"
                    ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
                    -> "4"
                    ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
                    ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
                    ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
                    -> "5"
                    else -> ""
                }) { configurationBean ->
                    if (configurationBean != null) {
                        ProcessProcessingPop(context, configurationBean, keyId = viewModel.keyId, businessType = viewModel.businessType) {
                            refreshData()
                        }.show(childFragmentManager, this.javaClass.name)
                    }
                }
                if (isAdded) (activity as BaseActivity).refreshData()
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