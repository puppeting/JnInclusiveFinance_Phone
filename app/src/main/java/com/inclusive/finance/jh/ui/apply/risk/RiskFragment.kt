package com.inclusive.finance.jh.ui.apply.risk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.*
import com.inclusive.finance.jh.ui.NavActivity
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 风险探测
 * */
class RiskFragment : MyBaseFragment() {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentRiskBinding

    var getUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentRiskBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        if (act is NavActivity) {
            initNavGraph()
        } else {
            when (viewModel.businessType) {
                ApplyModel.BUSINESS_TYPE_APPLY,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE, -> {
                    getUrl = Urls.getRiskTab
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
                    getUrl = Urls.get_jnj_cj_personal_fxtc_tab
                }
                ApplyModel.BUSINESS_TYPE_VISIT_NEW,
                ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
                ApplyModel.BUSINESS_TYPE_PRECREDIT,
                -> {
                    getUrl = Urls.get_visit_fxtc_tab
                }
                ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                -> {
                    getUrl = Urls.get_sunshine_fxtc_tab
                }
            }
            businessType = SZWUtils.getBusinessType(viewModel.businessType)
            DataCtrlClass.ApplyNet.getRiskTypeInfo(context, getUrl, keyId = viewModel.keyId, businessType = businessType) {
                if (it != null) {
                    if (it.size > 0) viewModel.jsonObject = Gson().toJson(it[0])
                    initNavGraph()
                    initButton(it)
                }
            }
        }
    }

    private fun initNavGraph() {
        val navHostFragment: NavHostFragment = childFragmentManager.findFragmentById(R.id.apply_nav_host_fragment_risk) as NavHostFragment
        val navGraph: NavGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph_risk)
        navGraph.setStartDestination(R.id.riskBaseFragment)
        navHostFragment.navController.graph = navGraph
    }

    private fun initButton(arrayList: ArrayList<JsonObject>) {
        var chipId = -1
        viewBind.chipGroup.removeAllViews()
        arrayList.forEach {
            viewBind.chipGroup.addView(DataBindingUtil.inflate<ViewChipRiskBinding>(LayoutInflater.from(context), R.layout.view_chip_risk, null, false)
                .apply {
                    data = SZWUtils.getJsonObjectString(it, "tabName")
                }.root.apply {
                    id = View.generateViewId()
                })
        }
        if (viewBind.chipGroup.childCount > 0) {
            chipId = viewBind.chipGroup[0].id
            viewBind.chipGroup.check(viewBind.chipGroup[0].id)
        }
        viewBind.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            //            获取当前展示的fragment
            val fragment = childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
            var isShowingFragment = false //是否是当前显示的fragment。
            //如果重复点击。就刷新数据，保留选择状态
            if (checkedId != -1) chipId = checkedId
            else {
                group.check(chipId)
                return@setOnCheckedChangeListener
            }
            val findNavController = Navigation.findNavController(act, R.id.apply_nav_host_fragment_risk)

            val title = group.findViewById<Chip>(checkedId).text.toString()
            viewModel.title = title
            val fragmentId = when (title) {
                "申请人" -> {
                    val firstOrNull = arrayList.firstOrNull { SZWUtils.getJsonObjectString(it, "tabName") == "申请人" }
                    viewModel.jsonObject = Gson().toJson(firstOrNull)
                    isShowingFragment = fragment is RiskBaseFragment
                    R.id.riskBaseFragment
                }
                "共同借款人",
                -> {
                    val firstOrNull = arrayList.firstOrNull { SZWUtils.getJsonObjectString(it, "tabName") == "共同借款人" }
                    viewModel.jsonObject = Gson().toJson(firstOrNull)
                    isShowingFragment = fragment is RiskBaseFragment
                    R.id.riskBaseFragment
                }
                else -> {
                    null
                }
            }
            if (fragmentId != null) {
                val build = NavOptions.Builder().setPopUpTo(fragmentId, true).build()
                if (isShowingFragment) (fragment as MyBaseFragment).refreshData()
                else findNavController.navigate(fragmentId, null, build)
            }
        }
    }

}