package com.inclusive.finance.jh.ui.apply.risk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentRiskBaseBinding
import com.inclusive.finance.jh.databinding.HeaderRiskBaseBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.textColor

/**
 * 风险探测
 * */
class RiskBaseFragment : MyBaseFragment(), PresenterClick {
    lateinit var mAdapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentRiskBaseBinding
    lateinit var headerViewBind: HeaderRiskBaseBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1

    var getUrl = ""
    var saveUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentRiskBaseBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            presenterClick = this@RiskBaseFragment
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseTypeAdapter(this@RiskBaseFragment)
        headerViewBind=HeaderRiskBaseBinding.bind(View.inflate(requireContext(),R.layout.header_risk_base,null))
        mAdapter.addHeaderView(headerViewBind.root)
        viewBind.mRecyclerView.adapter = mAdapter
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        mAdapter.textListItemConfig = { parentItem, parentPosition, sonItem, sonPosition ->
            ItemBaseListCardAdapter.TextListItemConfig().apply {
                if (SZWUtils.getJsonObjectString(sonItem, "item3") != "通过" && SZWUtils.getJsonObjectString(sonItem, "item3") != "" && sonItem != null) {
                    textDefaultColor = R.color.colorPrimary
                }
            }
        }
    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            -> {
                getUrl = Urls.getRiskList
                saveUrl = Urls.save_yg_fxtc
            }
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
                getUrl = Urls.get_jnj_cj_personal_fxtc_list
                saveUrl = Urls.save_jnj_fxtc
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            -> {
                viewBind.linearLayout.visibility = View.GONE
                getUrl = Urls.get_visit_fxtc_list
                saveUrl = Urls.save_visit_fxtc_list
            }
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl = Urls.get_visit_fxtc_list
                saveUrl = Urls.save_visit_fxtc_list
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getUrl = Urls.get_sunshine_fxtc_list
                saveUrl = Urls.save_sunshine_fxtc_list
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.SXSQNet.getRiskList(requireActivity(), getUrl, keyId = viewModel.keyId, parm = viewModel.jsonObject, businessType = businessType) {
            if (it != null) {

                if (it.size > 1) {
                    headerViewBind.bean = it[0]
                    val data = arrayListOf<BaseTypeBean>()
                    it.forEachIndexed { index, jsonObject ->
                        if (index > 0) {
                            data.add(Gson().fromJson(jsonObject, BaseTypeBean::class.java))
                        }
                    }
                    SZWUtils.setSeeOnlyMode(viewModel, data)
                    mAdapter.setNewInstance(data)
                    if (isAdded) (activity as BaseActivity).refreshData()
                    if (isAdded) headerViewBind.tvResult.textColor = ContextCompat.getColor(requireContext(), if (SZWUtils.getJsonObjectString(it[0], "detectResult") == "不通过") R.color.Red else R.color.Green)
                }


            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                val xxsm = mAdapter.data.firstOrNull { item -> item.dataKey == "xxsm" && item.valueName.isNotEmpty() }?.valueName
                if (SZWUtils.getJsonObjectString(headerViewBind.bean, "detectResult") == "不通过" && xxsm.isNullOrEmpty()) {
                    SZWUtils.showSnakeBarError("请填写详细说明")
                    return
                }
                DataCtrlClass.JNJNet.saveJNJfxtc(context, saveUrl, keyId = viewModel.keyId, SZWUtils.getJsonObjectString(headerViewBind.bean, "idenNo"), xxsm) {
                    refreshData()
                    (activity as BaseActivity).refreshData()
                }
            }
            else -> {
            }
        }
    }


}