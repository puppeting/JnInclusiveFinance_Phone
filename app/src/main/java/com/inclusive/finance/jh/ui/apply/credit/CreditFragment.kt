package com.inclusive.finance.jh.ui.apply.credit

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
import com.inclusive.finance.jh.databinding.FragmentCreditBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import org.jetbrains.anko.support.v4.act

/**
 * 征信管理
 * */
class CreditFragment : MyBaseFragment(), PresenterClick {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCreditBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            presenterClick=this@CreditFragment
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewModel.title = "征信管理"
        var chipId = R.id.bt_personal
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
            val findNavController = Navigation.findNavController(act, R.id.apply_nav_host_fragment_credit)
            val fragmentId = when (checkedId) {
                R.id.bt_face -> {
                    viewModel.title = "人脸识别"
                    isShowingFragment = fragment is CreditFaceFragment
                    R.id.creditFaceFragment
                }
                R.id.bt_certificate -> {
                    viewModel.title = "证件上传"
                    isShowingFragment = fragment is CreditAuthorizationFragment
                    R.id.creditAuthorizationFragment
                }
                R.id.bt_creditAuthorization -> {
                    viewModel.title = "征信授权书"
                    isShowingFragment = fragment is CreditAuthorizationFragment
                    R.id.creditAuthorizationFragment
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

    override fun onClick(v: View?) {

    }
}