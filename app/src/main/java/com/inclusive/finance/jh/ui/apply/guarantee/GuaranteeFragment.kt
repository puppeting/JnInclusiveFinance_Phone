package com.inclusive.finance.jh.ui.apply.guarantee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.databinding.FragmentGuaranteeBinding
import org.jetbrains.anko.support.v4.act

/**
 * 担保信息
 * */
class GuaranteeFragment : MyBaseFragment() {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGuaranteeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentGuaranteeBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewModel.title = "自然人担保"
        var chipId = R.id.bt_personal
        viewBind.chipGroup.setOnCheckedChangeListener { group, checkedId -> //            获取当前展示的fragment
            val fragment = childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
            var isShowingFragment = false //是否是当前显示的fragment。 //如果重复点击。就刷新数据，保留选择状态
            if (checkedId != -1) chipId = checkedId
            else {
                group.check(chipId)
                return@setOnCheckedChangeListener
            }
            val findNavController = Navigation.findNavController(act, R.id.apply_nav_host_fragment_guarantee)
            val fragmentId = when (checkedId) {
                R.id.bt_personal -> {
                    viewModel.title = "自然人担保"
                    isShowingFragment = fragment is GuaranteePersonalFragment
                    R.id.guaranteePersonalFragment
                }
                R.id.bt_house -> {
                    viewModel.title = "房产抵押"
                    isShowingFragment = fragment is GuaranteeBetFragment
                    R.id.guaranteeBetFragment
                }
                R.id.bt_depositReceipt -> {
                    viewModel.title = "存单质押"
                    isShowingFragment = fragment is GuaranteeBetFragment
                    R.id.guaranteeBetFragment
                }
                R.id.bt_country -> {
                    viewModel.title = "国债质押"
                    isShowingFragment = fragment is GuaranteeBetFragment
                    R.id.guaranteeBetFragment
                }
                R.id.bt_company -> {
                    viewModel.title = "担保公司担保"
                    isShowingFragment = fragment is GuaranteePersonalFragment
                    R.id.guaranteePersonalFragment
                }
                R.id.bt_enterprise -> {
                    viewModel.title = "企业担保"
                    isShowingFragment = fragment is GuaranteePersonalFragment
                    R.id.guaranteePersonalFragment
                }

                R.id.bt_other -> {
                    viewModel.title = "其他担保"
                    isShowingFragment = fragment is GuaranteeOtherFragment
                    R.id.guaranteeOtherFragment
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