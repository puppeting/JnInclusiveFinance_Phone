package com.inclusive.finance.jh.ui.dh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonParser
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCompanyInfoCollectBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 企业信息采集
 *
 * */
class CompanyInfoCollectFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCompanyInfoCollectBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCompanyInfoCollectBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CompanyInfoCollectFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data=viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@CompanyInfoCollectFragment)
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
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL, -> {
                getUrl=Urls.get_jnj_cj_personal_qyxxcj
                saveUrl=Urls.save_jnj_cj_personal_qyxxcj
                businessType="03"
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
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId=viewModel.keyId,businessType=businessType) {
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
            viewBind.btHfw -> {
                IRouter.goF(v, R.id.action_to_navActivity, "司法查询", viewModel.dhId, JsonParser.parseString("{\"type\":\"CompanyInfoCollectFragment\"}").asJsonObject, viewModel.businessType, viewModel.seeOnly)
            }
        }
    }

}