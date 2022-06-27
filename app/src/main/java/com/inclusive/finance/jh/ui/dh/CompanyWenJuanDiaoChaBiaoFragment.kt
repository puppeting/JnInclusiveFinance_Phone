package com.inclusive.finance.jh.ui.dh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.*
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 企业问券调查表
 * */
class CompanyWenJuanDiaoChaBiaoFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCompenyWenquandiaochaBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCompenyWenquandiaochaBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CompanyWenJuanDiaoChaBiaoFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data=viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@CompanyWenJuanDiaoChaBiaoFragment)
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
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL -> {
                getUrl=Urls.get_jnj_jc_onSite_wqdc
                saveUrl=Urls.save_jnj_jc_onSite_wqdc
//                businessType="03"
            }
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY -> {
                getUrl=Urls.get_jnj_jc_onSite_wqdc
                saveUrl=Urls.save_jnj_jc_onSite_wqdc
//                businessType="03"
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(),getUrl, keyId = viewModel.keyId,businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel,it)
                adapter.setNewInstance(it)
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), saveUrl, adapter.data, keyId = viewModel.keyId,businessType=businessType) {
            if (it != null) {
                if (isAdded)
                (activity as BaseActivity).refreshData()
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