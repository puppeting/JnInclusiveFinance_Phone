package com.inclusive.finance.jh.ui.apply.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.databinding.FragmentReportBinding
import com.inclusive.finance.jh.databinding.ViewstubAjlBinding
import com.inclusive.finance.jh.databinding.ViewstubGxlBinding
import org.jetbrains.anko.support.v4.act

/**
 * 调查报告
 * */
@Deprecated("暂时不用")
class ReportFragment : MyBaseFragment() {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentReportBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentReportBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    private val typeMap = HashMap<String, ArrayList<String>>().apply {
        put("DCBG_AJ", arrayListOf("按揭类-基本信息", "借款人条件及购房情况认定", "首付款支付及房价核实", "还款能力", "抵押物状况", "面谈情况", "调查意见"))
        put("DCBG_GX", arrayListOf("基本信息", "信贷历史", "申请贷款信息", "共同借款人和担保人的信息", "担保人资格检查", "第三方评价", "贷款资格审查", "申请人财务报表", "风险因素分析及调查结论"))
    }
    var titleList: ArrayList<String>? = ArrayList()
    override fun initData() {
        viewModel.title = ""
        DataCtrlClass.ApplyNet.getReportTypeInfo(requireActivity(), viewModel.creditId) {
            if (it != null) {
                titleList = typeMap[it]
                if (titleList?.size ?: 0 > 0) viewModel.title = titleList?.get(0)
                (childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment as MyBaseFragment).refreshData()
                initButton(it)
            }
        }
    }

    private fun initButton(type: String) {
        var chipId = -1
        var chipGroup = ChipGroup(context)
        viewBind.viewStub.viewStub?.layoutResource = when (type) {
            "DCBG_GX" -> R.layout.viewstub_gxl
            "DCBG_AJ" -> R.layout.viewstub_ajl
            else -> R.layout.viewstub_gxl
        }
        viewBind.viewStub.viewStub?.inflate()?.let {
            when (type) {
                "DCBG_GX" -> DataBindingUtil.bind<ViewstubGxlBinding>(it)?.apply {
                    data = viewModel
                    chipGroup = root as ChipGroup
                }
                "DCBG_AJ" -> DataBindingUtil.bind<ViewstubAjlBinding>(it)?.apply {
                    data = viewModel
                    chipGroup = root as ChipGroup
                }

                else -> DataBindingUtil.bind<ViewstubGxlBinding>(it)?.apply {
                    data = viewModel
                    chipGroup = root as ChipGroup
                }
            }
        }
        if (chipGroup.childCount > 0) {
            chipId = chipGroup[0].id
            chipGroup.check(chipGroup[0].id)
        }
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            //            获取当前展示的fragment
            val fragment = childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
            var isShowingFragment = false //是否是当前显示的fragment。
            //如果重复点击。就刷新数据，保留选择状态
            if (checkedId != -1) chipId = checkedId
            else {
                group.check(chipId)
                return@setOnCheckedChangeListener
            }
            val findNavController = Navigation.findNavController(act, R.id.apply_nav_host_fragment_report)

            val title = group.findViewById<Chip>(checkedId).text.toString()
            viewModel.title = title
            val fragmentId = when (title) {
                "基本信息",
                "信贷历史",
                "申请贷款信息",
                "共同借款人和担保人的信息",
                "担保人资格检查",
                "第三方评价",
                "贷款资格审查",
                "申请人财务报表",
                "风险因素分析及调查结论",
                "按揭类-基本信息",
                "借款人条件及购房情况认定",
                "首付款支付及房价核实",
                "还款能力",
                "抵押物状况",
                "面谈情况",
                "调查意见",
                -> {
                    isShowingFragment = fragment is ReportBaseFragment
                    R.id.reportBaseFragment
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