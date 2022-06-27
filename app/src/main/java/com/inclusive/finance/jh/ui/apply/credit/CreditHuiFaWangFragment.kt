package com.inclusive.finance.jh.ui.apply.credit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonParser
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCerditHuifawangBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

class CreditHuiFaWangFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCerditHuifawangBinding

    var getUrl=""
    var businessType=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCerditHuifawangBinding.inflate(inflater, container, false).apply {
                presenterClick = this@CreditHuiFaWangFragment
                viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
                data = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this)
        adapter.keyId = viewModel.keyId
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter=adapter
        adapter.setEmptyView(View.inflate(context, R.layout.empty_view, null).apply {
            findViewById<TextView>(R.id.empty_text).text = "此客户无司法信息"
        })

    }

    override fun refreshData(type: Int?) {
        initData()
    }

    override fun initData() {
        //        val mainData = SZWUtils.getJson(context, "准入信息汇法网查询.json")
        //        val it = Gson().fromJson<MutableList<BaseTypeBean>>(mainData, object :
        //            TypeToken<ArrayList<BaseTypeBean>>() {}.type)
        //        SZWUtils.setSeeOnlyMode(viewModel, it)
        //
        //        adapter.setNewInstance(it)

        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            -> {
                getUrl= Urls.getRiskHFW
            }
            ApplyModel.BUSINESS_TYPE_JNJ_YX,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            -> {
                getUrl= Urls.get_jnj_cj_personal_fxtc_hfw
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY -> {
                getUrl= when {
                    SZWUtils.getJsonObjectString(JsonParser.parseString(viewModel.jsonObject).asJsonObject,"type")=="CompanyInfoCollectFragment" -> Urls.get_jnj_cj_company_qyxxcj_hfw
                    else -> Urls.get_jnj_cj_personal_fxtc_hfw
                }
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl= Urls.get_visit_fxtc_hfw
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getUrl= Urls.get_sunshine_fxtc_hfw
            }
        }
        businessType=SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.getRiskHFW(requireActivity(), getUrl,viewModel.creditId, viewModel.jsonObject,dhId = viewModel.dhId,businessType = businessType) { it, msg ->
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
                if (isAdded)
                (activity as BaseActivity).refreshData()
            } else {
                adapter.emptyLayout?.findViewById<TextView>(R.id.empty_text)?.text = msg
            }
        }
    }


    override fun onClick(v: View?) {
        when (v) {
//            viewBind.btSearch -> {
//                DataCtrlClass.ApplyNet.getAdmitHuiFaWangInfoSearch(context, viewModel.creditId, js, viewModel.dbrid) {
//                    refreshData()
//                }
//            }
            viewBind.btRefresh -> {
                refreshData()
            }
        }
    }
}